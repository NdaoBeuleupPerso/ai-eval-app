# Espace Soumissionnaire - Guide d'Intégration AI

## Vue d'ensemble

L'espace Soumissionnaire permet aux utilisateurs avec le rôle `ROLE_SOUMISSIONNAIRE` de lancer des évaluations AI sur leurs appels d'offres et documents.

## Architecture

### Backend

- **Rôle**: `ROLE_SOUMISSIONNAIRE` défini dans `AuthoritiesConstants.java`
- **Contrôleur REST**: `SoumissionnaireEvaluationResource.java` (`/api/soumissionnaire`)
- **Endpoints préparés**:
  - `GET /api/soumissionnaire/appels-offres` - Liste les appels d'offres ouverts
  - `GET /api/soumissionnaire/soumissions/{soumissionId}/documents` - Récupère les documents
  - `POST /api/soumissionnaire/evaluations/lancer` - Lance une évaluation AI
  - `GET /api/soumissionnaire/evaluations/{evaluationId}/status` - Récupère le statut

### Frontend (Angular)

- **Service**: `EvaluationAiService` dans `src/main/webapp/app/entities/evaluation/service/`
- **Composant**: `SoumissionnaireDashboardComponent` dans `src/main/webapp/app/soumissionnaire/`
- **Route**: `/soumissionnaire` (accessible uniquement aux utilisateurs avec `ROLE_SOUMISSIONNAIRE`)
- **Navigation**: Lien "Évaluations AI" dans le navbar visible aux soumissionnaires

## Flux de l'Utilisateur

1. **Authentification**: L'utilisateur se connecte avec un compte soumissionnaire
2. **Navigation**: Accès au lien "Évaluations AI" dans le navbar
3. **Sélection d'appel d'offre**: Choix parmi la liste des appels d'offres ouverts
4. **Sélection des documents**: Sélection des documents à évaluer
5. **Lancement de l'évaluation**: Démarrage de l'évaluation AI asynchrone
6. **Résultats**: Affichage du statut et des résultats

## Points d'Intégration

### 1. Données Simulées (Actuellement Actives)

Les services utilisent des données mock dans le frontend:

- **AppelsOffres**: Tableau de test dans `getAppelsOffresMock()`
- **Documents**: Données simulées dans `getDocumentsSoumissionMock()`
- **Backend**: Endpoints retournent des réponses vides jusqu'à l'implémentation complète

### 2. Intégration avec les Endpoints Réels

**Quand les données seront disponibles**, modifier `EvaluationAiService`:

```typescript
// Remplacer les appels mock par:
getAppelsOffres(): Observable<IAppelOffreEvaluation[]> {
  return this.http.get<IAppelOffreEvaluation[]>(
    `${this.resourceUrl}/appels-offres`
  );
}

getDocumentsSoumission(soumissionId: number, appelOffreId: number): Observable<ISoumisisonnaireDocs> {
  return this.http.get<ISoumisisonnaireDocs>(
    `${this.resourceUrl}/soumissions/${soumissionId}/documents?appelOffreId=${appelOffreId}`
  );
}
```

### 3. Service Backend (À Compléter)

Dans `SoumissionnaireEvaluationResource.java`, implémenter:

```java
// 1. Récupérer les AppelsOffre ouverts du soumissionnaire actuel
@GetMapping("/appels-offres")
public ResponseEntity<List<Map<String, Object>>> getAppelsOffresDisponibles() {
  String currentUserLogin = SecurityUtils.getCurrentUserLogin();
  // Query: SELECT * FROM appel_offre WHERE statut = 'OUVERT'
}

// 2. Récupérer les documents d'une soumission
@GetMapping("/soumissions/{soumissionId}/documents")
public ResponseEntity<Map<String, Object>> getDocumentsSoumission() {
  // Valider que l'utilisateur courant est propriétaire de cette soumission
  // Query: SELECT * FROM document_joint WHERE soumission_id = ?
}

// 3. Lancer l'évaluation AI
@PostMapping("/evaluations/lancer")
public ResponseEntity<Map<String, Object>> lancerEvaluationAi() {
  // Créer une entité Evaluation
  // Appeler le service AI pour traiter les documents
  // Retourner le statut d'évaluation
}

```

## Configuration de Keycloak (OAuth2)

Pour que les utilisateurs soumissionnaires soient correctement authentifiés:

1. **Créer le rôle** dans Keycloak:

   - Nom: `SOUMISSIONNAIRE`
   - Description: "Utilisateur soumissionnaire qui peut lancer des évaluations"

2. **Affecter le rôle** aux utilisateurs:

   - Allez dans Users
   - Sélectionnez l'utilisateur
   - Onglet Role Mappings
   - Affecter le rôle SOUMISSIONNAIRE

3. **Vérifier la configuration OAuth2**:
   - `application-*.yml` doit mapper les rôles Keycloak correctement
   - La dépendance Spring Security OAuth2 doit être présente

## Structure des Données

### Modèle d'Évaluation

```typescript
interface IAiEvaluationRequest {
  soumissionId: number;
  appelOffreId: number;
  documentsIds: number[];
}

interface IAiEvaluationResponse {
  evaluationId: number;
  scoreGlobal: number;
  scoreAdmin?: number;
  scoreTech?: number;
  scoreFin?: number;
  rapportAnalyse: string;
  status: string;
}
```

### Types de Documents Supportés

- `OFFRE_TECHNIQUE` - Offre technique
- `ATTESTATION` - Attestations financières
- `GARANTIE` - Garanties et assurances
- `PV_CONFORMITE` - Procès-verbaux de conformité
- `AUTRE` - Autres documents

## Tests

### Tests Unitaires Angular

```bash
ng test --include='**/soumissionnaire/**'
ng test --include='**/evaluation-ai.service.spec.ts'
```

### Tests E2E (Cypress)

```bash
npm run e2e -- --spec "cypress/integration/soumissionnaire/**"
```

### Tests Manuels

1. Connecter un utilisateur avec rôle `ROLE_SOUMISSIONNAIRE`
2. Naviguer vers `/soumissionnaire`
3. Sélectionner un appel d'offre
4. Sélectionner des documents
5. Lancer une évaluation
6. Vérifier les résultats simulés

## Fichiers Modifiés/Créés

### Backend

- ✅ `AuthoritiesConstants.java` - Ajout du rôle SOUMISSIONNAIRE
- ✅ `SoumissionnaireEvaluationResource.java` - Nouveau contrôleur REST (endpoints)

### Frontend

- ✅ `authority.constants.ts` - Ajout enum SOUMISSIONNAIRE
- ✅ `app.routes.ts` - Nouvelles routes pour soumissionnaire
- ✅ `soumissionnaire-dashboard.component.ts` - Composant principal
- ✅ `soumissionnaire-dashboard.component.html` - Template
- ✅ `soumissionnaire-dashboard.component.scss` - Styles
- ✅ `soumissionnaire.routes.ts` - Routes du module soumissionnaire
- ✅ `evaluation-ai.service.ts` - Service pour compommer avec l'API
- ✅ `navbar.component.html` - Ajout lien de navigation

## Prochaines Étapes

1. **Implémentation des endpoints réels** dans le backend
2. **Intégration avec les données existantes** de la base de données
3. **Tests d'intégration** avec les services AI
4. **Notifications par email** pour les résultats d'évaluation
5. **Dashboard d'administration** pour monitorer les évaluations
6. **Export des rapports** en PDF/CSV

## Support

Pour plus de détails sur l'intégration avec les services AI, consultez:

- `AiService.java` - Service IA principal
- `AiConfiguration.java` - Configuration IA
- `TraceAudit` - Audit des appels IA
