import React, { useEffect, useState } from "react";
import { getAllProduitsAdmin, createProduit, deleteProduit } from "../../api/axios";
import { useNavigate } from "react-router-dom";
import {
  Package,
  Plus,
  Trash2,
  Search,
  ArrowLeft,
  Tag,
  DollarSign,
  Grid,
  AlertCircle,
  Edit2
} from "lucide-react";
import "./ProduitsAdmin.css";

export default function ProduitsAdmin() {
  const [produits, setProduits] = useState([]);
  const [nom, setNom] = useState("");
  const [prix, setPrix] = useState("");
  const [categorieId, setCategorieId] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [showForm, setShowForm] = useState(false);
  const token = localStorage.getItem("token");
  const navigate = useNavigate();

  const fetchProduits = async () => {
    try {
      const res = await getAllProduitsAdmin(token);
      setProduits(res.data);
    } catch (err) {
      console.error("Erreur chargement produits:", err);
    }
  };

  const handleAdd = async (e) => {
    e.preventDefault();
    try {
      await createProduit({ nom, prix, categorieId }, token);
      fetchProduits();
      setNom("");
      setPrix("");
      setCategorieId("");
      setShowForm(false);
      alert("Produit ajouté avec succès !");
    } catch {
      alert("Erreur ajout produit");
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm("Supprimer ce produit ?")) {
      try {
        await deleteProduit(id, token);
        fetchProduits();
        alert("Produit supprimé avec succès !");
      } catch {
        alert("Erreur lors de la suppression");
      }
    }
  };

  const handleCancel = () => {
    setNom("");
    setPrix("");
    setCategorieId("");
    setShowForm(false);
  };

  useEffect(() => {
    fetchProduits();
  }, []);

  const filteredProduits = produits.filter((p) =>
    p.nom.toLowerCase().includes(searchTerm.toLowerCase()) ||
    p.categorieNom?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="produits-admin-page">
      <div className="container-fluid">
        {/* Header */}
        <div className="page-header">
          <div className="row align-items-center">
            <div className="col">
              <div className="d-flex align-items-center gap-3 mb-2">
                <div className="icon-box gradient-green">
                  <Package size={28} />
                </div>
                <h1 className="h2 fw-bold mb-0">Gestion des Produits</h1>
              </div>
              <p className="text-muted mb-0">
                Gérez votre catalogue de produits
              </p>
            </div>
            <div className="col-auto">
              <button
                onClick={() => navigate("/admin/dashboard")}
                className="btn btn-back"
              >
                <ArrowLeft size={18} className="me-2" />
                Retour
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
                  placeholder="Rechercher un produit..."
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
                Nouveau produit
              </button>
            </div>
          </div>
        </div>

        {/* Formulaire */}
        {showForm && (
          <div className="form-card">
            <div className="d-flex align-items-center justify-content-between mb-4">
              <h5 className="fw-bold mb-0">Ajouter un produit</h5>
              <button onClick={handleCancel} className="btn btn-sm btn-close-form">
                ✕
              </button>
            </div>

            <form onSubmit={handleAdd}>
              <div className="row g-3">
                <div className="col-md-4">
                  <label className="form-label fw-semibold">
                    <Tag size={16} className="me-2" />
                    Nom du produit <span className="text-danger">*</span>
                  </label>
                  <input
                    type="text"
                    value={nom}
                    onChange={(e) => setNom(e.target.value)}
                    placeholder="Ex: Smartphone XYZ"
                    required
                    className="form-control form-control-custom"
                  />
                </div>

                <div className="col-md-4">
                  <label className="form-label fw-semibold">
                    <DollarSign size={16} className="me-2" />
                    Prix (€) <span className="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    value={prix}
                    onChange={(e) => setPrix(e.target.value)}
                    placeholder="Ex: 299.99"
                    required
                    className="form-control form-control-custom"
                  />
                </div>

                <div className="col-md-4">
                  <label className="form-label fw-semibold">
                    <Grid size={16} className="me-2" />
                    ID Catégorie <span className="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    value={categorieId}
                    onChange={(e) => setCategorieId(e.target.value)}
                    placeholder="Ex: 1"
                    required
                    className="form-control form-control-custom"
                  />
                </div>
              </div>

              <div className="d-flex gap-3 mt-4">
                <button type="submit" className="btn btn-save">
                  <Plus size={18} className="me-2" />
                  Ajouter le produit
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
            <div className="col-md-3">
              <div className="stat-item">
                <h3 className="stat-number">{produits.length}</h3>
                <p className="stat-label">Total produits</p>
              </div>
            </div>
            <div className="col-md-3">
              <div className="stat-item">
                <h3 className="stat-number">{filteredProduits.length}</h3>
                <p className="stat-label">Résultats affichés</p>
              </div>
            </div>
            <div className="col-md-3">
              <div className="stat-item">
                <h3 className="stat-number">
                  {produits.length > 0
                    ? (produits.reduce((sum, p) => sum + parseFloat(p.prix || 0), 0) / produits.length).toFixed(2)
                    : 0}€
                </h3>
                <p className="stat-label">Prix moyen</p>
              </div>
            </div>
            <div className="col-md-3">
              <div className="stat-item">
                <h3 className="stat-number">
                  {new Set(produits.map(p => p.categorieId)).size}
                </h3>
                <p className="stat-label">Catégories utilisées</p>
              </div>
            </div>
          </div>
        </div>

        {/* Table Card */}
        <div className="table-card">
          {filteredProduits.length === 0 ? (
            <div className="empty-state">
              <AlertCircle size={48} className="empty-icon" />
              <h5 className="fw-semibold mt-3 mb-2">Aucun produit trouvé</h5>
              <p className="text-muted mb-3">
                {searchTerm
                  ? "Essayez avec d'autres mots-clés"
                  : "Commencez par ajouter votre premier produit"}
              </p>
              {!searchTerm && (
                <button
                  onClick={() => setShowForm(true)}
                  className="btn btn-primary-custom"
                >
                  <Plus size={20} className="me-2" />
                  Ajouter un produit
                </button>
              )}
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-custom">
                <thead>
                  <tr>
                    <th style={{ width: '5%' }}>#</th>
                    <th style={{ width: '30%' }}>Produit</th>
                    <th style={{ width: '15%' }}>Prix</th>
                    <th style={{ width: '25%' }}>Catégorie</th>
                    <th style={{ width: '10%' }}>ID</th>
                    <th style={{ width: '15%' }} className="text-center">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredProduits.map((p, index) => (
                    <tr key={p.id}>
                      <td className="fw-semibold text-muted">{index + 1}</td>
                      <td>
                        <div className="d-flex align-items-center gap-2">
                          <div className="product-badge">
                            <Package size={16} />
                          </div>
                          <span className="fw-semibold">{p.nom}</span>
                        </div>
                      </td>
                      <td>
                        <span className="price-badge">{parseFloat(p.prix).toFixed(2)} €</span>
                      </td>
                      <td>
                        <span className="category-tag">
                          <Grid size={14} className="me-1" />
                          {p.categorieNom || "Non définie"}
                        </span>
                      </td>
                      <td className="text-muted">#{p.id}</td>
                      <td>
                        <div className="d-flex gap-2 justify-content-center">
                          <button
                            onClick={() => handleDelete(p.id)}
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
}