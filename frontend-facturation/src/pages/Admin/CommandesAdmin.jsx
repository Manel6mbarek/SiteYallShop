// pages/Admin/CommandesAdmin.jsx
import React, { useState, useEffect } from "react";
import { getAllCommandesAdmin, changerStatutCommande, changerModePaiement, getCommandeByIdAdmin } from "../../api/axios";
import {
  ShoppingCart,
  RefreshCw,
  Eye,
  Calendar,
  User,
  Package,
  CreditCard,
  MessageSquare,
  TrendingUp,
  X
} from "lucide-react";
import "./CommandesAdmin.css";

const CommandesAdmin = () => {
  const [commandes, setCommandes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedCommande, setSelectedCommande] = useState(null);
  const [showModal, setShowModal] = useState(false);

  const statutsCommande = [
    "EN_ATTENTE",
    "CONFIRMEE",
    "EN_PREPARATION",
    "PRETE",
    "EN_LIVRAISON",
    "LIVREE",
    "ANNULEE",
    "PAYEE"
  ];

  const modesPaiement = [
    "ESPECES",
    "CARTE_BANCAIRE",
    "CHEQUE",
    "VIREMENT",
    "PAYPAL"
  ];

  useEffect(() => {
    fetchCommandes();
  }, []);

  const fetchCommandes = async () => {
    try {
      setLoading(true);
      const response = await getAllCommandesAdmin();
      setCommandes(response.data);
      setError(null);
    } catch (err) {
      setError("Erreur lors du chargement des commandes");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleChangerStatut = async (id, nouveauStatut) => {
    try {
      await changerStatutCommande(id, nouveauStatut);
      fetchCommandes();
      alert("Statut modifié avec succès");
    } catch (err) {
      alert("Erreur lors du changement de statut");
      console.error(err);
    }
  };

  const handleChangerModePaiement = async (id, nouveauMode) => {
    try {
      await changerModePaiement(id, nouveauMode);
      fetchCommandes();
      alert("Mode de paiement modifié avec succès");
    } catch (err) {
      alert("Erreur lors du changement de mode de paiement");
      console.error(err);
    }
  };

  const handleVoirDetails = async (id) => {
    try {
      const response = await getCommandeByIdAdmin(id);
      setSelectedCommande(response.data);
      setShowModal(true);
    } catch (err) {
      alert("Erreur lors du chargement des détails");
      console.error(err);
    }
  };

  const getStatutBadgeClass = (statut) => {
    const classes = {
      EN_ATTENTE: "badge-warning",
      CONFIRMEE: "badge-info",
      EN_PREPARATION: "badge-purple",
      PRETE: "badge-indigo",
      EN_LIVRAISON: "badge-orange",
      LIVREE: "badge-success",
      ANNULEE: "badge-danger",
      PAYEE: "badge-teal"
    };
    return classes[statut] || "badge-secondary";
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Chargement...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="commandes-admin-page">
      <div className="container-fluid">
        {/* Header */}
        <div className="page-header">
          <div className="row align-items-center">
            <div className="col">
              <div className="d-flex align-items-center gap-3 mb-2">
                <div className="icon-box gradient-orange">
                  <ShoppingCart size={28} />
                </div>
                <h1 className="h2 fw-bold mb-0">Gestion des Commandes</h1>
              </div>
              <p className="text-muted mb-0">
                Suivez et gérez toutes les commandes clients
              </p>
            </div>
            <div className="col-auto">
              <button onClick={fetchCommandes} className="btn btn-refresh">
                <RefreshCw size={18} className="me-2" />
                Actualiser
              </button>
            </div>
          </div>
        </div>

        {/* Error Alert */}
        {error && (
          <div className="alert alert-danger alert-dismissible fade show" role="alert">
            <strong>Erreur!</strong> {error}
            <button type="button" className="btn-close" onClick={() => setError(null)}></button>
          </div>
        )}

        {/* Stats Cards */}
        <div className="row g-3 g-lg-4 mb-4">
          <div className="col-12 col-sm-6 col-lg-3">
            <div className="stat-card">
              <div className="stat-icon bg-primary-light">
                <ShoppingCart size={24} className="text-primary" />
              </div>
              <div>
                <p className="stat-label">Total commandes</p>
                <h3 className="stat-value">{commandes.length}</h3>
              </div>
            </div>
          </div>
          <div className="col-12 col-sm-6 col-lg-3">
            <div className="stat-card">
              <div className="stat-icon bg-warning-light">
                <TrendingUp size={24} className="text-warning" />
              </div>
              <div>
                <p className="stat-label">En attente</p>
                <h3 className="stat-value">
                  {commandes.filter(c => c.statut === 'EN_ATTENTE').length}
                </h3>
              </div>
            </div>
          </div>
          <div className="col-12 col-sm-6 col-lg-3">
            <div className="stat-card">
              <div className="stat-icon bg-success-light">
                <Package size={24} className="text-success" />
              </div>
              <div>
                <p className="stat-label">Livrées</p>
                <h3 className="stat-value">
                  {commandes.filter(c => c.statut === 'LIVREE').length}
                </h3>
              </div>
            </div>
          </div>
          <div className="col-12 col-sm-6 col-lg-3">
            <div className="stat-card">
              <div className="stat-icon bg-info-light">
                <CreditCard size={24} className="text-info" />
              </div>
              <div>
                <p className="stat-label">Chiffre d'affaires</p>
                <h3 className="stat-value">
                  {commandes.reduce((sum, c) => sum + (c.totalTTC || 0), 0).toFixed(2)}€
                </h3>
              </div>
            </div>
          </div>
        </div>

        {/* Table Card */}
        <div className="table-card">
          <div className="table-responsive">
            <table className="table table-custom">
              <thead>
                <tr>
                  <th style={{ width: '15%' }}>N° Commande</th>
                  <th style={{ width: '15%' }}>Client</th>
                  <th style={{ width: '12%' }}>Date</th>
                  <th style={{ width: '20%' }}>Statut</th>
                  <th style={{ width: '12%' }}>Total TTC</th>
                  <th style={{ width: '10%' }} className="text-center">Articles</th>
                  <th style={{ width: '16%' }} className="text-center">Actions</th>
                </tr>
              </thead>
              <tbody>
                {commandes.length === 0 ? (
                  <tr>
                    <td colSpan="7" className="text-center py-5">
                      <Package size={48} className="text-muted mb-3" />
                      <p className="text-muted mb-0">Aucune commande trouvée</p>
                    </td>
                  </tr>
                ) : (
                  commandes.map((commande) => (
                    <tr key={commande.id}>
                      <td className="fw-semibold">{commande.numeroCommande}</td>
                      <td>
                        <div className="d-flex align-items-center gap-2">
                          <User size={16} className="text-muted" />
                          <span>{commande.client?.nom} {commande.client?.prenom}</span>
                        </div>
                      </td>
                      <td className="text-muted small">
                        {new Date(commande.dateCommande).toLocaleDateString('fr-FR')}
                      </td>
                      <td>
                        <select
                          value={commande.statut}
                          onChange={(e) => handleChangerStatut(commande.id, e.target.value)}
                          className={`form-select form-select-sm status-badge ${getStatutBadgeClass(commande.statut)}`}
                        >
                          {statutsCommande.map((statut) => (
                            <option key={statut} value={statut}>
                              {statut.replace(/_/g, " ")}
                            </option>
                          ))}
                        </select>
                      </td>
                      <td className="fw-bold">{commande.totalTTC?.toFixed(2)} €</td>
                      <td className="text-center">
                        <span className="badge badge-count">
                          {commande.nombreArticles || 0}
                        </span>
                      </td>
                      <td className="text-center">
                        <button
                          onClick={() => handleVoirDetails(commande.id)}
                          className="btn btn-action btn-view"
                          title="Voir détails"
                        >
                          <Eye size={16} />
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Modal Détails */}
      {showModal && selectedCommande && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content-custom" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header-custom">
              <h3 className="modal-title">
                <ShoppingCart size={24} className="me-2" />
                Commande {selectedCommande.numeroCommande}
              </h3>
              <button onClick={() => setShowModal(false)} className="btn-close-modal">
                <X size={24} />
              </button>
            </div>

            <div className="modal-body-custom">
              <div className="row g-4 mb-4">
                <div className="col-md-6">
                  <div className="info-box">
                    <User size={20} className="info-icon" />
                    <div>
                      <p className="info-label">Client</p>
                      <p className="info-value">
                        {selectedCommande.client?.nom} {selectedCommande.client?.prenom}
                      </p>
                      <p className="info-subtext">{selectedCommande.client?.email}</p>
                    </div>
                  </div>
                </div>
                <div className="col-md-6">
                  <div className="info-box">
                    <Calendar size={20} className="info-icon" />
                    <div>
                      <p className="info-label">Date de commande</p>
                      <p className="info-value">
                        {new Date(selectedCommande.dateCommande).toLocaleString('fr-FR')}
                      </p>
                    </div>
                  </div>
                </div>
                <div className="col-md-6">
                  <div className="info-box">
                    <Package size={20} className="info-icon" />
                    <div>
                      <p className="info-label">Statut</p>
                      <select
                        value={selectedCommande.statut}
                        onChange={(e) => {
                          handleChangerStatut(selectedCommande.id, e.target.value);
                          setShowModal(false);
                        }}
                        className={`form-select status-badge ${getStatutBadgeClass(selectedCommande.statut)}`}
                      >
                        {statutsCommande.map((statut) => (
                          <option key={statut} value={statut}>
                            {statut.replace(/_/g, " ")}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>
                </div>
                <div className="col-md-6">
                  <div className="info-box">
                    <CreditCard size={20} className="info-icon" />
                    <div>
                      <p className="info-label">Mode de paiement</p>
                      <select
                        onChange={(e) => {
                          handleChangerModePaiement(selectedCommande.id, e.target.value);
                          setShowModal(false);
                        }}
                        className="form-select"
                      >
                        <option value="">Sélectionner...</option>
                        {modesPaiement.map((mode) => (
                          <option key={mode} value={mode}>
                            {mode.replace(/_/g, " ")}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>
                </div>
              </div>

              {selectedCommande.commentaire && (
                <div className="comment-box mb-4">
                  <MessageSquare size={20} className="me-2" />
                  <div>
                    <p className="info-label">Commentaire</p>
                    <p className="mb-0">{selectedCommande.commentaire}</p>
                  </div>
                </div>
              )}

              <h5 className="fw-bold mb-3">Articles commandés</h5>
              <div className="table-responsive mb-4">
                <table className="table table-bordered">
                  <thead>
                    <tr>
                      <th>Produit</th>
                      <th className="text-end">Prix unitaire</th>
                      <th className="text-center">Quantité</th>
                      <th className="text-end">Sous-total</th>
                    </tr>
                  </thead>
                  <tbody>
                    {selectedCommande.lignesCommande?.map((ligne) => (
                      <tr key={ligne.id}>
                        <td>{ligne.produit?.nom}</td>
                        <td className="text-end">{ligne.prixUnitaire?.toFixed(2)} €</td>
                        <td className="text-center">{ligne.quantite}</td>
                        <td className="text-end fw-bold">{ligne.sousTotal?.toFixed(2)} €</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              <div className="totals-box">
                <div className="d-flex justify-content-between mb-2">
                  <span>Sous-total HT</span>
                  <span className="fw-semibold">{selectedCommande.sousTotal?.toFixed(2)} €</span>
                </div>
                <div className="d-flex justify-content-between mb-3">
                  <span>TVA ({selectedCommande.tauxTVA}%)</span>
                  <span className="fw-semibold">{selectedCommande.montantTVA?.toFixed(2)} €</span>
                </div>
                <div className="d-flex justify-content-between total-line">
                  <span className="h5 mb-0">Total TTC</span>
                  <span className="h5 mb-0 fw-bold">{selectedCommande.totalTTC?.toFixed(2)} €</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CommandesAdmin;