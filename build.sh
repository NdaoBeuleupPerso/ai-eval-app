#!/bin/bash
set -e

echo "🛑 1. Nettoyage complet..."
# On arrête tout ce qui pourrait utiliser les ports 8080, 9080, 5432
docker compose --env-file .env -f src/main/docker/app.yml down || true

echo "🔨 2. Build de l'image (si nécessaire)..."
# On s'assure que l'image 'iaeval' est à jour
./mvnw package -Pprod jib:dockerBuild -DskipTests -ntp

echo "🚀 3. Lancement de la STACK COMPLÈTE (Prod)..."
# Docker va lire le .env et lancer : App + DB + Keycloak + Qdrant
docker compose --env-file .env -f src/main/docker/app.yml up -d

echo "-------------------------------------------------------"
echo "⏳ L'application va démarrer dans environ 90 secondes..."
echo "   (C'est le temps réglé dans JHIPSTER_SLEEP pour Keycloak)"
echo "-------------------------------------------------------"
echo "📊 Pour suivre la progression : docker logs -f iaeval-app"