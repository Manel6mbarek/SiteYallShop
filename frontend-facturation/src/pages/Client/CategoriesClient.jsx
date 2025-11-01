import React, { useEffect, useState } from "react";
import { getAllCategoriesClient } from "../../api/axios";
import { useAuth } from "../../context/AuthContext";
import { LayoutGrid, LogOut, AlertCircle } from "lucide-react";
import "./CategoriesClient.css";

const CategoriesClient = () => {
  const { logout } = useAuth();
  const [categories, setCategories] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");

  const loadCategories = async () => {
    try {
      const res = await getAllCategoriesClient();
      setCategories(res.data);
    } catch {
      alert("Erreur lors du chargement des catégories");
    }
  };

  useEffect(() => {
    loadCategories();
  }, []);

  const filteredCategories = categories.filter((cat) =>
    cat.nom.toLowerCase().includes(searchTerm.toLowerCase()) ||
    cat.description?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="categories-client-page container-fluid">
      {/* Header */}
      <div className="page-header">
        <div className="row align-items-center">
          <div className="col">
            <div className="d-flex align-items-center gap-3 mb-2">
              <div className="icon-box gradient-green">
                <LayoutGrid size={28} />
              </div>
              <h1 className="h2 fw-bold mb-0">Catégories disponibles</h1>
            </div>
            <p className="text-muted mb-0">
              Découvrez toutes les catégories de produits
            </p>
          </div>
          <div className="col-auto">
            <button onClick={logout} className="btn btn-logout">
              <LogOut size={18} className="me-2" />
              Déconnexion
            </button>
          </div>
        </div>
      </div>

      {/* Barre de recherche */}
      <div className="action-bar mb-4">
        <div className="row g-3 align-items-center">
          <div className="col-md-6 mx-auto">
            <div className="search-box">
              <input
                type="text"
                className="form-control search-input"
                placeholder="Rechercher une catégorie..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
          </div>
        </div>
      </div>

      {/* Cartes de catégories */}
      <div className="categories-grid row g-4">
        {filteredCategories.length === 0 ? (
          <div className="empty-state text-center">
            <AlertCircle size={48} className="empty-icon" />
            <h5 className="fw-semibold mt-3 mb-2">Aucune catégorie trouvée</h5>
            <p className="text-muted">
              {searchTerm
                ? "Essayez avec d'autres mots-clés"
                : "Aucune catégorie disponible pour le moment"}
            </p>
          </div>
        ) : (
          filteredCategories.map((cat) => (
            <div key={cat.id} className="col-md-4 col-sm-6">
              <div className="category-card">
                <div className="icon-circle">
                  <LayoutGrid size={22} />
                </div>
                <h5 className="category-title">{cat.nom}</h5>
                <p className="category-desc">
                  {cat.description || "Aucune description disponible."}
                </p>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default CategoriesClient;
