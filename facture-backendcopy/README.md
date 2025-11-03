# 📦 Backend - Système de Gestion de Facturation

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Application backend Spring Boot pour la gestion complète d'un système de facturation avec gestion des utilisateurs, produits, commandes et factures.

## 📋 Table des matières

- [Aperçu](#aperçu)
- [Technologies](#technologies)
- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Modèle de données](#modèle-de-données)
- [API Endpoints](#api-endpoints)
- [Sécurité](#sécurité)
- [Tests](#tests)
- [Déploiement](#déploiement)

---

## 🎯 Aperçu

### Fonctionnalités principales

- ✅ **Authentification JWT** - Système de connexion sécurisé
- ✅ **Gestion des utilisateurs** - Admin et Client avec rôles différenciés
- ✅ **Gestion des produits** - CRUD complet avec stock et alertes
- ✅ **Gestion des catégories** - Organisation des produits
- ✅ **Système de commandes** - Panier, validation, suivi
- ✅ **Génération de factures** - Automatique avec calculs TVA
- ✅ **Export PDF** - Simple, multiple et ZIP
- ✅ **Dashboard administrateur** - Statistiques en temps réel

---

## 🛠 Technologies

```json
{
  "backend": {
    "java": "17",
    "spring-boot": "3.x",
    "spring-data-jpa": "3.x",
    "hibernate": "6.x"
  },
  "database": {
    "mysql": "8.0",
    "postgresql": "14.x (alternative)"
  },
  "security": {
    "jwt": "latest",
    "bcrypt": "latest"
  },
  "tools": {
    "maven": "3.8+",
    "itext": "7.x (PDF)",
    "lombok": "latest"
  }
}
```

---

## 🏗 Architecture

```
src/main/java/com/facturation/facture/
├── 📂 model/                    # Entités JPA
├── 📂 dto/                      # Data Transfer Objects
├── 📂 controller/               # REST Controllers
├── 📂 service/                  # Logique métier
├── 📂 repository/               # Repositories JPA
├── 📂 security/                 # JWT & Security
├── 📂 exception/                # Gestion erreurs
└── 📂 config/                   # Configurations
```

---

## ✅ Prérequis

| Outil | Version minimale | Téléchargement |
|-------|------------------|----------------|
| JDK | 17+ | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) |
| Maven | 3.8+ | [Maven](https://maven.apache.org/download.cgi) |
| MySQL | 8.0+ | [MySQL](https://dev.mysql.com/downloads/) |
| Git | 2.x | [Git](https://git-scm.com/downloads) |

---

## 🚀 Installation

### 1️⃣ Cloner le projet

```bash
git clone https://github.com/votre-username/facturation-backend.git
cd facturation-backend
```

### 2️⃣ Créer la base de données

```sql
CREATE DATABASE facturation_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'facturation_user'@'localhost' IDENTIFIED BY 'VotreMotDePasse123!';
GRANT ALL PRIVILEGES ON facturation_db.* TO 'facturation_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3️⃣ Configurer l'application

Créer `src/main/resources/application.properties` :

```properties
# Configuration serveur
server.port=8080
spring.application.name=facturation-backend

# Base de données
spring.datasource.url=jdbc:mysql://localhost:3306/facturation_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=facturation_user
spring.datasource.password=VotreMotDePasse123!
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT
jwt.secret=VotreCleSecreteTresLongueEtSecurisee123456789ABCDEFGHIJKLMNOP
jwt.expiration=86400000

# Upload fichiers
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging
logging.level.com.facturation=DEBUG
```

### 4️⃣ Compiler et démarrer

```bash
# Compiler
mvn clean install

# Démarrer l'application
mvn spring-boot:run
```

✅ **L'API est accessible sur** : `http://localhost:8080`

---

## ⚙️ Configuration

### Profils d'environnement

#### Développement (`application-dev.properties`)

```properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
logging.level.com.facturation=DEBUG
```

#### Production (`application-prod.properties`)

```properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.com.facturation=INFO
server.error.include-message=never
```

**Activer un profil** :

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---



### 📊 Tables détaillées

#### 🔷 Table `users`

**Description** : Stocke les utilisateurs (Admin et Client)

| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identifiant unique |
| `email` | VARCHAR(100) | NOT NULL, UNIQUE | Email de connexion |
| `mot_de_passe` | VARCHAR(255) | NOT NULL | Mot de passe hashé |
| `nom` | VARCHAR(50) | NOT NULL | Nom de famille |
| `prenom` | VARCHAR(50) | NOT NULL | Prénom |
| `telephone` | VARCHAR(20) | NULL | Numéro de téléphone |
| `adresse` | TEXT | NULL | Adresse complète |
| `role` | ENUM | NOT NULL | 'ADMIN' ou 'CLIENT' |
| `actif` | BOOLEAN | NOT NULL, DEFAULT TRUE | Compte actif/inactif |
| `date_creation` | DATETIME | NOT NULL | Date de création |
| `date_modification` | DATETIME | NULL | Dernière modification |

**Validations** :
- Email : Format valide
- Mot de passe : Minimum 8 caractères
- Nom/Prénom : 2-50 caractères

**Index** :
- Index unique sur `email`
- Index sur `role`

---

#### 🔷 Table `categories`

**Description** : Catégories de produits

| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identifiant unique |
| `nom` | VARCHAR(100) | NOT NULL | Nom de la catégorie |
| `description` | TEXT | NULL | Description détaillée |
| `actif` | BOOLEAN | NOT NULL, DEFAULT TRUE | Catégorie active |
| `date_creation` | DATETIME | NOT NULL | Date de création |
| `date_modification` | DATETIME | NULL | Dernière modification |

**Validations** :
- Nom : 2-100 caractères, obligatoire

**Relations** :
- One-to-Many avec `produits`

---

#### 🔷 Table `produits`

**Description** : Catalogue des produits

| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identifiant unique |
| `nom` | VARCHAR(100) | NOT NULL | Nom du produit |
| `description` | TEXT | NULL | Description détaillée |
| `prix` | DECIMAL(10,2) | NOT NULL | Prix unitaire |
| `quantite_stock` | INT | NOT NULL, DEFAULT 0 | Quantité en stock |
| `seuil_alerte` | INT | NOT NULL, DEFAULT 0 | Seuil d'alerte stock |
| `disponible` | BOOLEAN | NOT NULL, DEFAULT TRUE | Disponibilité |
| `image_path` | VARCHAR(255) | NULL | Chemin de l'image |
| `categorie_id` | BIGINT | NOT NULL, FK | ID de la catégorie |
| `date_creation` | DATETIME | NOT NULL | Date de création |
| `date_modification` | DATETIME | NULL | Dernière modification |

**Validations** :
- Nom : 2-100 caractères
- Prix : > 0
- Quantité stock : >= 0
- Seuil alerte : >= 0

**Relations** :
- Many-to-One avec `categories`
- One-to-Many avec `lignes_commande`

**Index** :
- Index sur `categorie_id`
- Index sur `disponible`

---

#### 🔷 Table `commandes`

**Description** : Commandes des clients

| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identifiant unique |
| `numero_commande` | VARCHAR(50) | NOT NULL, UNIQUE | Numéro de commande |
| `date_commande` | DATETIME | NOT NULL | Date de création |
| `date_modification` | DATETIME | NULL | Dernière modification |
| `statut` | ENUM | NOT NULL | Statut de la commande |
| `sous_total` | DECIMAL(10,2) | NOT NULL | Total HT |
| `taux_tva` | DECIMAL(5,2) | NOT NULL, DEFAULT 20.00 | Taux de TVA |
| `montant_tva` | DECIMAL(10,2) | NOT NULL | Montant TVA |
| `total_ht` | DECIMAL(10,2) | NOT NULL | Total hors taxes |
| `total_ttc` | DECIMAL(10,2) | NOT NULL | Total TTC |
| `commentaire` | TEXT | NULL | Commentaire client |
| `client_id` | BIGINT | NOT NULL, FK | ID du client |

**Statuts possibles** :
- `EN_ATTENTE` : Commande créée
- `CONFIRMEE` : Confirmée par le client
- `EN_PREPARATION` : En préparation
- `PRETE` : Prête pour livraison
- `EN_LIVRAISON` : En cours de livraison
- `LIVREE` : Livrée
- `ANNULEE` : Annulée
- `PAYEE` : Payée

**Validations** :
- Tous les montants : >= 0
- Taux TVA : >= 0

**Relations** :
- Many-to-One avec `users` (client)
- One-to-Many avec `lignes_commande`
- One-to-One avec `factures`

**Index** :
- Index unique sur `numero_commande`
- Index sur `client_id`
- Index sur `statut`
- Index sur `date_commande`

---

#### 🔷 Table `lignes_commande`

**Description** : Détails des produits dans les commandes

| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identifiant unique |
| `quantite` | INT | NOT NULL | Quantité commandée |
| `prix_unitaire` | DECIMAL(10,2) | NOT NULL | Prix à l'achat |
| `sous_total` | DECIMAL(10,2) | NOT NULL | quantite × prix_unitaire |
| `nom_produit` | VARCHAR(100) | NULL | Nom du produit (copie) |
| `commande_id` | BIGINT | NOT NULL, FK | ID de la commande |
| `produit_id` | BIGINT | NOT NULL, FK | ID du produit |

**Validations** :
- Quantité : >= 1
- Prix unitaire : > 0
- Sous-total : >= 0

**Relations** :
- Many-to-One avec `commandes`
- Many-to-One avec `produits`

**Index** :
- Index sur `commande_id`
- Index sur `produit_id`

**Logique métier** :
- Calcul automatique du sous-total
- Copie du nom du produit pour historique

---

#### 🔷 Table `factures`

**Description** : Factures générées depuis les commandes

| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identifiant unique |
| `numero_facture` | VARCHAR(50) | NOT NULL, UNIQUE | Numéro de facture |
| `date_facture` | DATETIME | NOT NULL | Date de facturation |
| `date_creation` | DATETIME | NOT NULL | Date de création |
| `date_modification` | DATETIME | NULL | Dernière modification |
| `date_paiement` | DATETIME | NULL | Date du paiement |
| `statut` | ENUM | NOT NULL | Statut de la facture |
| `mode_paiement` | ENUM | NULL | Mode de paiement |
| `montant_ht` | DECIMAL(10,2) | NOT NULL | Montant HT |
| `montant_tva` | DECIMAL(10,2) | NOT NULL | Montant TVA |
| `montant_ttc` | DECIMAL(10,2) | NOT NULL | Montant TTC |
| `commentaire` | TEXT | NULL | Commentaire |
| `nom_client` | VARCHAR(100) | NULL | Nom du client (copie) |
| `statut_commande` | VARCHAR(50) | NULL | Statut commande (copie) |
| `commande_id` | BIGINT | NOT NULL, FK, UNIQUE | ID de la commande |

**Statuts possibles** :
- `EN_ATTENTE` : En attente de paiement
- `PAYEE` : Payée
- `ANNULEE` : Annulée
- `REMBOURSEE` : Remboursée

**Modes de paiement** :
- `ESPECES` : Espèces
- `CARTE_BANCAIRE` : Carte bancaire
- `CHEQUE` : Chèque
- `VIREMENT` : Virement
- `PAYPAL` : PayPal

**Validations** :
- Tous les montants : >= 0

**Relations** :
- One-to-One avec `commandes`

**Index** :
- Index unique sur `numero_facture`
- Index unique sur `commande_id`
- Index sur `statut`
- Index sur `date_facture`

---

### 🔗 Relations entre tables

```sql
-- User → Commande
ALTER TABLE commandes 
ADD CONSTRAINT fk_commande_client 
FOREIGN KEY (client_id) REFERENCES users(id);

-- Categorie → Produit
ALTER TABLE produits 
ADD CONSTRAINT fk_produit_categorie 
FOREIGN KEY (categorie_id) REFERENCES categories(id);

-- Commande → LigneCommande
ALTER TABLE lignes_commande 
ADD CONSTRAINT fk_ligne_commande 
FOREIGN KEY (commande_id) REFERENCES commandes(id);

-- Produit → LigneCommande
ALTER TABLE lignes_commande 
ADD CONSTRAINT fk_ligne_produit 
FOREIGN KEY (produit_id) REFERENCES produits(id);

-- Commande → Facture
ALTER TABLE factures 
ADD CONSTRAINT fk_facture_commande 
FOREIGN KEY (commande_id) REFERENCES commandes(id);
```

---

## 🔌 API Endpoints

### 🔐 Authentification

| Méthode | Endpoint | Description | Body |
|---------|----------|-------------|------|
| POST | `/api/auth/register` | Inscription client | `ClientDTO` |
| POST | `/api/auth/login` | Connexion | `{email, motDePasse}` |

**Exemple Login** :

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "client@example.com",
    "motDePasse": "password123"
  }'
```

**Réponse** :

```json
{
  "id": 1,
  "email": "client@example.com",
  "role": "CLIENT",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### 👥 Clients (Admin)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/clients` | Liste des clients |
| GET | `/api/clients/{id}` | Détails d'un client |
| POST | `/api/clients` | Créer un client |
| PUT | `/api/clients/{id}` | Modifier un client |
| DELETE | `/api/clients/{id}` | Supprimer un client |
| GET | `/api/clients/recherche?terme={terme}` | Rechercher |
| PATCH | `/api/clients/{id}/statut?actif={bool}` | Activer/Désactiver |

---

### 🏷 Catégories

#### Admin

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/admin/categories` | Liste |
| GET | `/api/admin/categories/{id}` | Détails |
| POST | `/api/admin/categories` | Créer |
| PUT | `/api/admin/categories/{id}` | Modifier |
| DELETE | `/api/admin/categories/{id}` | Supprimer |

#### Client

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/clients/categories` | Liste |
| GET | `/api/clients/categories/{id}` | Détails |

---

### 📦 Produits

#### Admin

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/admin/produits` | Liste |
| GET | `/api/admin/produits/{id}` | Détails |
| POST | `/api/admin/produits` | Créer |
| PUT | `/api/admin/produits/{id}` | Modifier |
| DELETE | `/api/admin/produits/{id}` | Supprimer |
| PATCH | `/api/admin/produits/{id}/disponible` | Marquer disponible |
| PATCH | `/api/admin/produits/{id}/indisponible` | Marquer indisponible |

#### Client

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/clients/produits` | Produits disponibles |
| GET | `/api/clients/produits/{id}` | Détails |
| GET | `/api/clients/produits/prix?prixMin={min}&prixMax={max}` | Par prix |

---

### 🛒 Commandes

#### Admin

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/admin/commandes` | Toutes les commandes |
| GET | `/api/admin/commandes/{id}` | Détails |
| PATCH | `/api/admin/commandes/{id}/statut?statut={statut}` | Changer statut |

#### Client

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/client/commandes/mes-commandes/{clientId}` | Mes commandes |
| POST | `/api/client/commandes/creer-avec-produits?idClient={id}` | Créer avec produits |
| POST | `/api/client/commandes/{cmdId}/produits/{prodId}?quantite={q}&idClient={id}` | Ajouter produit |
| PUT | `/api/client/commandes/{cmdId}/produits/{prodId}?nouvelleQuantite={q}&idClient={id}` | Modifier quantité |
| DELETE | `/api/client/commandes/{cmdId}/produits/{prodId}?idClient={id}` | Retirer produit |
| PUT | `/api/client/commandes/{id}/annuler?idClient={id}` | Annuler |

---

### 💳 Factures

#### Admin

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/admin/factures` | Toutes les factures |
| GET | `/api/admin/factures/{id}` | Détails |
| PATCH | `/api/admin/factures/{id}/marquer-payee?modePaiement={mode}` | Marquer payée |
| GET | `/api/admin/factures/statistiques` | Statistiques |
| GET | `/api/admin/factures/dashboard` | Dashboard |
| GET | `/api/admin/factures/{id}/pdf` | Export PDF |
| GET | `/api/admin/factures/export/multiple` | Export multiple PDF |
| GET | `/api/admin/factures/export/zip` | Export ZIP |

#### Client

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/clients/factures/historique?clientId={id}` | Mes factures |
| GET | `/api/clients/factures/{id}?clientId={id}` | Détails |
| GET | `/api/clients/factures/{id}/pdf?clientId={id}` | Télécharger PDF |

---

## 🔒 Sécurité

### JWT Token

**Obtenir un token** :

```bash
POST /api/auth/login
```

**Utiliser le token** :

```bash
GET /api/admin/produits
Authorization: Bearer {votre_token}
```

### Rôles

| Rôle | Accès |
|------|-------|
| **ADMIN** | Tous les endpoints `/api/admin/**` |
| **CLIENT** | Endpoints `/api/client/**` et `/api/clients/**` (lecture) |

---

## 🧪 Tests

```bash
# Tests unitaires
mvn test

# Tests d'intégration
mvn verify

# Coverage
mvn clean test jacoco:report
```

---

## 🚢 Déploiement

### Build Production

```bash
mvn clean package -Pprod
```

### Docker

**Dockerfile** :

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Build** :

```bash
docker build -t facturation-backend .
docker run -p 8080:8080 facturation-backend
```

### Docker Compose

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: facturation_db
      MYSQL_USER: facturation_user
      MYSQL_PASSWORD: password
      MYSQL_ROOT_PASSWORD: root
    ports:
      - "3306:3306"

  backend:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    environment:
      DB_HOST: mysql
      DB_PORT: 3306
```

**Lancer** :

```bash
docker-compose up -d
```

---

## 📚 Documentation

- **Swagger UI** : `http://localhost:8080/swagger-ui.html`
- **API Docs** : `http://localhost:8080/api-docs`

---

## 🤝 Contribution

1. Fork le projet
2. Créer une branche (`git checkout -b feature/AmazingFeature`)
3. Commit (`git commit -m 'Add AmazingFeature'`)
4. Push (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

---

## 📄 Licence

MIT License - voir [LICENSE](LICENSE)

---

## 📧 Contact

- **Email** : support@facturation.com
- **GitHub** : [@votre-username](https://github.com/votre-username)

---

**Développé avec ❤️ en Java & Spring Boot**
