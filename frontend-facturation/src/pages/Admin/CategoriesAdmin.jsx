// src/pages/Admin/CategoriesAdmin.jsx
import React, { useEffect, useState } from "react";
import {
  getAllCategoriesAdmin,
  createCategorie,
  updateCategorie,
  deleteCategorie,
} from "../../api/axios";
import { useAuth } from "../../context/AuthContext";
import {
  LayoutGrid,
  Plus,
  Edit2,
  Trash2,
  Save,
  X,
  LogOut,
  Search,
  AlertCircle
} from "lucide-react";
import "./CategoriesAdmin.css";

const CategoriesAdmin = () => {
  const { logout } = useAuth();
  const [categories, setCategories] = useState([]);
  const [form, setForm] = useState({ id: null, nom: "", description: "" });
  const [isEditing, setIsEditing] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [showForm, setShowForm] = useState(false);

  const loadCategories = async () => {
    try {
      const res = await getAllCategoriesAdmin();
      setCategories(res.data);
    } catch {
      alert("Erreur lors du chargement des catégories");
    }
  };

  useEffect(() => {
    loadCategories();
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (isEditing) {
        await updateCategorie(form.id, form);
        alert("Catégorie modifiée !");
      } else {
        await createCategorie(form);
        alert("Catégorie créée !");
      }
      setForm({ id: null, nom: "", description: "" });
      setIsEditing(false);
      setShowForm(false);
      loadCategories();
    } catch {
      alert("Erreur lors de l'enregistrement");
    }
  };

  const handleEdit = (cat) => {
    setForm(cat);
    setIsEditing(true);
    setShowForm(true);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleDelete = async (id) => {
    if (window.confirm("Supprimer cette catégorie ?")) {
      try {
        await deleteCategorie(id);
        loadCategories();
      } catch {
        alert("Erreur lors de la suppression");
      }
    }
  };

  const handleCancel = () => {
    setForm({ id: null, nom: "", description: "" });
    setIsEditing(false);
    setShowForm(false);
  };

  const filteredCategories = categories.filter((cat) =>
    cat.nom.toLowerCase().includes(searchTerm.toLowerCase()) ||
    cat.description?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="categories-admin-page">
      <div className="container-fluid">
        {/* Header */}
        <div className="page-header">
          <div className="row align-items-center">
            <div className="col">
              <div className="d-flex align-items-center gap-3 mb-2">
                <div className="icon-box gradient-blue">
                  <LayoutGrid size={28} />
                </div>
                <h1 className="h2 fw-bold mb-0">Gestion des catégories</h1>
              </div>
              <p className="text-muted mb-0">
                Gérez toutes vos catégories de produits
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

        {/* Action Bar */}
        <div className="action-bar">
          <div className="row g-3 align-items-center">
            <div className="col-md-6">
              <div className="search-box">
                <Search size={20} className="search-icon" />
                <input
                  type="text"
                  className="form-control search-input"
                  placeholder="Rechercher une catégorie..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>
            </div>
            <div className="col-md-6 text-md-end">
              <button
                onClick={() => setShowForm(!showForm)}
                className="btn btn-primary-custom"
              >
                <Plus size={20} className="me-2" />
                Nouvelle catégorie
              </button>
            </div>
          </div>
        </div>

        {/* Formulaire (Collapse) */}
        {showForm && (
          <div className="form-card">
            <div className="d-flex align-items-center justify-content-between mb-4">
              <h5 className="fw-bold mb-0">
                {isEditing ? "Modifier la catégorie" : "Créer une catégorie"}
              </h5>
              <button onClick={handleCancel} className="btn btn-sm btn-close-form">
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleSubmit}>
              <div className="row g-3">
                <div className="col-md-6">
                  <label className="form-label fw-semibold">
                    Nom de la catégorie <span className="text-danger">*</span>
                  </label>
                  <input
                    name="nom"
                    value={form.nom}
                    onChange={handleChange}
                    placeholder="Ex: Électronique"
                    required
                    className="form-control form-control-custom"
                  />
                </div>

                <div className="col-md-6">
                  <label className="form-label fw-semibold">Description</label>
                  <input
                    name="description"
                    value={form.description}
                    onChange={handleChange}
                    placeholder="Ex: Produits électroniques"
                    className="form-control form-control-custom"
                  />
                </div>
              </div>

              <div className="d-flex gap-3 mt-4">
                <button type="submit" className="btn btn-save">
                  <Save size={18} className="me-2" />
                  {isEditing ? "Mettre à jour" : "Créer"}
                </button>
                <button
                  type="button"
                  onClick={handleCancel}
                  className="btn btn-cancel"
                >
                  Annuler
                </button>
              </div>
            </form>
          </div>
        )}

        {/* Stats Card */}
        <div className="stats-card mb-4">
          <div className="row text-center">
            <div className="col-md-4">
              <div className="stat-item">
                <h3 className="stat-number">{categories.length}</h3>
                <p className="stat-label">Total catégories</p>
              </div>
            </div>
            <div className="col-md-4">
              <div className="stat-item">
                <h3 className="stat-number">{filteredCategories.length}</h3>
                <p className="stat-label">Résultats affichés</p>
              </div>
            </div>
            <div className="col-md-4">
              <div className="stat-item">
                <h3 className="stat-number">
                  {categories.filter(c => c.description).length}
                </h3>
                <p className="stat-label">Avec description</p>
              </div>
            </div>
          </div>
        </div>

        {/* Table Card */}
        <div className="table-card">
          {filteredCategories.length === 0 ? (
            <div className="empty-state">
              <AlertCircle size={48} className="empty-icon" />
              <h5 className="fw-semibold mt-3 mb-2">Aucune catégorie trouvée</h5>
              <p className="text-muted mb-3">
                {searchTerm
                  ? "Essayez avec d'autres mots-clés"
                  : "Commencez par créer votre première catégorie"}
              </p>
              {!searchTerm && (
                <button
                  onClick={() => setShowForm(true)}
                  className="btn btn-primary-custom"
                >
                  <Plus size={20} className="me-2" />
                  Créer une catégorie
                </button>
              )}
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-custom">
                <thead>
                  <tr>
                    <th style={{ width: '5%' }}>#</th>
                    <th style={{ width: '25%' }}>Nom</th>
                    <th style={{ width: '45%' }}>Description</th>
                    <th style={{ width: '25%' }} className="text-center">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredCategories.map((cat, index) => (
                    <tr key={cat.id}>
                      <td className="fw-semibold text-muted">{index + 1}</td>
                      <td>
                        <div className="d-flex align-items-center gap-2">
                          <div className="category-badge">
                            <LayoutGrid size={16} />
                          </div>
                          <span className="fw-semibold">{cat.nom}</span>
                        </div>
                      </td>
                      <td className="text-muted">
                        {cat.description || <em className="text-muted">Aucune description</em>}
                      </td>
                      <td>
                        <div className="d-flex gap-2 justify-content-center">
                          <button
                            onClick={() => handleEdit(cat)}
                            className="btn btn-action btn-edit"
                            title="Modifier"
                          >
                            <Edit2 size={16} />
                          </button>
                          <button
                            onClick={() => handleDelete(cat.id)}
                            className="btn btn-action btn-delete"
                            title="Supprimer"
                          >
                            <Trash2 size={16} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default CategoriesAdmin;