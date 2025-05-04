# API de Gestion d'Inventaire Retail

## Aperçu

Cette API REST développée avec Spring Boot permet la gestion complète d'inventaire multi-magasins pour le secteur retail. Elle offre des fonctionnalités de gestion des produits, de suivi des stocks dans différents points de vente, et de réservation de produits, le tout dans une architecture moderne et évolutive.

## Fonctionnalités

- **Gestion des produits**
    - Création, récupération, mise à jour et suppression de produits
    - Recherche de produits par identifiant ou SKU
    - Gestion des informations complètes (nom, description, prix, code SKU)

- **Gestion des stocks**
    - Suivi des niveaux de stock par produit et par magasin
    - Mise à jour des quantités en stock
    - Vérification de disponibilité en temps réel

- **Système de réservation**
    - Réservation de produits avec génération de code unique
    - Validation de disponibilité avant réservation
    - Gestion des réservations avec expiration automatique

- **Synchronisation d'inventaire**
    - Propagation des mises à jour de stock entre magasins
    - Gestion des conflits de synchronisation

## Technologies utilisées

- **Backend**: Java 17, Spring Boot 3.x
- **Base de données**: H2 (en mémoire)
- **Tests**: JUnit 5, Mockito, Spring Test
- **Build**: Maven

## Prérequis

Pour exécuter cette application, vous devez disposer de:

- Java Development Kit (JDK) 17 ou supérieur
- Maven 3.6.x ou supérieur
- Un IDE Java (IntelliJ IDEA, Eclipse, VS Code avec extensions Java)
- Postman (pour les tests manuels d'API)

## Installation et démarrage

### Cloner le dépôt

```bash
git clone https://github.com/votre-nom/inventory-api.git
cd inventory-api
```

### Compiler le projet

```bash
mvn clean install
```

### Exécuter l'application

```bash
mvn spring-boot:run
```

L'application démarrera sur le port 8080 par défaut. Vous pouvez accéder à l'API via `http://localhost:8080/api`.

## Configuration

Le fichier principal de configuration se trouve dans `src/main/resources/application.properties`. Voici les paramètres importants:

```properties
# Configuration de la base de données H2
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=osza
spring.datasource.password=osza123
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Active la console H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Configuration pour exécution des scripts SQL
spring.sql.init.mode=always
spring.sql.init.platform=h2
spring.jpa.defer-datasource-initialization=true

# Configuration JPA
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Configuration du serveur
server.port=8080
```

## Accès à la base de données

Cette application utilise une base de données H2 en mémoire pour simplifier le développement et les tests. Pour accéder à la console H2:

1. Démarrez l'application
2. Ouvrez votre navigateur à l'adresse: http://localhost:8080/h2-console
3. Connectez-vous avec:
    - JDBC URL: `jdbc:h2:mem:testdb`
    - Username: `osza`
    - Password: `osza123`

## Structure du projet

```
inventoryapi/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── inventoryapi/
│   │   │           ├── InventoryApiApplication.java     # Point d'entrée de l'application
│   │   │           ├── controllers/                     # Contrôleurs REST
│   │   │           │   └── ProductController.java
│   │   │           ├── models/                          # Entités JPA
│   │   │           │   ├── Product.java
│   │   │           │   ├── Stock.java
│   │   │           │   └── Reservation.java
│   │   │           ├── repositories/                    # Repositories Spring Data
│   │   │           │   ├── ProductRepository.java
│   │   │           │   ├── StockRepository.java
│   │   │           │   └── ReservationRepository.java
│   │   │           ├── services/                        # Couche métier
│   │   │           │   ├── ProductService.java
│   │   │           │   └── ProductServiceImpl.java
│   │   │           └── exceptions/                      # Gestion des exceptions
│   │   │               └── ResourceNotFoundException.java
│   │   └── resources/
│   │       ├── application.properties                   # Configuration de l'application
│   │       └── data.sql                                 # Données initiales
│   └── test/
│       └── java/
│           └── com/
│               └── inventoryapi/
│                   └── controllers/
│                       └── ProductControllerTest.java   # Tests unitaires
└── pom.xml                                             # Configuration Maven
```

## Documentation de l'API

### Endpoints des produits

| Méthode HTTP | Endpoint                  | Description                                    |
|--------------|---------------------------|------------------------------------------------|
| GET          | /api/products             | Récupérer tous les produits                    |
| GET          | /api/products/{id}        | Récupérer un produit par son ID                |
| POST         | /api/products             | Créer un nouveau produit                       |
| PUT          | /api/products/{id}        | Mettre à jour un produit existant              |
| DELETE       | /api/products/{id}        | Supprimer un produit                           |

#### Exemple de corps de requête (POST/PUT)

```json
{
  "name": "T-shirt Homme",
  "description": "T-shirt en coton bio",
  "price": 19.99,
  "sku": "TSH-001"
}
```

### Endpoints de gestion des stocks

| Méthode HTTP | Endpoint                                               | Description                              |
|--------------|---------------------------------------------------------|------------------------------------------|
| GET          | /api/products/availability/{storeId}/{sku}/{quantity}   | Vérifier la disponibilité d'un produit   |
| PUT          | /api/products/stock/{storeId}/{sku}?quantity={quantity} | Mettre à jour le stock d'un produit      |

### Endpoints de réservation

| Méthode HTTP | Endpoint                                            | Description                    |
|--------------|-----------------------------------------------------|--------------------------------|
| POST         | /api/products/reserve/{storeId}/{sku}/{quantity}    | Réserver un produit            |

## Tests

### Tests unitaires automatisés (JUnit et Mockito)

Les tests unitaires utilisent JUnit 5 avec Mockito pour tester le contrôleur REST de manière isolée. La classe `ProductControllerTest` contient les tests pour toutes les fonctionnalités du contrôleur.

Pour exécuter les tests unitaires:

```bash
mvn test
```

#### Approche des tests unitaires

Les tests unitaires suivent l'approche suivante:

1. **Configuration** - Utilisation de `@ExtendWith(MockitoExtension.class)` pour l'intégration de Mockito
2. **Isolation** - Utilisation de `@Mock` pour simuler le service et `@InjectMocks` pour injecter les mocks
3. **Pattern AAA** - Structure Arrange-Act-Assert pour chaque test
4. **MockMvc** - Simulation des requêtes HTTP pour tester les endpoints REST

Exemple de test:

```java
@Test
@DisplayName("Test GET /api/products/{id} - Récupérer un produit par ID")
void testGetProductById() throws Exception {
    // Arrange - Simulation du service
    when(productService.getProductById(1L))
        .thenReturn(Optional.of(testProduct));
    
    // Act & Assert - Exécution de la requête et vérification
    mockMvc.perform(get("/api/products/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Test Product"))
        .andExpect(jsonPath("$.price").value(19.99));
}
```

### Tests manuels avec Postman

Vous pouvez également tester l'API manuellement avec Postman. Voici quelques exemples de requêtes:

#### Récupérer tous les produits

- **Méthode**: GET
- **URL**: http://localhost:8080/api/products
- **Réponse attendue**: Liste de tous les produits avec code 200 OK

#### Créer un nouveau produit

- **Méthode**: POST
- **URL**: http://localhost:8080/api/products
- **Headers**: Content-Type: application/json
- **Body**:
  ```json
  {
    "name": "Pull-over Homme",
    "description": "Pull-over en laine",
    "price": 39.99,
    "sku": "PHM-004"
  }
  ```
- **Réponse attendue**: Produit créé avec code 201 Created

#### Mettre à jour le stock d'un produit

- **Méthode**: PUT
- **URL**: http://localhost:8080/api/products/stock/STORE-001/TSH-001?quantity=60
- **Réponse attendue**: Code 200 OK

#### Réserver un produit

- **Méthode**: POST
- **URL**: http://localhost:8080/api/products/reserve/STORE-001/TSH-001/3
- **Réponse attendue**: Code de réservation avec code 201 Created

Une collection Postman complète est disponible dans le dossier `/docs/postman` pour faciliter les tests manuels.

## Données initiales

Lors du démarrage de l'application, les données initiales sont chargées à partir du fichier `src/main/resources/data.sql`:

- 3 produits (T-shirt, Jeans, Veste)
- Stocks pour ces produits dans 2 magasins différents
- Une réservation active

Ces données permettent de tester rapidement l'API sans avoir à créer des données manuellement.

## Gestion des erreurs

L'API utilise les codes HTTP standards pour indiquer le succès ou l'échec des opérations:

- 200 OK - Requête traitée avec succès
- 201 Created - Ressource créée avec succès
- 204 No Content - Requête traitée avec succès, pas de contenu à renvoyer
- 400 Bad Request - Paramètres invalides
- 404 Not Found - Ressource non trouvée
- 500 Internal Server Error - Erreur interne du serveur

Les erreurs retournent également un message descriptif pour aider au débogage.

## Performances et limitations

- La base de données H2 en mémoire est utilisée uniquement à des fins de développement et de test
- Pour une utilisation en production, il est recommandé de configurer une base de données persistante (MySQL, PostgreSQL)
- L'API ne gère pas actuellement l'authentification et l'autorisation

## Bonnes pratiques et conventions

Le code suit les conventions et bonnes pratiques suivantes:

- Architecture en couches (contrôleur, service, repository)
- Utilisation de DTOs pour la séparation des modèles d'API et de persistance
- Documentation du code avec Javadoc
- Tests unitaires pour toutes les fonctionnalités clés
- Gestion explicite des erreurs avec des exceptions personnalisées

## Auteur

**Ossama Zarani**  
