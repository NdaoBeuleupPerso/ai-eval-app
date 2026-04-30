#!/bin/bash
echo "🧹 Nettoyage de la production actuelle..."
docker rm -f iaeval-app iaeval-postgresql iaeval-keycloak iaeval-qdrant 2>/dev/null

echo "🏗️  Construction de l'image Docker avec Jib..."
# On saute les tests pour aller plus vite, mais on compile le profil 'prod'
./mvnw package -Pprod verify jib:dockerBuild -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Erreur lors de la construction de l'image Java."
    exit 1
fi

echo "🚀 Lancement de la pile complète (App + Infra)..."
if docker compose version > /dev/null 2>&1; then DOCKER_CMD="docker compose"; else DOCKER_CMD="docker-compose"; fi

$DOCKER_CMD -f docker-compose-prod.yml up -d

echo "✅ Déploiement terminé !"
echo "🌐 L'application sera disponible sur http://localhost:8080"
echo "🔍 Pour voir les logs de l'application : docker logs -f iaeval-app"