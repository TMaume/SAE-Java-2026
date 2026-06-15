#!/usr/bin/env bash

echo "🧱 Démarrage du conteneur MariaDB..."
docker compose up -d

echo "⏳ Attente du démarrage complet de la base (5 secondes)..."
sleep 5

echo "📂 Déplacement dans le dossier BD..."
# Le '|| exit 1' coupe le script si le dossier BD n'existe pas, pour éviter les catastrophes
cd BD || exit 1 

echo "🏗️  Création de la structure (creation_lego.sql)..."
mariadb -h 127.0.0.1 -P 3306 -u lego_user -plegopassword --local-infile=1 sae_lego < creation_lego.sql

echo "💉 Injection des données depuis les CSV (jeuessai_lego.sql)..."
mariadb -h 127.0.0.1 -P 3306 -u lego_user -plegopassword --local-infile=1 sae_lego < jeuessai_lego.sql

echo "🔙 Retour à la racine..."
cd ..

echo "✅ Base de données réinitialisée et prête à l'emploi !"
echo "🚀 Tu peux lancer l'application avec : mvn clean javafx:run"