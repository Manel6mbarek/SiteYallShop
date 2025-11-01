import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  getCommandesClient, // ✅ Fonction spécifique pour le client
} from "../../api/axios";
import { LayoutGrid, ShoppingBag, RefreshCw, LogOut, Search } from "lucide-react";
import "./CommandesClient.css";
import { useAuth } from "../../context/AuthContext";

const CommandesClient = () => {
  const { logout } = useAuth();
  const navigate = useNavigate();
  const [commandes, setCommandes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filtreStatut, setFiltreStatut] = useState("TOUS");
  const [triePar, setTriePar] = useState("date_desc");
  const [searchTerm, setSearchTerm] = useState("");

  const user = JSON.parse(localStorage.getItem("user"));
  const clientId = user?.id;

  const statutsDisponibles = [
    "TOUS",
    "EN_ATTENTE",
    "CONFIRMEE",
    "EN_PREPARATION",
    "PRETE",
    "EN_LIVRAISON",
    "LIVREE",
    "ANNULEE",
    "PAYEE",
  ];

  useEffect(() => {
    if (!clientId) {
      setError("Utilisateur non connecté");
      setLoading(false);
      navigate("/login");
      return;
    }
    fetchCommandes();
  }, [clientId]);

  const fetchCommandes = async () => {
    try {
      setLoading(true);
      setError(null);
      
      // ✅ Appel de l'API spécifique pour récupérer les commandes du client
      const response = await getCommandesClient(clientId);
      
      console.log("Commandes reçues:", response.data);
      setCommandes(response.data || []);
    } catch (err) {
      console.error("Erreur chargement commandes:", err);
      setError(err.response?.data?.error || "Erreur lors du chargement des commandes");
      setCommandes([]);
    } finally {
      setLoading(false);
    }
  };

  const getStatutBadgeClass = (statut) => {
    const map = {
      EN_ATTENTE: "badge-warning",
      CONFIRMEE: "badge-info",
      EN_PREPARATION: "badge-purple",
      PRETE: "badge-indigo",
      EN_LIVRAISON: "badge-orange",
      LIVREE: "badge-success",
      ANNULEE: "badge-danger",
      PAYEE: "badge-teal",
    };
    return map[statut] || "badge-secondary";
  };

  const commandesFiltrees = commandes
    .filter(
      (cmd) =>
        (filtreStatut === "TOUS" || cmd.statut === filtreStatut) &&
        (cmd.numeroCommande?.toLowerCase().includes(searchTerm.toLowerCase()) ||
          cmd.statut?.toLowerCase().includes(searchTerm.toLowerCase()))
    )
    .sort((a, b) => {
      switch (triePar) {
        case "date_desc":
          return new Date(b.dateCommande) - new Date(a.dateCommande);
        case "date_asc":
          return new Date(a.dateCommande) - new Date(b.dateCommande);
        case "montant_desc":
          return (b.totalTTC || 0) - (a.totalTTC || 0);
        case "montant_asc":
          return (a.totalTTC || 0) - (b.totalTTC || 0);
        default:
          return 0;
      }
    });

  if (loading) {
    return (
      <div className="text-center py-5">
        <div className="spinner-border text-primary" role="status"></div>
        <p className="mt-3 text-muted">Chargement de vos commandes...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container mt-5">
        <div className="alert alert-danger" role="alert">
          <h4 className="alert-heading">Erreur!</h4>
          <p>{error}</p>
          <hr />
          <button onClick={fetchCommandes} className="btn btn-danger">
            Réessayer
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="commandes-client-page container-fluid">
      {/* HEADER */}
      <div className="page-header">
        <div className="row align-items-center">
          <div className="col">
            <div className="d-flex align-items-center gap-3 mb-2">
              <div className="icon-box gradient-blue">
                <ShoppingBag size={26} />
              </div>
              <h1 className="h2 fw-bold mb-0">Mes Commandes</h1>
            </div>
            <p className="text-muted mb-0">
              Suivez et gérez vos commandes en toute simplicité
            </p>
            {/* Affichage info utilisateur pour debug */}
            <small className="text-success">
              Connecté en tant que: {user?.prenom} {user?.nom} (ID: {clientId})
            </small>
          </div>
          <div className="col-auto">
            <button onClick={logout} className="btn btn-logout">
              <LogOut size={18} className="me-2" />
              Déconnexion
            </button>
          </div>
        </div>
      </div>

      {/* ACTIONS */}
      <div className="action-bar">
        <div className="row g-3 align-items-center">
          <div className="col-md-3">
            <div className="search-box">
              <Search size={18} className="search-icon" />
              <input
                type="text"
                className="form-control search-input"
                placeholder="Rechercher une commande..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
          </div>
          <div className="col-md-3">
            <select
              className="form-select"
              value={filtreStatut}
              onChange={(e) => setFiltreStatut(e.target.value)}
            >
              {statutsDisponibles.map((s) => (
                <option key={s} value={s}>
                  {s === "TOUS" ? "Tous les statuts" : s.replace(/_/g, " ")}
                </option>
              ))}
            </select>
          </div>
          <div className="col-md-3">
            <select
              className="form-select"
              value={triePar}
              onChange={(e) => setTriePar(e.target.value)}
            >
              <option value="date_desc">Date (récent → ancien)</option>
              <option value="date_asc">Date (ancien → récent)</option>
              <option value="montant_desc">Montant (élevé → faible)</option>
              <option value="montant_asc">Montant (faible → élevé)</option>
            </select>
          </div>
          <div className="col-md-3 text-md-end">
            <button onClick={fetchCommandes} className="btn btn-refresh">
              <RefreshCw size={18} className="me-2" />
              Actualiser
            </button>
          </div>
        </div>
      </div>

      {/* LISTE DES COMMANDES */}
      {commandesFiltrees.length === 0 ? (
        <div className="empty-state text-center mt-5">
          <LayoutGrid size={60} className="empty-icon" />
          <h5 className="fw-semibold mt-3 mb-2">Aucune commande trouvée</h5>
          <p className="text-muted">
            {searchTerm
              ? "Essayez avec d'autres critères"
              : "Vous n'avez encore passé aucune commande"}
          </p>
          <button
          onClick={() => navigate("/client/produits")}
            //onClick={() => navigate("/client/commandes/nouvelle")}
            className="btn btn-primary-custom mt-3"
          >
            + Nouvelle commande
          </button>
        </div>
      ) : (
        <div className="row g-4 mt-2">
          {commandesFiltrees.map((cmd) => (
            <div className="col-md-6" key={cmd.id}>
              <div className="commande-card">
                <div className="d-flex justify-content-between align-items-center mb-3">
                  <h5 className="fw-bold text-primary mb-0">
                    Commande #{cmd.numeroCommande}
                  </h5>
                  <span className={`badge ${getStatutBadgeClass(cmd.statut)}`}>
                    {cmd.statut.replace(/_/g, " ")}
                  </span>
                </div>
                <p className="text-muted small mb-3">
                  📅 {new Date(cmd.dateCommande).toLocaleDateString("fr-FR")}  
                </p>
                <div className="commande-infos">
                  <div>
                    <span className="label">Articles :</span>{" "}
                    <strong>{cmd.nombreArticles || 0}</strong>
                  </div>
                  <div>
                    <span className="label">Total TTC :</span>{" "}
                    <strong className="text-success">
                      {cmd.totalTTC?.toFixed(2)} €
                    </strong>
                  </div>
                </div>
                <div className="d-flex gap-2 mt-3">
                  <button
                    className="btn btn-sm btn-outline-primary"
                    onClick={() => navigate(`/client/commandes/${cmd.id}`)}
                  >
                    📋 Détails
                  </button>
                  {cmd.statut === "EN_ATTENTE" && (
                    <button
                      className="btn btn-sm btn-outline-warning"
                      onClick={() => navigate(`/client/commandes/${cmd.id}`)}
                    >
                      ✏️ Modifier
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default CommandesClient;