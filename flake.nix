{
  description = "Environnement de développement Briqu'IUTO (JavaFX, Maven, JDBC)";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = nixpkgs.legacyPackages.${system};
        jdk = pkgs.openjdk21; 
      in
      {
        devShells.default = pkgs.mkShell {
          buildInputs = [
            jdk
            pkgs.maven
            pkgs.mariadb # Ajout du client MySQL/MariaDB en local
          ];

          shellHook = ''
            export JAVA_HOME="${jdk}"
            
            # Correction des avertissements xorg avec les nouveaux noms de paquets
            export LD_LIBRARY_PATH="${pkgs.lib.makeLibraryPath [
              pkgs.libGL
              pkgs.gtk3
              pkgs.glib
              pkgs.pango
              pkgs.cairo
              pkgs.atk
              pkgs.gdk-pixbuf
              pkgs.libx11
              pkgs.libxtst
              pkgs.libxxf86vm
            ]}:$LD_LIBRARY_PATH"

            echo "🧱 Bienvenue dans l'environnement de dev Briqu'IUTO !"
            echo "☕ Java: $(java -version 2>&1 | head -n 1)"
            echo "🐘 Maven: $(mvn -version | head -n 1)"
            echo "🐬 MariaDB Client: prêt à injecter les données !"
          '';
        };
      });
}