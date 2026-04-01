# Configuration et Intégration - Étapes Suivantes

## 1. Configuration Keycloak pour le Rôle Soumissionnaire

### Créer le Rôle dans Keycloak

```bash
# Via l'interface Admin Console:
1. Accédez à https://{keycloak-url}/admin
2. Allez à Roles
3. Cliquez "Create role"
4. Nom: SOUMISSIONNAIRE
5. Description: "Utilisateur soumissionnaire pour évaluations AI"
6. Cliquez Save
```

### Affecter le Rôle aux Utilisateurs

```bash
# Via l'interface Admin Console:
1. Allez à Users
2. Sélectionnez l'utilisateur
3. Onglet "Role Mappings"
4. Dans "Realm Roles", sélectionnez SOUMISSIONNAIRE
5. Cliquez "Add selected"
```

## 2. Configuration de l'Application Backend

### application.yml

L'application doit être configurée pour mapper les rôles Keycloak:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          # Les rôles Keycloak seront automatiquement mappés
          jwk-set-uri: ${KEYCLOAK_URL}/auth/realms/${KEYCLOAK_REALM}/protocol/openid-connect/certs
```

### Vérifier les Dépendances Maven

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-oauth2-resource-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-oauth2-jose</artifactId>
</dependency>
```

## 3. Implémentation des Endpoints Réels

### Étape 1: Implémenter getAppelsOffresDisponibles()

**Fichier**: `SoumissionnaireEvaluationResource.java`

```java
@GetMapping("/appels-offres")
public ResponseEntity<List<AppelOffreDTO>> getAppelsOffresDisponibles() {
  LOG.debug("REST request to get available AppelsOffres");

  String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("Current user not found"));

  // Récupérer l'utilisateur courant
  Optional<User> user = userRepository.findOneByLogin(currentUserLogin);
  if (user.isEmpty()) {
    return ResponseEntity.notFound().build();
  }

  // TODO: Implémenter la logique pour récupérer les appels d'offres
  // Pour le moment, retour tous les appels ouverts
  // À adapter selon vos règles métier (appels assignés au soumissionnaire, etc.)

  List<AppelOffreDTO> appels = appelOffreQueryService
    .findByCriteria(AppelOffreCriteria.builder().statut(StatutAppel.OUVERT).build(), Pageable.unpaged())
    .getContent()
    .stream()
    .map(appelOffreMapper::toDto)
    .toList();

  return ResponseEntity.ok(appels);
}

```

### Étape 2: Implémenter getDocumentsSoumission()

```java
@GetMapping("/soumissions/{soumissionId}/documents")
public ResponseEntity<Map<String, Object>> getDocumentsSoumission(
  @PathVariable Long soumissionId,
  @RequestParam(required = false) Long appelOffreId
) {
  LOG.debug("REST request to get documents for soumission: {}", soumissionId);

  Optional<SoumissionDTO> soumission = soumissionService.findOne(soumissionId);
  if (soumission.isEmpty()) {
    return ResponseEntity.notFound().build();
  }

  // Valider que l'utilisateur courant a le droit d'accéder
  // à cette soumission ...

  List<DocumentJointDTO> documents = soumission.get().getDocuments() != null
    ? soumission
      .get()
      .getDocuments()
      .stream()
      .filter(doc -> appelOffreId == null || doc.getSoumission().getAppelOffre().getId().equals(appelOffreId))
      .toList()
    : List.of();

  Map<String, Object> response = new HashMap<>();
  response.put("soumissionId", soumissionId);
  response.put("documents", documents);

  return ResponseEntity.ok(response);
}

```

### Étape 3: Implémenter lancerEvaluationAi()

```java
@PostMapping("/evaluations/lancer")
public ResponseEntity<Map<String, Object>> lancerEvaluationAi(@Valid @RequestBody EvaluationRequest request) {
  LOG.debug("REST request to launch AI evaluation: {}", request);

  // Valider la soumission
  Optional<SoumissionDTO> soumission = soumissionService.findOne(request.getSoumissionId());
  if (soumission.isEmpty()) {
    return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "Soumission not found"));
  }

  // Récupérer l'utilisateur courant pour audit
  String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse("UNKNOWN");

  // Créer une entité Evaluation
  EvaluationDTO evaluation = new EvaluationDTO();
  evaluation.setSoumission(soumission.get());
  evaluation.setDateEvaluation(Instant.now());
  evaluation.setEstValidee(false);
  evaluation.setEvaluateur(new UserDTO());
  evaluation.getEvaluateur().setLogin(currentUserLogin);

  // Appeler le service AI avec les documents
  evaluationService.evaluerSoumission(request.getSoumissionId(), request.getDocumentsIds());

  // Sauvegarder et retourner
  EvaluationDTO savedEvaluation = evaluationService.save(evaluation);

  return ResponseEntity.accepted()
    .body(Map.of("evaluationId", savedEvaluation.getId(), "status", "EN_COURS", "message", "Évaluation lancée avec succès"));
}

```

## 4. Modèles de Requête/Réponse

### Créer des DTOs

**Fichier**: `src/main/java/com/mycompany/iaeval/service/dto/EvaluationRequest.java`

```java
public class EvaluationRequest {

  private Long soumissionId;
  private Long appelOffreId;
  private List<Long> documentsIds;
  // Getters/Setters
}

```

## 5. Tester les Endpoints

### Avec cURL

```bash
# 1. Récupérer les appels d'offres
curl -H "Authorization: Bearer {TOKEN}" \
  http://localhost:8080/api/soumissionnaire/appels-offres

# 2. Récupérer les documents
curl -H "Authorization: Bearer {TOKEN}" \
  http://localhost:8080/api/soumissionnaire/soumissions/1/documents?appelOffreId=1

# 3. Lancer une évaluation
curl -X POST \
  -H "Authorization: Bearer {TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"soumissionId":1,"appelOffreId":1,"documentsIds":[1,2,3]}' \
  http://localhost:8080/api/soumissionnaire/evaluations/lancer
```

### Avec Postman

1. Créer une collection "IA-EVAL Soumissionnaire"
2. Configurer OAuth2 avec Keycloak
3. Importer les endpoints listés ci-dessus

## 6. Tester l'Interface Angular

### Développement Local

```bash
# Construction
npm start

# Tester dans le navigateur
# 1. Connecter avec un utilisateur soumissionnaire
# 2. Accéder à http://localhost:4200/soumissionnaire
# 3. Tester le flux complet
```

### Tests Unitaires

```bash
# Composant
ng test --include='**/soumissionnaire-dashboard.component.spec.ts'

# Service
ng test --include='**/evaluation-ai.service.spec.ts'

# Tous les tests soumissionnaire
ng test --include='**/soumissionnaire/**'
```

## 7. Intégration avec les Services AI

### Mettre à Jour EvaluationService

```java
// Dans EvaluationService.java
public void evaluerSoumission(Long soumissionId, List<Long> documentIds) {
  LOG.debug("Launching AI evaluation for soumission: {}", soumissionId);

  // 1. Récupérer les documents
  List<DocumentJoint> documents = documentJointRepository.findAllById(documentIds);

  // 2. Extraire le contenu OCR
  List<String> contenuDocuments = documents.stream().map(DocumentJoint::getContenuOcr).filter(Objects::nonNull).toList();

  // 3. Appeler le service IA
  String prompt = construirePromptEvaluation(documents);
  String resultat = aiService.evaluerDocuments(prompt, contenuDocuments);

  // 4. Parser et sauvegarder les résultats
  EvaluationResultat resultatParsed = parserResultatAi(resultat);
  sauvegarderEvaluation(soumissionId, resultatParsed);
}

```

## 8. Déploiement en Production

### Étapes Pré-Déploiement

- [ ] Tester avec de vrais utilisateurs Keycloak
- [ ] Valider les permissions Keycloak
- [ ] Tester la performance avec plusieurs appels d'offres
- [ ] Documenter les procédures d'administration
- [ ] Créer les rôles dans Keycloak production

### Variables d'Environnement

```bash
export SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=https://keycloak-prod/auth/realms/production/protocol/openid-connect/certs
```

## Ressources et Références

- [Spring Security OAuth2 Documentation](https://spring.io/projects/spring-security-oauth2-resourceserver)
- [Keycloak Role Management](https://www.keycloak.org/docs/latest/server_admin/index.html#creating-and-managing-roles)
- [Angular HttpClient Testing](https://angular.io/guide/http#testing-http-requests)
- [JHipster Security Configuration](https://www.jhipster.tech/security/)
