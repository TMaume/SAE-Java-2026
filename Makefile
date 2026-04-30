# ============================================================
#  SAE Java 2026 — Makefile
#  Prérequis : être dans l'env devenv (nix develop / direnv)
# ============================================================

PROJECT_DIR  := sae
BD_DIR       := BD
TARGET       := $(PROJECT_DIR)/target

DB_HOST      ?= 127.0.0.1
DB_PORT      ?= 3306
DB_NAME      ?= lego_db
DB_USER      ?= sae_user
DB_PASS      ?= sae_pass
DB_URL       := jdbc:mariadb://$(DB_HOST):$(DB_PORT)/$(DB_NAME)

JAVAFX_MODULES ?= javafx.controls,javafx.fxml,javafx.base,javafx.graphics
MAIN_CLASS     ?= MainApp
JAR_FILE       := $(TARGET)/sae-1.0.0-jar-with-dependencies.jar

# ============================================================
.PHONY: help all build run run-mvn test clean \
        db-start db-stop db-status \
        db-create db-drop db-reset db-import \
        db-shell deps install-jdbc check-env

all: build

# ------------------------------------------------------------
# help
# ------------------------------------------------------------
help:
	@printf "\n"
	@printf "\033[1m\033[36mSAE Java 2026 — commandes disponibles\033[0m\n"
	@printf "\033[36m══════════════════════════════════════════════\033[0m\n"
	@printf "\n"
	@printf "\033[1m── BUILD & EXECUTION ──────────────────────────\033[0m\n"
	@printf "  \033[32mmake build\033[0m          Compile le projet (mvn package)\n"
	@printf "  \033[32mmake run\033[0m            Lance l'application JavaFX (JAR)\n"
	@printf "  \033[32mmake run-mvn\033[0m        Lance via mvn javafx:run (dev)\n"
	@printf "  \033[32mmake test\033[0m           Execute les tests JUnit\n"
	@printf "  \033[32mmake clean\033[0m          Supprime target/\n"
	@printf "  \033[32mmake deps\033[0m           Telecharge les dependances Maven\n"
	@printf "\n"
	@printf "\033[1m── BASE DE DONNEES ─────────────────────────────\033[0m\n"
	@printf "  \033[33mmake db-start\033[0m       Demarre MariaDB\n"
	@printf "  \033[33mmake db-stop\033[0m        Arrete MariaDB\n"
	@printf "  \033[33mmake db-status\033[0m      Verifie l'etat du serveur\n"
	@printf "  \033[33mmake db-create\033[0m      Cree le schema (creation_lego.sql)\n"
	@printf "  \033[33mmake db-drop\033[0m        Supprime les tables (destruction_lego.sql)\n"
	@printf "  \033[33mmake db-reset\033[0m       Drop + Create + Import CSV\n"
	@printf "  \033[33mmake db-import\033[0m      Importe tous les fichiers CSV\n"
	@printf "  \033[33mmake db-shell\033[0m       Shell MariaDB interactif\n"
	@printf "\n"
	@printf "\033[1m── UTILITAIRES ─────────────────────────────────\033[0m\n"
	@printf "  \033[36mmake install-jdbc\033[0m   Installe le JAR JDBC dans ~/.m2\n"
	@printf "  \033[36mmake check-env\033[0m      Verifie que l'environnement est OK\n"
	@printf "\n"
	@printf "\033[1m── VARIABLES SURCHARGEABLES ────────────────────\033[0m\n"
	@printf "  DB_HOST=$(DB_HOST)  DB_PORT=$(DB_PORT)\n"
	@printf "  DB_NAME=$(DB_NAME)  DB_USER=$(DB_USER)  DB_PASS=***\n"
	@printf "\n"

# ============================================================
# BUILD
# ============================================================

build: check-env
	@printf "\033[1m\033[32m>> Compilation Maven...\033[0m\n"
	cd $(PROJECT_DIR) && mvn -B package -DskipTests \
	    -Djavafx.modules=$(JAVAFX_MODULES)
	@printf "\033[32mOK Build termine : $(JAR_FILE)\033[0m\n"

run: $(JAR_FILE)
	@printf "\033[1m\033[32m>> Lancement JavaFX...\033[0m\n"
	java \
	  --module-path "$(JAVAFX_HOME)/lib" \
	  --add-modules $(JAVAFX_MODULES) \
	  -Ddb.url="$(DB_URL)" \
	  -Ddb.user="$(DB_USER)" \
	  -Ddb.password="$(DB_PASS)" \
	  -jar $(JAR_FILE)

run-mvn: check-env
	@printf "\033[1m\033[32m>> Lancement via mvn javafx:run...\033[0m\n"
	cd $(PROJECT_DIR) && mvn -B javafx:run \
	  -Ddb.url="$(DB_URL)" \
	  -Ddb.user="$(DB_USER)" \
	  -Ddb.password="$(DB_PASS)"

test: check-env
	@printf "\033[1m\033[32m>> Tests JUnit...\033[0m\n"
	cd $(PROJECT_DIR) && mvn -B test \
	    -Djavafx.modules=$(JAVAFX_MODULES) \
	    -Ddb.url="$(DB_URL)" \
	    -Ddb.user="$(DB_USER)" \
	    -Ddb.password="$(DB_PASS)"

clean:
	@printf "\033[31m>> Nettoyage...\033[0m\n"
	cd $(PROJECT_DIR) && mvn -B clean
	@printf "\033[32mOK target/ supprime\033[0m\n"

deps:
	@printf "\033[36m>> Resolution des dependances...\033[0m\n"
	cd $(PROJECT_DIR) && mvn -B dependency:resolve

# ============================================================
# BASE DE DONNEES
# ============================================================

db-start:
	@printf "\033[1m\033[33m>> Demarrage MariaDB...\033[0m\n"
	devenv up -d 2>/dev/null || \
	  mysqld_safe --datadir="$${DEVENV_STATE:-/tmp/devenv}/mysql" \
	              --socket="$${DEVENV_STATE:-/tmp/devenv}/mysql/mysql.sock" &
	@sleep 2
	@$(MAKE) db-status

db-stop:
	@printf "\033[33m>> Arret MariaDB...\033[0m\n"
	mysqladmin -h $(DB_HOST) -P $(DB_PORT) -u root shutdown 2>/dev/null || \
	  pkill -f mysqld || true

db-status:
	@printf "\033[36m>> Etat MariaDB :\033[0m\n"
	@mysqladmin -h $(DB_HOST) -P $(DB_PORT) \
	            -u $(DB_USER) -p$(DB_PASS) status 2>/dev/null \
	  && printf "\033[32mOK Serveur actif\033[0m\n" \
	  || printf "\033[31mKO Serveur inaccessible\033[0m\n"

db-create:
	@printf "\033[1m\033[33m>> Creation du schema (creation_lego.sql)...\033[0m\n"
	mysql -h $(DB_HOST) -P $(DB_PORT) \
	      -u $(DB_USER) -p$(DB_PASS) \
	      $(DB_NAME) < $(BD_DIR)/creation_lego.sql
	@printf "\033[32mOK Schema cree\033[0m\n"

db-drop:
	@printf "\033[31m>> Suppression des tables (destruction_lego.sql)...\033[0m\n"
	mysql -h $(DB_HOST) -P $(DB_PORT) \
	      -u $(DB_USER) -p$(DB_PASS) \
	      $(DB_NAME) < $(BD_DIR)/destruction_lego.sql
	@printf "\033[33mOK Tables supprimees\033[0m\n"

db-import:
	@printf "\033[1m\033[33m>> Import des fichiers CSV...\033[0m\n"
	@for csv in $(BD_DIR)/*.csv; do \
	  table=$$(basename $$csv .csv); \
	  printf "  -> Import $$table depuis $$csv\n"; \
	  mysql -h $(DB_HOST) -P $(DB_PORT) \
	        -u $(DB_USER) -p$(DB_PASS) \
	        --local-infile=1 \
	        $(DB_NAME) -e \
	    "LOAD DATA LOCAL INFILE '$$csv' \
	     INTO TABLE \`$$table\` \
	     FIELDS TERMINATED BY ',' \
	     OPTIONALLY ENCLOSED BY '\"' \
	     LINES TERMINATED BY '\n' \
	     IGNORE 1 ROWS;"; \
	done
	@printf "\033[32mOK Import CSV termine\033[0m\n"

db-reset: db-drop db-create db-import
	@printf "\033[32mOK Base reinitialisee\033[0m\n"

db-shell:
	@printf "\033[36m>> Connexion a $(DB_NAME)...\033[0m\n"
	mysql -h $(DB_HOST) -P $(DB_PORT) \
	      -u $(DB_USER) -p$(DB_PASS) \
	      $(DB_NAME)

# ============================================================
# UTILITAIRES
# ============================================================

install-jdbc:
	@printf "\033[36m>> Installation du JAR JDBC dans ~/.m2...\033[0m\n"
	@test -n "$(MARIADB_JDBC_JAR)" || \
	  (printf "\033[31mKO MARIADB_JDBC_JAR non defini. Lancer depuis nix develop\033[0m\n" && exit 1)
	mvn install:install-file \
	  -Dfile="$(MARIADB_JDBC_JAR)" \
	  -DgroupId=org.mariadb.jdbc \
	  -DartifactId=mariadb-java-client \
	  -Dversion=3.3.3 \
	  -Dpackaging=jar
	@printf "\033[32mOK JAR installe\033[0m\n"

check-env:
	@command -v java  >/dev/null 2>&1 || (printf "\033[31mKO java introuvable\033[0m\n"  && exit 1)
	@command -v mvn   >/dev/null 2>&1 || (printf "\033[31mKO mvn introuvable\033[0m\n"   && exit 1)
	@command -v mysql >/dev/null 2>&1 || (printf "\033[31mKO mysql introuvable\033[0m\n" && exit 1)
	@test -n "$(JAVAFX_HOME)" || \
	  (printf "\033[31mKO JAVAFX_HOME non defini. Lancer depuis nix develop\033[0m\n" && exit 1)

$(JAR_FILE):
	$(MAKE) build