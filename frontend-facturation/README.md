# 🎨 Frontend - Système de Gestion de Facturation

[![React](https://img.shields.io/badge/React-18.x-blue.svg)](https://reactjs.org/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.x-purple.svg)](https://getbootstrap.com/)
[![Axios](https://img.shields.io/badge/Axios-1.x-green.svg)](https://axios-http.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Application frontend React pour l'interface utilisateur du système de gestion de facturation avec interfaces séparées pour Administrateurs et Clients.

## 📋 Table des matières

- [Aperçu](#aperçu)
- [Technologies](#technologies)
- [Structure du projet](#structure-du-projet)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Fonctionnalités](#fonctionnalités)
- [Routes](#routes)
- [API Integration](#api-integration)
- [Authentification](#authentification)
- [Build & Déploiement](#build--déploiement)

---

## 🎯 Aperçu

### Fonctionnalités principales

- ✅ **Interface Admin** - Gestion complète du système
- ✅ **Interface Client** - Consultation et commandes
- ✅ **Authentification JWT** - Connexion sécurisée
- ✅ **Gestion du panier** - Ajout/modification produits
- ✅ **Suivi des commandes** - État en temps réel
- ✅ **Visualisation factures** - Export PDF
- ✅ **Design responsive** - Mobile, tablette, desktop
- ✅ **Notifications temps réel** - Feedback utilisateur

---

## 🛠 Technologies

```json
{
  "frontend": {
    "react": "18.x",
    "react-router-dom": "6.x",
    "axios": "1.x"
  },
  "styling": {
    "bootstrap": "5.x",
    "bootstrap-icons": "1.x"
  },
  "build": {
    "vite": "5.x (recommandé)",
    "webpack": "5.x (alternative)"
  },
  "state-management": {
    "react-context": "built-in"
  }
}
```

---

## 🏗 Structure du projet

```
facturation-frontend/
├── 📂 public/                      # Fichiers statiques
│   ├── index.html
│   └── favicon.ico
│
├── 📂 src/
│   ├── 📂 api/                     # Configuration API
│   │   └── api.js                  # Axios + endpoints
│   │
│   ├── 📂 context/                 # Context API
│   │   └── AuthContext.jsx        # Gestion authentification
│   │
│   ├── 📂 pages/                   # Pages de l'application
│   │   ├── Login.jsx              # Page de connexion
│   │   │
│   │   ├── 📂 Admin/              # Pages Admin
│   │   │   ├── DashboardAdmin.jsx
│   │   │   ├── CategoriesAdmin.jsx
│   │   │   ├── ProduitsAdmin.jsx
│   │   │   ├── CommandesAdmin.jsx
│   │   │   └── FacturesAdmin.jsx
│   │   │
│   │   └── 📂 Client/             # Pages Client
│   │       ├── DashboardClient.jsx
│   │       ├── CategoriesClient.jsx
│   │       ├── ProduitClient.jsx
│   │       ├── CommandesClient.jsx
│   │       ├── DetailsCommande.jsx
│   │       ├── FacturesClient.jsx
│   │       └── DetailsFacture.jsx
│   │
│   ├── 📂 components/              # Composants réutilisables
│   │   ├── Navbar.jsx
│   │   ├── Sidebar.jsx
│   │   ├── Card.jsx
│   │   └── Modal.jsx
│   │
│   ├── 📂 utils/                   # Fonctions utilitaires
│   │   ├── formatters.js
│   │   └── validators.js
│   │
│   ├── 📂 styles/                  # Styles CSS
│   │   ├── main.css
│   │   └── custom.css
│   │
│   ├── App.jsx                     # Composant racine
│   └── main.jsx                    # Point d'entrée
│
├── package.json                    # Dépendances
├── vite.config.js                  # Configuration Vite
└── README.md                       # Documentation
```

---

## ✅ Prérequis

| Outil | Version minimale | Téléchargement |
|-------|------------------|----------------|
| Node.js | 18.x+ | [Node.js](https://nodejs.org/) |
| npm | 9.x+ | Inclus avec Node.js |
| Git | 2.x | [Git](https://git-scm.com/) |

**Vérifier les versions** :

```bash
node --version
npm --version
git --version
```

---

## 🚀 Installation

### 1️⃣ Cloner le projet

```bash
git clone https://github.com/votre-username/facturation-frontend.git
cd facturation-frontend
```

### 2️⃣ Installer les dépendances

```bash
npm install
```

### 3️⃣ Configurer l'API

Créer un fichier `.env` à la racine :

```env
VITE_API_URL=http://localhost:8080/api
VITE_APP_NAME=Facturation App
```

### 4️⃣ Démarrer le serveur de développement

```bash
npm run dev
```

✅ **L'application est accessible sur** : `http://localhost:3000`

---

## ⚙️ Configuration

### Variables d'environnement

| Variable | Description | Exemple |
|----------|-------------|---------|
| `VITE_API_URL` | URL de l'API backend | `http://localhost:8080/api` |
| `VITE_APP_NAME` | Nom de l'application | `Facturation App` |
| `VITE_TIMEOUT` | Timeout des requêtes (ms) | `30000` |

### Configuration Axios (`src/api/api.js`)

```javascript
import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080/api",
  headers: { "Content-Type": "application/json" },
  timeout: 30000,
});

// Intercepteur pour ajouter le token JWT
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Intercepteur pour gérer les erreurs
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export default api;
```

---

## 🎨 Fonctionnalités

### 🔐 Authentification

#### Connexion

```javascript
// src/pages/Login.jsx
const handleLogin = async (e) => {
  e.preventDefault();
  try {
    await login(email, motDePasse);
    // Redirection automatique vers dashboard
  } catch (error) {
    alert("Email ou mot de passe incorrect");
  }
};
```

#### Inscription Client

```javascript
const handleRegister = async (e) => {
  e.preventDefault();
  try {
    await register({
      email,
      motDePasse,
      nom,
      prenom,
      telephone,
      adresse
    });
    alert("Inscription réussie !");
  } catch (error) {
    alert("Erreur lors de l'inscription");
  }
};
```

---

### 👨‍💼 Interface Administrateur

#### Dashboard Admin

**Fonctionnalités** :
- 📊 Statistiques en temps réel
- 📈 Graphiques de ventes
- 🔔 Alertes stock faible
- 📋 Dernières commandes

#### Gestion des Catégories

```javascript
// Créer une catégorie
const handleCreateCategorie = async (data) => {
  try {
    await createCategorie(data);
    fetchCategories(); // Rafraîchir la liste
    alert("Catégorie créée !");
  } catch (error) {
    alert("Erreur lors de la création");
  }
};

// Modifier une catégorie
const handleUpdateCategorie = async (id, data) => {
  try {
    await updateCategorie(id, data);
    fetchCategories();
    alert("Catégorie modifiée !");
  } catch (error) {
    alert("Erreur lors de la modification");
  }
};

// Supprimer une catégorie
const handleDeleteCategorie = async (id) => {
  if (window.confirm("Confirmer la suppression ?")) {
    try {
      await deleteCategorie(id);
      fetchCategories();
      alert("Catégorie supprimée !");
    } catch (error) {
      alert("Erreur lors de la suppression");
    }
  }
};
```

#### Gestion des Produits

**CRUD complet** :
- ✅ Créer un produit avec catégorie
- ✅ Modifier prix, stock, description
- ✅ Marquer disponible/indisponible
- ✅ Supprimer un produit
- ✅ Recherche et filtrage

#### Gestion des Commandes

**Actions disponibles** :
- 📋 Voir toutes les commandes
- 🔄 Changer le statut
- 💳 Définir mode de paiement
- 👁️ Voir détails avec lignes

```javascript
// Changer le statut d'une commande
const handleChangeStatut = async (commandeId, nouveauStatut) => {
  try {
    await changerStatutCommande(commandeId, nouveauStatut);
    fetchCommandes();
    alert(`Statut changé : ${nouveauStatut}`);
  } catch (error) {
    alert("Erreur lors du changement de statut");
  }
};
```

#### Gestion des Factures

**Export PDF** :

```javascript
// Export d'une facture unique
const handleExportPDF = async (factureId) => {
  try {
    const response = await exporterFacturePDFAdmin(factureId);
    
    // Créer un lien de téléchargement
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `facture_${factureId}.pdf`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    
    alert("PDF téléchargé avec succès !");
  } catch (error) {
    alert("Erreur lors de l'export PDF");
  }
};

// Export multiple avec filtres
const handleExportMultiple = async () => {
  try {
    const params = {
      statut: selectedStatut,
      dateDebut: dateDebut,
      dateFin: dateFin,
      clientId: selectedClient
    };
    
    const response = await exporterMultipleFacturesPDFAdmin(params);
    
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `factures_export_${Date.now()}.pdf`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    
    alert("Export multiple réussi !");
  } catch (error) {
    alert("Erreur lors de l'export multiple");
  }
};

// Export ZIP
const handleExportZIP = async () => {
  try {
    const params = {
      factureIds: selectedFactures // Array d'IDs
    };
    
    const response = await exporterFacturesZIPAdmin(params);
    
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `factures_${Date.now()}.zip`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    
    alert("ZIP téléchargé avec succès !");
  } catch (error) {
    alert("Erreur lors de l'export ZIP");
  }
};
```

---

### 👤 Interface Client

#### Dashboard Client

**Fonctionnalités** :
- 🛒 Résumé du panier
- 📦 Dernières commandes
- 💰 Historique des factures
- 🔔 Notifications

#### Catalogue Produits

```javascript
// Afficher les produits disponibles
const [produits, setProduits] = useState([]);
const [panier, setPanier] = useState([]);

useEffect(() => {
  const fetchProduits = async () => {
    try {
      const response = await getAllProduitsClient();
      setProduits(response.data);
    } catch (error) {
      console.error("Erreur chargement produits:", error);
    }
  };
  fetchProduits();
}, []);

// Ajouter au panier
const handleAjouterPanier = (produit, quantite) => {
  const existant = panier.find(item => item.produit.id === produit.id);
  
  if (existant) {
    setPanier(panier.map(item => 
      item.produit.id === produit.id 
        ? { ...item, quantite: item.quantite + quantite }
        : item
    ));
  } else {
    setPanier([...panier, { 
      produit, 
      quantite, 
      prixUnitaire: produit.prix 
    }]);
  }
  
  alert("Produit ajouté au panier !");
};
```

#### Gestion du Panier

```javascript
// Créer une commande depuis le panier
const handleValiderPanier = async () => {
  try {
    const commandeDTO = {
      lignesCommande: panier.map(item => ({
        produit: { id: item.produit.id },
        quantite: item.quantite,
        prixUnitaire: item.prixUnitaire
      })),
      commentaire: commentaire
    };
    
    const response = await creerCommandeAvecProduits(commandeDTO);
    
    alert(`Commande créée : ${response.data.numeroCommande}`);
    setPanier([]); // Vider le panier
    navigate('/client/commandes');
  } catch (error) {
    alert("Erreur lors de la création de la commande");
  }
};

// Modifier quantité dans le panier
const handleModifierQuantite = (produitId, nouvelleQuantite) => {
  if (nouvelleQuantite < 1) return;
  
  setPanier(panier.map(item => 
    item.produit.id === produitId 
      ? { ...item, quantite: nouvelleQuantite }
      : item
  ));
};

// Supprimer du panier
const handleSupprimerPanier = (produitId) => {
  setPanier(panier.filter(item => item.produit.id !== produitId));
};

// Calculer le total
const calculerTotal = () => {
  return panier.reduce((total, item) => 
    total + (item.quantite * item.prixUnitaire), 0
  );
};
```

#### Suivi des Commandes

```javascript
// Récupérer mes commandes
const fetchMesCommandes = async () => {
  try {
    const user = JSON.parse(localStorage.getItem('user'));
    const response = await getCommandesClient(user.id);
    setCommandes(response.data);
  } catch (error) {
    console.error("Erreur chargement commandes:", error);
  }
};

// Annuler une commande
const handleAnnulerCommande = async (commandeId) => {
  if (!window.confirm("Annuler cette commande ?")) return;
  
  try {
    await annulerCommande(commandeId);
    fetchMesCommandes();
    alert("Commande annulée avec succès");
  } catch (error) {
    alert("Impossible d'annuler cette commande");
  }
};
```

#### Consultation des Factures

```javascript
// Récupérer mes factures
const fetchMesFactures = async () => {
  try {
    const user = JSON.parse(localStorage.getItem('user'));
    const response = await getHistoriqueFacturesClient(user.id);
    setFactures(response.data);
  } catch (error) {
    console.error("Erreur chargement factures:", error);
  }
};

// Télécharger une facture en PDF
const handleDownloadPDF = async (factureId) => {
  try {
    const user = JSON.parse(localStorage.getItem('user'));
    const response = await exporterFacturePDF(factureId, user.id);
    
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `facture_${factureId}.pdf`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    
    alert("Facture téléchargée !");
  } catch (error) {
    alert("Erreur lors du téléchargement");
  }
};

// Prévisualiser une facture
const handlePreviewPDF = async (factureId) => {
  try {
    const user = JSON.parse(localStorage.getItem('user'));
    const response = await previewFacturePDF(factureId, user.id);
    
    const url = window.URL.createObjectURL(new Blob([response.data]));
    window.open(url, '_blank');
    
    alert("Facture ouverte dans un nouvel onglet");
  } catch (error) {
    alert("Erreur lors de la prévisualisation");
  }
};
```

---

## 🗺 Routes

### Structure des routes

```javascript
// src/App.jsx
function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          {/* Route par défaut */}
          <Route path="/" element={<Navigate to="/login" />} />
          
          {/* Authentification */}
          <Route path="/login" element={<Login />} />
          
          {/* Routes Admin */}
          <Route path="/admin/dashboard" element={<AdminDashboard />} />
          <Route path="/admin/categories" element={<CategoriesAdmin />} />
          <Route path="/admin/produits" element={<ProduitsAdmin />} />
          <Route path="/admin/commandes" element={<CommandesAdmin />} />
          <Route path="/admin/factures" element={<FacturesAdmin />} />
          
          {/* Routes Client */}
          <Route path="/client/dashboard" element={<ClientDashboard />} />
          <Route path="/client/categories" element={<CategoriesClient />} />
          <Route path="/client/produits" element={<ProduitsClient />} />
          <Route path="/client/commandes" element={<CommandesClient />} />
          <Route path="/client/factures" element={<FacturesClient />} />
          <Route path="/client/factures/:id" element={<DetailsFacture />} />
          
          {/* 404 */}
          <Route path="*" element={<Navigate to="/login" />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}
```

### Protection des routes

```javascript
// src/components/ProtectedRoute.jsx
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const ProtectedRoute = ({ children, requiredRole }) => {
  const { user } = useAuth();
  
  if (!user) {
    return <Navigate to="/login" />;
  }
  
  if (requiredRole && user.role !== requiredRole) {
    return <Navigate to={user.role === 'ADMIN' ? '/admin/dashboard' : '/client/dashboard'} />;
  }
  
  return children;
};

// Utilisation
<Route 
  path="/admin/produits" 
  element={
    <ProtectedRoute requiredRole="ADMIN">
      <ProduitsAdmin />
    </ProtectedRoute>
  } 
/>
```

---

## 🔌 API Integration

### Service API complet (`src/api/api.js`)

Toutes les fonctions d'API sont déjà définies :

#### Authentification
- `login(email, motDePasse)` - Connexion
- `register(clientData)` - Inscription

#### Catégories
- `getAllCategoriesAdmin()` - Liste (Admin)
- `getAllCategoriesClient()` - Liste (Client)
- `createCategorie(data)` - Créer
- `updateCategorie(id, data)` - Modifier
- `deleteCategorie(id)` - Supprimer

#### Produits
- `getAllProduitsAdmin()` - Liste (Admin)
- `getAllProduitsClient()` - Liste (Client)
- `createProduit(data)` - Créer
- `updateProduit(id, data)` - Modifier
- `deleteProduit(id)` - Supprimer

#### Commandes
- `getAllCommandesAdmin()` - Liste (Admin)
- `getCommandesClient(clientId)` - Mes commandes
- `creerCommandeAvecProduits(commandeDTO)` - Créer avec produits
- `ajouterProduitACommande(cmdId, prodId, quantite)` - Ajouter produit
- `modifierQuantiteProduit(cmdId, prodId, quantite)` - Modifier quantité
- `supprimerProduitDeCommande(cmdId, prodId)` - Retirer produit
- `annulerCommande(cmdId)` - Annuler
- `changerStatutCommande(id, statut)` - Changer statut (Admin)

#### Factures
- `getAllFacturesAdmin()` - Liste (Admin)
- `getHistoriqueFacturesClient(clientId)` - Mes factures
- `exporterFacturePDFAdmin(factureId)` - Export PDF (Admin)
- `exporterMultipleFacturesPDFAdmin(params)` - Export multiple (Admin)
- `exporterFacturesZIPAdmin(params)` - Export ZIP (Admin)
- `exporterFacturePDF(factureId, clientId)` - Télécharger (Client)
- `previewFacturePDF(factureId, clientId)` - Prévisualiser (Client)

---

## 🔐 Authentification

### Context AuthContext

```javascript
// src/context/AuthContext.jsx
import { createContext, useContext, useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);

  // Charger depuis localStorage
  useEffect(() => {
    const storedUser = localStorage.getItem("user");
    const storedToken = localStorage.getItem("token");
    if (storedUser && storedToken) {
      setUser(JSON.parse(storedUser));
      setToken(storedToken);
    }
  }, []);

  const login = async (email, motDePasse) => {
    const response = await axios.post("http://localhost:8080/api/auth/login", {
      email,
      motDePasse,
    });
    
    const userData = response.data;
    
    localStorage.setItem("token", userData.token);
    localStorage.setItem("user", JSON.stringify(userData));
    
    setUser(userData);
    setToken(userData.token);
    
    // Redirection selon le rôle
    if (userData.role === "ADMIN") {
      navigate("/admin/dashboard");
    } else {
      navigate("/client/dashboard");
    }
  };

  const register = async (clientData) => {
    await axios.post("http://localhost:8080/api/auth/register", clientData);
    alert("Inscription réussie !");
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
    setToken(null);
    navigate("/login");
  };

  return (
    <AuthContext.Provider value={{ user, token, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
```

### Utilisation dans les composants

```javascript
import { useAuth } from '../context/AuthContext';

function MonComposant() {
  const { user, logout } = useAuth();
  
  return (
    <div>
      <p>Bonjour {user?.prenom} {user?.nom}</p>
      <button onClick={logout}>Déconnexion</button>
    </div>
  );
}
```

---

## 📦 Build & Déploiement

### Build de production

```bash
npm run build
```

Les fichiers optimisés seront dans le dossier `dist/`

### Preview du build

```bash
npm run preview
```

### Déploiement

#### Vercel

```bash
npm install -g vercel
vercel
```

#### Netlify

```bash
npm install -g netlify-cli
netlify deploy --prod
```

#### Docker

**Dockerfile** :

```dockerfile
FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

**Build et run** :

```bash
docker build -t facturation-frontend .
docker run -p 3000:80 facturation-frontend
```

---

## 🎨 Personnalisation

### Styles Bootstrap

Modifier `src/styles/custom.css` :

```css
:root {
  --primary-color: #007bff;
  --secondary-color: #6c757d;
  --success-color: #28a745;
  --danger-color: #dc3545;
}

.btn-primary {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
}

.card {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}
```

---

## 🧪 Tests

### Installation des dépendances de test

```bash
npm install --save-dev @testing-library/react @testing-library/jest-dom vitest
```

### Exécuter les tests

```bash
npm run test
```

---

## 📚 Documentation supplémentaire

- **Backend README** : Voir [README-BACKEND.md](../backend/README-BACKEND.md)
- **React Docs** : https://react.dev/
- **Bootstrap Docs** : https://getbootstrap.com/docs/

---

## 🤝 Contribution

1. Fork le projet
2. Créer une branche (`git checkout -b feature/AmazingFeature`)
3. Commit (`git commit -m 'Add AmazingFeature'`)
4. Push (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

---

## 📧 Contact

- **Email** : support@facturation.com
- **GitHub** : [@Manel6barek](https://github.com/Manel6mbarek)

---

## 🔗 Liens utiles

- [Backend Repository](https://github.com/votre-username/facturation-backend)
- [Documentation API](http://localhost:8080/swagger-ui.html)
- [Rapport de bugs](https://github.com/votre-username/facturation-frontend/issues)

---

**Développé avec ❤️ en React & Bootstrap**
