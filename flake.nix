{
  description = "SAE Java 2026 — JavaFX + MariaDB + Maven + JDBC";

  inputs = {
    nixpkgs.url     = "github:NixOS/nixpkgs/nixos-24.11";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;
          config.allowUnfree = false;
        };

        jdk    = pkgs.jdk21;
        javafx = pkgs.openjfx21;

      in {
        devShells.default = pkgs.mkShell {

          packages = with pkgs; [
            jdk
            maven
            mariadb
            mariadb-connector-java
            glib
            gtk3
            libGL
            xorg.libX11
            xorg.libXext
            xorg.libXrender
            xorg.libXtst
            freetype
            fontconfig
          ];

          shellHook = ''
            export JAVA_HOME="${jdk}"
            export JAVAFX_HOME="${javafx}"
            export JAVAFX_MODULES="javafx.controls,javafx.fxml,javafx.base,javafx.graphics"
            export MARIADB_JDBC_JAR="${pkgs.mariadb-connector-java}/share/java/mariadb-connector-java.jar"

            echo ""
            echo "╔══════════════════════════════════════════════════╗"
            echo "║       SAE Java 2026 — Dev Environment            ║"
            echo "╠══════════════════════════════════════════════════╣"
            echo "║  Java    : $(java -version 2>&1 | head -1)"
            echo "║  Maven   : $(mvn -version 2>&1 | head -1)"
            echo "║  MariaDB : $(mariadbd --version 2>&1 | head -1)"
            echo "╠══════════════════════════════════════════════════╣"
            echo "║  make help   → liste des commandes               ║"
            echo "╚══════════════════════════════════════════════════╝"
            echo ""
          '';
        };
      }
    );
}
