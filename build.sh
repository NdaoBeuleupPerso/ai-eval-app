#!/bin/bash
cd /home/ousmane-ndao/ia-eval-app
echo "🔨 Démarrage du build production..."
./mvnw clean package -Pprod -DskipTests 2>&1 | tee build.log
BUILD_STATUS=$?

if [ $BUILD_STATUS -eq 0 ]; then
  echo "✅ BUILD RÉUSSI!"
  echo "📦 JAR créé: target/iaeval-0.0.1-SNAPSHOT.jar"
  ls -lh target/*.jar 2>/dev/null || echo "JAR non trouvé"
else
  echo "❌ BUILD ÉCHOUÉ (code: $BUILD_STATUS)"
  echo ""
  echo "📋 Dernières erreurs:"
  tail -50 build.log | grep -E "ERROR|error: src|Failed"
fi

exit $BUILD_STATUS
