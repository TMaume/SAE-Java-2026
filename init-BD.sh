#!/usr/bin/env bash
echo "🧱 Démarrage du conteneur MariaDB..."
docker compose up -d

echo "⏳ Attente du démarrage complet de MariaDB..."
until mariadb -h 127.0.0.1 -P 3306 -u o22403450 -po22403450 --silent -e "SELECT 1;" 2>/dev/null; do
    echo "   ... pas encore prêt, on attend 2s"
    sleep 2
done
echo "✅ MariaDB est prêt !"

echo "📂 Déplacement dans le dossier BD..."
cd BD || exit 1

echo "🏗️  Création de la structure (creation_lego.sql)..."
mariadb -h 127.0.0.1 -P 3306 -u o22403450 -po22403450 --local-infile=1 dbo22403450 < creation_lego.sql

echo "💉 Injection des données depuis les CSV (jeuessai_lego.sql)..."
mariadb -h 127.0.0.1 -P 3306 -u o22403450 -po22403450 --local-infile=1 dbo22403450 < jeuessai_lego.sql

echo "🔙 Retour à la racine..."
cd ..
echo "✅ Base de données réinitialisée et prête !"