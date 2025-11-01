// components/Client/HistoriqueCommandesBootstrap.jsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const HistoriqueCommandes = ({ commandes, onRefresh }) => {
  const navigate = useNavigate();
  const [filtreStatut, setFiltreStatut] = useState("TOUS");
  const [triePar, setTriePar] = useState("date_desc");

  const statutsDisponibles = [
    "TOUS",
    "EN_ATTENTE",
    "CONFIRMEE",
    "EN_PREPARATION",
    "PRETE",
    "EN_LIVRAISON",
    "LIVREE",
    "ANNULEE",
    "PAYEE"
  ];

  const getBadgeClass = (statut) => {
    const colors = {
      EN_ATTENTE: "bg-warning text-dark",
      CONFIRMEE: "bg-primary text-white",
      EN_PREPARATION: "bg-purple text-white",
      PRETE: "bg-info text-white",
      EN_LIVRAISON: "bg-orange text-white",
      LIVREE: "bg-success text-white",
      ANNULEE: "bg-danger text-white",
      PAYEE: "bg-teal text-white"
    };
    return colors[statut] || "bg-secondary text-white";
  };

  const getStatutIcon = (statut) => {
    const icons = {
      EN_ATTENTE: "⏳",
      CONFIRMEE: "✓",
      EN_PREPARATION: "📦",
      PRETE: "✔",
      EN_LIVRAISON: "🚚",
      LIVREE: "🎉",
      ANNULEE: "✖",
      PAYEE: "💰"
    };
    return icons[statut] || "•";
  };

  const commandesFiltrees = commandes
    .filter(cmd => filtreStatut === "TOUS" || cmd.statut === filtreStatut)
    .sort((a, b) => {
      switch (triePar) {
        case "date_desc": return new Date(b.dateCommande) - new Date(a.dateCommande);
        case "date_asc": return new Date(a.dateCommande) - new Date(b.dateCommande);
        case "montant_desc": return (b.totalTTC || 0) - (a.totalTTC || 0);
        case "montant_asc": return (a.totalTTC || 0) - (b.totalTTC || 0);
        default: return 0;
      }
    });

  const stats = {
    total: commandes.reduce((sum, cmd) => sum + (cmd.totalTTC || 0), 0),
    enCours: commandes.filter(cmd =>
      ["EN_ATTENTE", "CONFIRMEE", "EN_PREPARATION", "PRETE", "EN_LIVRAISON"].includes(cmd.statut)
    ).length,
    livrees: commandes.filter(cmd => cmd.statut === "LIVREE").length
  };

  if (!commandes || commandes.length === 0) {
    return (
      <div className="card text-center p-5">
        <div className="display-1 mb-3">📦</div>
        <h3 className="mb-2">Aucune commande</h3>
        <p className="text-muted mb-4">Vous n'avez pas encore passé de commande.</p>
        <button
          onClick={() => navigate("/client/commandes/nouvelle")}
          className="btn btn-success"
        >
          Créer ma première commande
        </button>
      </div>
    );
  }

  return (
    <div className="mb-5">
      {/* Statistiques */}
      <div className="row mb-4">
        <div className="col-md-4 mb-3">
          <div className="card text-white bg-primary h-100">
            <div className="card-body text-center">
              <h3 className="card-title">{commandes.length}</h3>
              <p className="card-text">Commandes totales</p>
            </div>
          </div>
        </div>
        <div className="col-md-4 mb-3">
          <div className="card text-white bg-warning h-100">
            <div className="card-body text-center">
              <h3 className="card-title">{stats.enCours}</h3>
              <p className="card-text">En cours</p>
            </div>
          </div>
        </div>
        <div className="col-md-4 mb-3">
          <div className="card text-white bg-success h-100">
            <div className="card-body text-center">
              <h3 className="card-title">{stats.total.toFixed(2)} €</h3>
              <p className="card-text">Montant total</p>
            </div>
          </div>
        </div>
      </div>

      {/* Filtres et tri */}
      <div className="card mb-4 p-3">
        <div className="d-flex flex-column flex-md-row align-items-center justify-content-between gap-3">
          <div className="d-flex align-items-center gap-2">
            <label className="form-label mb-0">Filtrer par statut:</label>
            <select
              value={filtreStatut}
              onChange={e => setFiltreStatut(e.target.value)}
              className="form-select"
            >
              {statutsDisponibles.map(statut => (
                <option key={statut} value={statut}>
                  {statut.replace(/_/g, " ")}
                </option>
              ))}
            </select>
          </div>
          <div className="d-flex align-items-center gap-2">
            <label className="form-label mb-0">Trier par:</label>
            <select
              value={triePar}
              onChange={e => setTriePar(e.target.value)}
              className="form-select"
            >
              <option value="date_desc">Date (Plus récent)</option>
              <option value="date_asc">Date (Plus ancien)</option>
              <option value="montant_desc">Montant (Élevé → Faible)</option>
              <option value="montant_asc">Montant (Faible → Élevé)</option>
            </select>
          </div>
          <button onClick={onRefresh} className="btn btn-outline-secondary">
            🔄 Actualiser
          </button>
        </div>
      </div>

      {/* Liste commandes */}
      <div className="row g-4">
        {commandesFiltrees.map(commande => (
          <div key={commande.id} className="col-12">
            <div className="card h-100 shadow-sm">
              <div className="card-body">
                {/* Header */}
                <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-3">
                  <div>
                    <h5 className="card-title mb-1">Commande #{commande.numeroCommande}</h5>
                    <small className="text-muted">
                      {new Date(commande.dateCommande).toLocaleDateString("fr-FR", {
                        day: "numeric", month: "long", year: "numeric", hour: "2-digit", minute: "2-digit"
                      })}
                    </small>
                  </div>
                  <span className={`badge ${getBadgeClass(commande.statut)} mt-2 mt-md-0`}>
                    {getStatutIcon(commande.statut)} {commande.statut.replace(/_/g, " ")}
                  </span>
                </div>

                {/* Détails */}
                <div className="row text-center text-md-start mb-3">
                  <div className="col-md-3"><small>Articles</small><p>{commande.nombreArticles || 0}</p></div>
                  <div className="col-md-3"><small>Sous-total HT</small><p>{commande.sousTotal?.toFixed(2)} €</p></div>
                  <div className="col-md-3"><small>TVA</small><p>{commande.montantTVA?.toFixed(2)} €</p></div>
                  <div className="col-md-3"><small>Total TTC</small><p className="fw-bold text-success">{commande.totalTTC?.toFixed(2)} €</p></div>
                </div>

                {/* Actions */}
                <div className="d-flex flex-wrap gap-2">
                  <button
                    onClick={() => navigate(`/client/commandes/${commande.id}`)}
                    className="btn btn-primary btn-sm"
                  >
                    Voir les détails
                  </button>
                  {commande.statut === "EN_ATTENTE" && (
                    <button
                      onClick={() => navigate(`/client/commandes/${commande.id}`)}
                      className="btn btn-warning btn-sm"
                    >
                      Modifier la commande
                    </button>
                  )}
                </div>
              </div>
            </div>
          </div>
        ))}
        {commandesFiltrees.length === 0 && (
          <div className="col-12 text-center text-muted p-4">
            Aucune commande ne correspond à vos critères de recherche.
          </div>
        )}
      </div>
    </div>
  );
};

export default HistoriqueCommandes;
