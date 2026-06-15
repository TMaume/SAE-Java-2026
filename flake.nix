{
  description = "Environnement Java + JavaFX + MariaDB JDBC";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;
          # Nécessaire pour JavaFX (licence Oracle acceptée)
          config.allowUnfree = true;
        };

        # ── JDK avec JavaFX intégré ──────────────────────────────────────────
        # Option 1 (recommandée) : JDK Temurin + OpenJFX séparé
        jdk = pkgs.jdk21;
        javafx = pkgs.openjfx21;

        # Jar du connecteur JDBC MariaDB (téléchargé via Maven Central)
        mariadbJdbcVersion = "3.3.3";
        mariadbJdbcJar = pkgs.fetchurl {
          url = "https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/${mariadbJdbcVersion}/mariadb-java-client-${mariadbJdbcVersion}.jar";
          sha256 = "sha256-ueWlUmCKTubnJ5GDvUT+nQdhxFBm91bXr2e8q4Dc5g0=";
        };

        # Script helper pour lancer une app JavaFX facilement
        runJavaFX = pkgs.writeShellScriptBin "run-javafx" ''
          exec ${jdk}/bin/java \
            --module-path ${javafx}/lib \
            --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.web \
            -cp "./*:${mariadbJdbcJar}" \
            "$@"
        '';

      in {
        # ── Shell de développement ──────────────────────────────────────────
        devShells.default = pkgs.mkShell {
          name = "java-javafx-mariadb";

          packages = [
            jdk
            javafx
            pkgs.maven       # mvn   – build Maven
            pkgs.gradle      # gradle – build Gradle (optionnel)
            pkgs.mariadb     # serveur MariaDB local
            pkgs.mariadb.client # client CLI mysql / mariadb
            runJavaFX
          ];

          # Variables d'environnement
          shellHook = ''
            echo ""
            echo "╔══════════════════════════════════════════════╗"
            echo "║  Environnement Java + JavaFX + MariaDB JDBC  ║"
            echo "╚══════════════════════════════════════════════╝"
            echo ""
            echo "  Java    : $(java -version 2>&1 | head -1)"
            echo "  Maven   : $(mvn -v 2>/dev/null | head -1)"
            echo "  MariaDB : $(mariadbd --version 2>/dev/null | head -1)"
            echo ""
            echo "  Jar JDBC MariaDB ${mariadbJdbcVersion} : ${mariadbJdbcJar}"
            echo ""
            echo "  ▶  Lancer votre app JavaFX :"
            echo "     run-javafx com.example.Main"
            echo ""
            echo "  ▶  Démarrer MariaDB en local :"
            echo "     mysql_install_db --user=$USER --datadir=./data"
            echo "     mysqld --datadir=./data --socket=./mariadb.sock &"
            echo "     mysql -u root --socket=./mariadb.sock"
            echo ""

            # Module path JavaFX exposé pour Maven/Gradle
            export JAVAFX_HOME="${javafx}"
            export PATH_TO_FX="${javafx}/lib"

            # Classpath JDBC disponible pour les compilations manuelles
            export MARIADB_JDBC_JAR="${mariadbJdbcJar}"

            # Java sur le PATH (sécurité)
            export JAVA_HOME="${jdk}"
          '';
        };

        # ── Packages exportés ───────────────────────────────────────────────
        packages = {
          inherit runJavaFX;
          default = runJavaFX;
        };
      }
    );
}
