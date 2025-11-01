import React from "react";
import { useNavigate, useLocation } from "react-router-dom";

const Sidebar = ({ role = "client" }) => {
  const navigate = useNavigate();
  const location = useLocation();

  const isActive = (path) => location.pathname === path;

  const adminMenuItems = [
    { path: "/admin/dashboard", icon: "📊", label: "Dashboard" },
    { path: "/admin/categories", icon: "📁", label: "Catégories" },
    { path: "/admin/produits", icon: "📦", label: "Produits" },
    { path: "/admin/commandes", icon: "🛒", label: "Commandes" },
    { path: "/admin/factures", icon: "💰", label: "Factures" },
  ];

  const clientMenuItems = [
    { path: "/client/dashboard", icon: "🏠", label: "Dashboard" },
    { path: "/client/categories", icon: "📁", label: "Catégories" },
    { path: "/client/produits", icon: "📦", label: "Produits" },
    { path: "/client/commandes", icon: "🛒", label: "Mes Commandes" },
    { path: "/client/factures", icon: "💰", label: "Mes Factures" },
  ];

  const menuItems = role === "admin" ? adminMenuItems : clientMenuItems;

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    navigate("/login");
  };

  return (
    <div className="w-64 bg-gradient-to-b from-gray-800 to-gray-900 text-white min-h-screen flex flex-col">
      {/* Header */}
      <div className="p-6 border-b border-gray-700">
        <h2 className="text-2xl font-bold">
          {role === "admin" ? "Admin Panel" : "Client Portal"}
        </h2>
        <p className="text-sm text-gray-400 mt-1">
          {role === "admin" ? "Gestion" : "Espace Client"}
        </p>
      </div>

      {/* Navigation */}
      <nav className="flex-1 p-4 space-y-2">
        {menuItems.map((item) => (
          <button
            key={item.path}
            onClick={() => navigate(item.path)}
            className={`w-full flex items-center space-x-3 px-4 py-3 rounded-lg transition-all ${
              isActive(item.path)
                ? "bg-blue-600 text-white shadow-lg"
                : "text-gray-300 hover:bg-gray-700 hover:text-white"
            }`}
          >
            <span className="text-xl">{item.icon}</span>
            <span className="font-medium">{item.label}</span>
          </button>
        ))}
      </nav>

      {/* User info & Logout */}
      <div className="p-4 border-t border-gray-700">
        <div className="bg-gray-700 rounded-lg p-3 mb-3">
          <p className="text-sm text-gray-400">Connecté en tant que</p>
          <p className="font-semibold">
            {JSON.parse(localStorage.getItem("user") || "{}")?.nom || "Utilisateur"}
          </p>
        </div>
        <button
          onClick={handleLogout}
          className="w-full flex items-center justify-center space-x-2 px-4 py-3 bg-red-600 hover:bg-red-700 rounded-lg transition-colors"
        >
          <span>🚪</span>
          <span className="font-medium">Déconnexion</span>
        </button>
      </div>
    </div>
  );
};

export default Sidebar;