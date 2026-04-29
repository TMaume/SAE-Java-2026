# ============================================================
#  SAE Java 2026 — Makefile
#  Prérequis : être dans l'env devenv (nix develop / direnv)
# ============================================================

# ── Répertoires ──────────────────────────────────────────────
PROJECT_DIR  := sae
BD_DIR       := BD
TARGET       := $(PROJECT_DIR)/target

# ── Identifiants MariaDB (surchargeables via l'env ou CLI) ───
DB_HOST      ?= 127.0.0.1
DB_PORT      ?= 3306
DB_NAME      ?= lego_db
DB_USER      ?= sae_user
DB_PASS      ?= sae_pass
DB_URL       := jdbc:mariadb://$(DB_HOST):$(DB_PORT)/$(DB_NAME)

# ── Java / JavaFX ────────────────────────────────────────────
JAVAFX_MODULES ?= javafx.controls,javafx.fxml,javafx.base,javafx.graphics
MAIN_CLASS     ?= MainApp
JAR_FILE       := $(TARGET)/sae-1.0.0-jar-with-dependencies.jar

# ── Couleurs terminal ────────────────────────────────────────
RESET  := \033[0m
BOLD   := \033[1m
GREEN  := \033[32m
YELLOW := \033[33m
CYAN   := \033[36m
RED    := \033[31m

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
	@echo ""
	@echo "$(BOLD)$(CYAN)SAE Java 2026 — commandes disponibles$(RESET)"
	@echo "$(CYAN)══════════════════════════════════════════════$(RESET)"
	@echo ""
	@echo "$(BOLD)── BUILD & EXECUTION ──────────────────────────$(RESET)"
	@echo "  $(GREEN)make build$(RESET)          Compile le projet (mvn package)"
	@echo "  $(GREEN)make run$(RESET)            Lance l'application JavaFX (JAR)"
	@echo "  $(GREEN)make run-mvn$(RESET)        Lance via mvn javafx:run (dev)"
	@echo "  $(GREEN)make test$(RESET)           Execute les tests JUnit"
	@echo "  $(GREEN)make clean$(RESET)          Supprime target/"
	@echo "  $(GREEN)make deps$(RESET)           Telecharge les dependances Maven"
	@echo ""
	@echo "$(BOLD)── BASE DE DONNEES ─────────────────────────────$(RESET)"
	@echo "  $(YELLOW)make db-start$(RESET)       Demarre MariaDB (devenv)"
	@echo "  $(YELLOW)make db-stop$(RESET)        Arrete MariaDB"
	@echo "  $(YELLOW)make db-status$(RESET)      Verifie l'etat du serveur"
	@echo "  $(YELLOW)make db-create$(RESET)      Cree le schema (creation_lego.sql)"
	@echo "  $(YELLOW)make db-drop$(RESET)        Supprime les tables (destruction_lego.sql)"
	@echo "  $(YELLOW)make db-reset$(RESET)       Drop + Create + Import CSV"
	@echo "  $(YELLOW)make db-import$(RESET)      Importe tous les fichiers CSV"
	@echo "  $(YELLOW)make db-shell$(RESET)       Shell MariaDB interactif"
	@echo ""
	@echo "$(BOLD)── UTILITAIRES ─────────────────────────────────$(RESET)"
	@echo "  $(CYAN)make install-jdbc$(RESET)   Installe le JAR JDBC dans ~/.m2"
	@echo "  $(CYAN)make check-env$(RESET)      Verifie que l'environnement est OK"
	@echo ""
	@echo "$(BOLD)── VARIABLES SURCHARGEABLES ────────────────────$(RESET)"
	@echo "  DB_HOST=$(DB_HOST)  DB_PORT=$(DB_PORT)"
	@echo "  DB_NAME=$(DB_NAME)  DB_USER=$(DB_USER)  DB_PASS=***"
	@echo ""

# ============================================================
# BUILD
# ============================================================

build: check-env
	@echo "$(BOLD)$(GREEN)>> Compilation Maven...$(RESET)"
	cd $(PROJECT_DIR) && mvn -B package -DskipTests \
	    -Djavafx.modules=$(JAVAFX_MODULES)
	@echo "$(GREEN)OK Build termine : $(JAR_FILE)$(RESET)"

run: $(JAR_FILE)
	@echo "$(BOLD)$(GREEN)>> Lancement JavaFX...$(RESET)"
	java \
	  --module-path "$(JAVAFX_HOME)/lib" \
	  --add-modules $(JAVAFX_MODULES) \
	  -Ddb.url="$(DB_URL)" \
	  -Ddb.user="$(DB_USER)" \
	  -Ddb.password="$(DB_PASS)" \
	  -jar $(JAR_FILE)

run-mvn: check-env
	@echo "$(BOLD)$(GREEN)>> Lancement via mvn javafx:run...$(RESET)"
	cd $(PROJECT_DIR) && mvn -B javafx:run \
	  -Ddb.url="$(DB_URL)" \
	  -Ddb.user="$(DB_USER)" \
	  -Ddb.password="$(DB_PASS)"

test: check-env
	@echo "$(BOLD)$(GREEN)>> Tests JUnit...$(RESET)"
	cd $(PROJECT_DIR) && mvn -B test \
	    -Djavafx.modules=$(JAVAFX_MODULES) \
	    -Ddb.url="$(DB_URL)" \
	    -Ddb.user="$(DB_USER)" \
	    -Ddb.password="$(DB_PASS)"

clean:
	@echo "$(RED)>> Nettoyage...$(RESET)"
	cd $(PROJECT_DIR) && mvn -B clean
	@echo "$(GREEN)OK target/ supprime$(RESET)"

deps:
	@echo "$(CYAN)>> Resolution des dependances...$(RESET)"
	cd $(PROJECT_DIR) && mvn -B dependency:resolve

# ============================================================
# BASE DE DONNEES
# ============================================================

db-start:
	@echo "$(BOLD)$(YELLOW)>> Demarrage MariaDB...$(RESET)"
	devenv up -d 2>/dev/null || \
	  mysqld_safe --datadir="$${DEVENV_STATE:-/tmp/devenv}/mysql" \
	              --socket="$${DEVENV_STATE:-/tmp/devenv}/mysql/mysql.sock" &
	@sleep 2
	@$(MAKE) db-status

db-stop:
	@echo "$(YELLOW)>> Arret MariaDB...$(RESET)"
	mysqladmin -h $(DB_HOST) -P $(DB_PORT) -u root shutdown 2>/dev/null || \
	  pkill -f mysqld || true

db-status:
	@echo "$(CYAN)>> Etat MariaDB :$(RESET)"
	@mysqladmin -h $(DB_HOST) -P $(DB_PORT) \
	            -u $(DB_USER) -p$(DB_PASS) status 2>/dev/null \
	  && echo "$(GREEN)OK Serveur actif$(RESET)" \
	  || echo "$(RED)KO Serveur inaccessible$(RESET)"

db-create:
	@echo "$(BOLD)$(YELLOW)>> Creation du schema (creation_lego.sql)...$(RESET)"
	mysql -h $(DB_HOST) -P $(DB_PORT) \
	      -u $(DB_USER) -p$(DB_PASS) \
	      $(DB_NAME) < $(BD_DIR)/creation_lego.sql
	@echo "$(GREEN)OK Schema cree$(RESET)"

db-drop:
	@echo "$(RED)>> Suppression des tables (destruction_lego.sql)...$(RESET)"
	mysql -h $(DB_HOST) -P $(DB_PORT) \
	      -u $(DB_USER) -p$(DB_PASS) \
	      $(DB_NAME) < $(BD_DIR)/destruction_lego.sql
	@echo "$(YELLOW)OK Tables supprimees$(RESET)"

db-import:
	@echo "$(BOLD)$(YELLOW)>> Import des fichiers CSV...$(RESET)"
	@for csv in $(BD_DIR)/*.csv; do \
	  table=$$(basename $$csv .csv); \
	  echo "  -> Import $$table depuis $$csv"; \
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
	@echo "$(GREEN)OK Import CSV termine$(RESET)"

db-reset: db-drop db-create db-import
	@echo "$(GREEN)OK Base reinitalisee$(RESET)"

db-shell:
	@echo "$(CYAN)>> Connexion a $(DB_NAME)...$(RESET)"
	mysql -h $(DB_HOST) -P $(DB_PORT) \
	      -u $(DB_USER) -p$(DB_PASS) \
	      $(DB_NAME)

# ============================================================
# UTILITAIRES
# ============================================================

install-jdbc:
	@echo "$(CYAN)>> Installation du JAR JDBC dans ~/.m2...$(RESET)"
	@test -n "$(MARIADB_JDBC_JAR)" || \
	  (echo "$(RED)KO MARIADB_JDBC_JAR non defini. Lancer depuis l'env devenv$(RESET)" && exit 1)
	mvn install:install-file \
	  -Dfile="$(MARIADB_JDBC_JAR)" \
	  -DgroupId=org.mariadb.jdbc \
	  -DartifactId=mariadb-java-client \
	  -Dversion=3.3.3 \
	  -Dpackaging=jar
	@echo "$(GREEN)OK JAR installe$(RESET)"

check-env:
	@command -v java  >/dev/null 2>&1 || (echo "$(RED)KO java introuvable$(RESET)"  && exit 1)
	@command -v mvn   >/dev/null 2>&1 || (echo "$(RED)KO mvn introuvable$(RESET)"   && exit 1)
	@command -v mysql >/dev/null 2>&1 || (echo "$(RED)KO mysql introuvable$(RESET)" && exit 1)
	@test -n "$(JAVAFX_HOME)" || \
	  (echo "$(RED)KO JAVAFX_HOME non defini. Lancer depuis l'env devenv$(RESET)" && exit 1)

$(JAR_FILE):
	$(MAKE) build
