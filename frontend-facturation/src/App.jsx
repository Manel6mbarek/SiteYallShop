import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import 'bootstrap/dist/css/bootstrap.min.css';
// Pages existantes
import Login from "./pages/Login";
import AdminDashboard from "./pages/Admin/DashboardAdmin";
import ClientDashboard from "./pages/Client/DashboardClient";
import CategoriesAdmin from "./pages/Admin/CategoriesAdmin";
import CategoriesClient from "./pages/Client/CategoriesClient";
import ProduitsAdmin from "./pages/Admin/ProduitsAdmin";
import ProduitsClient from "./pages/Client/ProduitClient";

// 🆕 Nouvelles pages Commandes
import CommandesAdmin from "./pages/Admin/CommandesAdmin";
import CommandesClient from "./pages/Client/CommandesClient";
import DetailsCommande from "./pages/Client/DetailsCommande";

// 🆕 Pages Factures
import FacturesAdmin from "./pages/Admin/FacturesAdmin";
import FacturesClient from "./pages/Client/FacturesClient";
import DetailsFacture from "./pages/Client/DetailsFacture";

function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          <Route path="/" element={<Navigate to="/login" />} />
          <Route path="/login" element={<Login />} />
          
          {/* Dashboards */}
          <Route path="/admin/dashboard" element={<AdminDashboard />} />
          <Route path="/client/dashboard" element={<ClientDashboard />} />
          
          {/* Catégories */}
          <Route path="/admin/categories" element={<CategoriesAdmin />} />
          <Route path="/client/categories" element={<CategoriesClient />} />
          
          {/* Produits */}
          <Route path="/admin/produits" element={<ProduitsAdmin />} />
          <Route path="/client/produits" element={<ProduitsClient />} />
          
          {/* 🆕 Commandes */}
          <Route path="/admin/commandes" element={<CommandesAdmin />} />
          <Route path="/client/commandes" element={<CommandesClient />} />
          
          {/* 🆕 Factures */}
          <Route path="/admin/factures" element={<FacturesAdmin />} />
          <Route path="/client/factures" element={<FacturesClient />} />
          <Route path="/client/factures/:id" element={<DetailsFacture />} />
          
          <Route path="*" element={<Navigate to="/login" />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;