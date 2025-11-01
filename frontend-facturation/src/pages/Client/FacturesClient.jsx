// components/Client/FacturesClientBootstrap.jsx
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  getHistoriqueFacturesClient,
  exporterFacturePDF,
  previewFacturePDF,
} from "../../api/axios";

const FacturesClient = () => {
  const navigate = useNavigate();
  const [factures, setFactures] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filtreStatut, setFiltreStatut] = useState("TOUS");
  const [searchTerm, setSearchTerm] = useState("");
  const [downloading, setDownloading] = useState(null);
  const [openIndex, setOpenIndex] = useState(null); // ✅ Gestion manuelle du collapse

  const clientId = JSON.parse(localStorage.getItem("user"))?.id;

  useEffect(() => {
    if (clientId) chargerFactures();
  }, [clientId]);

  const chargerFactures = async () => {
    try {
      setLoading(true);
      const response = await getHistoriqueFacturesClient(clientId);
      setFactures(response.data);
    } catch (error) {
      console.error("❌ Erreur chargement factures:", error);
      alert("Erreur lors du chargement des factures");
    } finally {
      setLoading(false);
    }
  };

  const handleTelechargerPDF = async (factureId) => {
    try {
      setDownloading(factureId);
      const response = await exporterFacturePDF(factureId, clientId);

      if (!response.data) throw new Error("Aucune donnée reçue du serveur");
      const blob = new Blob([response.data], { type: "application/pdf" });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = `facture_${factureId}.pdf`;
      link.click();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error(error);
      alert("Erreur lors du téléchargement du PDF");
    } finally {
      setDownloading(null);
    }
  };

  const handlePreviewPDF = async (factureId) => {
    try {
      const response = await previewFacturePDF(factureId, clientId);
      if (!response.data) throw new Error("Aucune donnée reçue du serveur");
      const blob = new Blob([response.data], { type: "application/pdf" });
      const url = window.URL.createObjectURL(blob);
      window.open(url, "_blank");
      setTimeout(() => window.URL.revokeObjectURL(url), 10000);
    } catch (error) {
      console.error(error);
      alert("Erreur lors de la prévisualisation du PDF");
    }
  };

  const facturesFiltrees = factures.filter(
    (f) =>
      (filtreStatut === "TOUS" || f.statut === filtreStatut) &&
      f.numeroFacture?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getBadgeClass = (statut) => {
    switch (statut) {
      case "PAYEE":
        return "bg-success text-white";
      case "EN_ATTENTE":
        return "bg-warning text-dark";
      case "ANNULEE":
        return "bg-danger text-white";
      default:
        return "bg-secondary text-white";
    }
  };

  if (loading)
    return (
      <div className="d-flex justify-content-center align-items-center vh-100">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Chargement...</span>
        </div>
      </div>
    );

  if (!factures || factures.length === 0)
    return (
      <div className="card text-center my-5">
        <div className="card-body">
          <div className="display-1 mb-3">📄</div>
          <h5 className="card-title">Aucune facture</h5>
          <p className="card-text">Vous n'avez pas encore de factures.</p>
        </div>
      </div>
    );

  return (
    <div className="container my-4">
      {/* Filtres */}
      <div className="row mb-3 g-2">
        <div className="col-md-4">
          <input
            type="text"
            placeholder="Rechercher par N° de facture..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="form-control"
          />
        </div>
        <div className="col-md-4">
          <select
            value={filtreStatut}
            onChange={(e) => setFiltreStatut(e.target.value)}
            className="form-select"
          >
            <option value="TOUS">Tous les statuts</option>
            <option value="EN_ATTENTE">En attente</option>
            <option value="PAYEE">Payée</option>
            <option value="ANNULEE">Annulée</option>
          </select>
        </div>
        <div className="col-md-4 text-end">
          <button className="btn btn-secondary" onClick={chargerFactures}>
            🔄 Actualiser
          </button>
        </div>
      </div>

      {/* Liste des factures */}
      <div className="accordion" id="accordionFactures">
        {facturesFiltrees.map((facture, index) => (
          <div className="accordion-item mb-2" key={facture.id}>
            <h2 className="accordion-header">
              <button
                className={`accordion-button ${
                  openIndex === index ? "" : "collapsed"
                }`}
                type="button"
                onClick={() =>
                  setOpenIndex(openIndex === index ? null : index)
                }
              >
                Facture #{facture.numeroFacture} -{" "}
                <span className={`badge ms-2 ${getBadgeClass(facture.statut)}`}>
                  {facture.statut.replace(/_/g, " ")}
                </span>
              </button>
            </h2>

            {/* ✅ Affichage conditionnel React pur */}
            {openIndex === index && (
              <div className="accordion-body border-top">
                <p>Date: {new Date(facture.dateFacture).toLocaleString()}</p>
                <p>Commande: {facture.numeroCommande || "-"}</p>
                <p>Montant HT: {facture.montantHT?.toFixed(2)} TND</p>
                <p>TVA: {facture.montantTVA?.toFixed(2)} TND</p>
                <p>
                  Total TTC:{" "}
                  <strong>{facture.montantTTC?.toFixed(2)} TND</strong>
                </p>
                <div className="d-flex flex-wrap gap-2 mt-2">
                  <button
                    className="btn btn-primary btn-sm"
                    onClick={() => handlePreviewPDF(facture.id)}
                    disabled={downloading === facture.id}
                  >
                    👁️ Voir
                  </button>
                  <button
                    className="btn btn-success btn-sm"
                    onClick={() => handleTelechargerPDF(facture.id)}
                    disabled={downloading === facture.id}
                  >
                    {downloading === facture.id ? (
                      <>
                        <span className="spinner-border spinner-border-sm me-1"></span>
                        Téléchargement...
                      </>
                    ) : (
                      <>📥 PDF</>
                    )}
                  </button>
                  <button
                    className="btn btn-info btn-sm"
                    onClick={() => navigate(`/client/factures/${facture.id}`)}
                  >
                    🔍 Détails
                  </button>
                </div>
              </div>
            )}
          </div>
        ))}
        {facturesFiltrees.length === 0 && (
          <div className="text-center text-muted mt-3">
            Aucune facture ne correspond à vos critères de recherche.
          </div>
        )}
      </div>
    </div>
  );
};

export default FacturesClient;
