# 🧾 Système de Gestion de Facturation

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.x-blue.svg)](https://reactjs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> Application full-stack moderne de gestion de facturation avec interfaces séparées Admin/Client, système de commandes complet et génération automatique de factures PDF.

---

## 📋 Vue d'ensemble

### 🎯 Objectif du projet
Système complet permettant la gestion d'un catalogue produits, la création de commandes et la génération automatique de factures avec export PDF. Interface web responsive avec authentification JWT et rôles différenciés.

### ✨ Fonctionnalités clés

| Fonctionnalité | Description |
|----------------|-------------|
| 🔐 **Authentification** | JWT sécurisé avec rôles Admin/Client |
| 👥 **Gestion utilisateurs** | CRUD complet, activation/désactivation |
| 📦 **Catalogue produits** | Gestion stock, catégories, alertes |
| 🛒 **Commandes** | Panier dynamique, suivi temps réel |
| 💰 **Facturation** | Génération auto, calculs TVA, exports PDF |
| 📊 **Dashboard** | Statistiques et KPIs en temps réel |
| 📱 **Responsive** | Compatible mobile, tablette, desktop |

---

## 🏗 Architecture

```
facturation-app/
│
├── 📁 backend/                    # API REST Spring Boot
│   ├── src/main/java/
│   │   └── com.facturation.facture/
│   │       ├── model/            # Entités JPA
│   │       ├── dto/              # Data Transfer Objects
│   │       ├── controller/       # REST Controllers
│   │       ├── service/          # Logique métier
│   │       ├── repository/       # Repositories JPA
│   │       └── security/         # JWT & Security
│   └── README-BACKEND.md         # 📖 Documentation détaillée
│
├── 📁 frontend/                   # Application React
│   ├── src/
│   │   ├── api/                  # Intégration API
│   │   ├── context/              # Context API (Auth)
│   │   ├── pages/                # Pages Admin & Client
│   │   └── components/           # Composants réutilisables
│   └── README-FRONTEND.md        # 📖 Documentation détaillée
│
└── README.md                      # 📄 Ce fichier
```

---

## 🛠 Stack technique

### Backend
```
Java 17 + Spring Boot 3.x + Spring Security + JWT
MySQL 8.0 + Hibernate/JPA + iText (PDF)
Maven + Lombok + Bean Validation
```

### Frontend
```
React 18.x + React Router 6.x + Axios
Bootstrap 5.x + Bootstrap Icons
Vite (Build) + Context API (State)
```

---

## 🚀 Installation rapide

### Prérequis
- **JDK 17+** | **Node.js 18+** | **MySQL 8.0+** | **Git**

### 1️⃣ Cloner le projet
```bash
git clone https://github.com/votre-username/facturation-app.git
cd facturation-app
```

### 2️⃣ Configuration Backend

**Base de données :**
```sql
CREATE DATABASE facturation_db;
CREATE USER 'facturation_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON facturation_db.* TO 'facturation_user'@'localhost';
```

**Configuration (`backend/src/main/resources/application.properties`) :**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/facturation_db
spring.datasource.username=facturation_user
spring.datasource.password=password
jwt.secret=VotreCleSecrete123456789
```

**Démarrer :**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
✅ Backend : `http://localhost:8080`

### 3️⃣ Configuration Frontend

**Variables d'environnement (`.env`) :**
```env
VITE_API_URL=http://localhost:8080/api
```

**Démarrer :**
```bash
cd frontend
npm install
npm run dev
```
✅ Frontend : `http://localhost:3000`

---

## 🎯 Utilisation

### 👨‍💼 Compte Admin (par défaut)
```
Email    : admin@facturation.com
Password : admin123
```

**Accès :**
- Gestion complète : utilisateurs, produits, catégories
- Traitement commandes : changement statut, mode paiement
- Factures : génération, export PDF (simple/multiple/ZIP)
- Dashboard : statistiques, graphiques, alertes

### 👤 Compte Client

**Inscription :**
```
POST /api/auth/register
{
  "email": "client@example.com",
  "motDePasse": "password123",
  "nom": "Dupont",
  "prenom": "Jean"
}
```

**Accès :**
- Catalogue produits avec recherche et filtres
- Panier dynamique avec calcul temps réel
- Création et suivi des commandes
- Consultation et téléchargement des factures PDF

---

## 📊 Modèle de données

### Entités principales

```
User ──────────> Commande ──────────> Facture
  │                  │
  │                  │
  └─ Role           └──> LigneCommande <──┐
     (ADMIN/CLIENT)           │            │
                              │            │
Categorie ────────> Produit <─────────────┘
```

### Tables (7 principales)

| Table | Description | Champs clés |
|-------|-------------|-------------|
| `users` | Utilisateurs | id, email, role, nom, prenom |
| `categories` | Catégories | id, nom, description |
| `produits` | Produits | id, nom, prix, stock, categorie_id |
| `commandes` | Commandes | id, numero, statut, totalTTC, client_id |
| `lignes_commande` | Détails commande | id, quantite, prix, commande_id, produit_id |
| `factures` | Factures | id, numero, montantTTC, statut, commande_id |
| `notifications` | Notifications | id, type, message, user_id |

📖 **Voir détails complets** : [README-BACKEND.md](backend/README-BACKEND.md#modèle-de-données)

---

## 🔌 API Endpoints

### Authentification
```
POST   /api/auth/register        # Inscription
POST   /api/auth/login           # Connexion (retourne JWT)
```

### Admin
```
GET    /api/admin/categories     # Liste catégories
POST   /api/admin/categories     # Créer catégorie
GET    /api/admin/produits       # Liste produits
POST   /api/admin/produits       # Créer produit
GET    /api/admin/commandes      # Toutes les commandes
PATCH  /api/admin/commandes/:id/statut  # Changer statut
GET    /api/admin/factures       # Toutes les factures
GET    /api/admin/factures/:id/pdf      # Export PDF
GET    /api/admin/factures/export/zip   # Export ZIP
```

### Client
```
GET    /api/clients/produits                    # Produits disponibles
POST   /api/client/commandes/creer-avec-produits # Créer commande
GET    /api/client/commandes/mes-commandes/:id  # Mes commandes
PUT    /api/client/commandes/:id/annuler        # Annuler commande
GET    /api/clients/factures/historique         # Mes factures
GET    /api/clients/factures/:id/pdf            # Télécharger PDF
```

📖 **Documentation complète** : [README-BACKEND.md](backend/README-BACKEND.md#api-endpoints)

---

## 🔐 Sécurité

### JWT Authentication
```javascript
// 1. Login
POST /api/auth/login
Response: { token: "eyJhbGc...", role: "ADMIN" }

// 2. Requêtes authentifiées
GET /api/admin/produits
Headers: { Authorization: "Bearer eyJhbGc..." }
```

### Rôles et permissions

| Rôle | Accès |
|------|-------|
| **ADMIN** | Tous les endpoints `/api/admin/**` |
| **CLIENT** | Endpoints `/api/client/**` et lecture `/api/clients/**` |

---

## 📸 Captures d'écran

### Interface Login
```
<img width="1851" height="964" alt="image" src="https://github.com/user-attachments/assets/a99b7984-03d8-45c2-8862-6c61e4601fe6" />

```

### Interface Admin
```
<img width="1851" height="964" alt="image" src="https://github.com/user-attachments/assets/743ac7be-8533-4a3c-a274-c09e4aebdcc7" />

```

### Interface Client
```
<img width="1851" height="964" alt="image" src="https://github.com/user-attachments/assets/8d420340-8394-4956-8c52-879085fa2edf" />

```
---

## 🧪 Tests

### Backend
```bash
cd backend
mvn test                    # Tests unitaires
mvn verify                  # Tests d'intégration
mvn jacoco:report          # Coverage
```

### Frontend
```bash
cd frontend
npm test                    # Tests Jest
npm run test:coverage      # Coverage
```

---

## 📦 Déploiement

### Option 1 : Docker Compose (Recommandé)

**docker-compose.yml :**
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: facturation_db
      MYSQL_USER: user
      MYSQL_PASSWORD: password
    ports:
      - "3306:3306"

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    environment:
      DB_HOST: mysql

  frontend:
    build: ./frontend
    ports:
      - "3000:80"
    depends_on:
      - backend
```

**Démarrer :**
```bash
docker-compose up -d
```

### Option 2 : Déploiement séparé

**Backend (Heroku/Railway) :**
```bash
cd backend
mvn clean package
# Déployer le JAR généré
```

**Frontend (Vercel/Netlify) :**
```bash
cd frontend
npm run build
# Déployer le dossier dist/
```

---

## 📈 Roadmap

### Version 1.0 (Actuelle) ✅
- [x] Authentification JWT
- [x] CRUD complet (Users, Produits, Catégories)
- [x] Système de commandes
- [x] Génération factures PDF
- [x] Dashboard admin

---


## 📖 Documentation

| Document | Description |
|----------|-------------|
| [README-BACKEND.md](backend/README-BACKEND.md) | Documentation complète Backend |
| [README-FRONTEND.md](frontend/README-FRONTEND.md) | Documentation complète Frontend |
| [API.md](docs/API.md) | Référence API REST |
| [ARCHITECTURE.md](docs/ARCHITECTURE.md) | Architecture détaillée |

---


## 👨‍💻 Auteur

**Mbarek Manel**


---

## 📊 Statistiques du projet

```
📦 Backend
  ├── 7 Entités JPA
  ├── 45+ Endpoints REST
  ├── 15+ Services métier
  └── JWT Security

🎨 Frontend
  ├── 12+ Pages
  ├── 20+ Composants
  ├── Context API
  └── Responsive Design

📈 Fonctionnalités
  ├── Authentification
  ├── CRUD complet
  ├── Commandes
  ├── Facturation PDF
  └── Dashboard
```

---

## ⭐ Star History

Si ce projet vous a aidé, n'oubliez pas de lui donner une ⭐ !

---

<div align="center">

Fait  par [Manel Mbarek](https://github.com/manel6mbarek)

</div>
