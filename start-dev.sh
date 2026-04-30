#!/bin/bash

# 1. Charger les variables d'environnement du fichier .env
if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
    echo "✅ Clés API et configurations chargées depuis le fichier .env"
else
    echo "⚠️ Attention : Fichier .env non trouvé."
fi

# 2. Nettoyage des anciens conteneurs pour éviter les conflits
echo "🧹 Nettoyage des anciens conteneurs iaeval..."
docker rm -f iaeval-postgresql iaeval-keycloak iaeval-qdrant iaeval-app 2>/dev/null

# 3. Détection de Docker Compose
if docker compose version > /dev/null 2>&1; then
    DOCKER_CMD="docker compose"
else
    DOCKER_CMD="docker-compose"
fi

# 4. Lancement de l'infrastructure en arrière-plan (-d)
echo "🚀 Démarrage de l'infrastructure (Postgres, Keycloak, Qdrant)..."
$DOCKER_CMD -f docker-compose-infra.yml up -d

if [ $? -ne 0 ]; then
    echo "❌ Erreur lors du lancement de Docker."
    exit 1
fi

# 5. Boucle d'attente pour Keycloak
echo "⏳ Attente du démarrage complet de Keycloak (environ 45s)..."
count=0
until curl -s http://127.0.0.1:9080/realms/jhipster > /dev/null; do
  count=$((count+5))
  echo "En attente depuis ${count}s..."
  sleep 5
  if [ $count -gt 120 ]; then
    echo "❌ Keycloak met trop de temps à démarrer. Vérifiez les logs avec 'docker logs iaeval-keycloak'."
    exit 1
  fi
done

echo "✅ L'infrastructure est prête !"

# 6. LANCEMENT DE L'APPLICATION JAVA
echo "☕ Lancement de l'application iaeval (Profil: DEV)..."
echo "-------------------------------------------------------"
# On utilise ./mvnw pour lancer l'app. 
# Le terminal restera ouvert sur les logs de l'application.
echo "☕ On compile et lance l'application Java..."
./mvnw compile -DskipTests
./mvnw spring-boot:run -Dspring.profiles.active=dev -DskipTests