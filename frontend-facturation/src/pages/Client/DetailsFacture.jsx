// components/Client/DetailsFactureBootstrap.jsx
import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  getFactureByIdClient,
  exporterFacturePDF,
  previewFacturePDF,
} from "../../api/axios";

const DetailsFacture = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [facture, setFacture] = useState(null);
  const [loading, setLoading] = useState(true);

  const clientId = JSON.parse(localStorage.getItem("user"))?.id;

  useEffect(() => {
    if (clientId && id) chargerFacture();
  }, [id, clientId]);

  const chargerFacture = async () => {
    try {
      setLoading(true);
      const response = await getFactureByIdClient(id, clientId);
      setFacture(response.data);
    } catch (error) {
      console.error(error);
      alert("Erreur lors du chargement de la facture");
      navigate("/client/factures");
    } finally {
      setLoading(false);
    }
  };

  const handleTelechargerPDF = async () => {
    try {
      const response = await exporterFacturePDF(id, clientId);
      const blob = new Blob([response.data], { type: "application/pdf" });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = `facture_${facture.numeroFacture}.pdf`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error(error);
      alert("Erreur lors du téléchargement");
    }
  };

  const handlePreviewPDF = async () => {
    try {
      const response = await previewFacturePDF(id, clientId);
      const blob = new Blob([response.data], { type: "application/pdf" });
      const url = window.URL.createObjectURL(blob);
      window.open(url, "_blank");
    } catch (error) {
      console.error(error);
      alert("Erreur lors de la prévisualisation");
    }
  };

  if (loading)
    return (
      <div className="d-flex justify-content-center align-items-center vh-100">
        <h5>Chargement...</h5>
      </div>
    );

  if (!facture)
    return (
      <div className="d-flex justify-content-center align-items-center vh-100">
        <div className="text-center">
          <div className="display-1 mb-3">❌</div>
          <h5>Facture introuvable</h5>
          <button
            className="btn btn-primary mt-3"
            onClick={() => navigate("/client/factures")}
          >
            Retour aux factures
          </button>
        </div>
      </div>
    );

  const getBadgeClass = (statut) => {
    switch (statut) {
      case "PAYEE": return "bg-success text-white";
      case "EN_ATTENTE": return "bg-warning text-dark";
      case "ANNULEE": return "bg-danger text-white";
      default: return "bg-secondary text-white";
    }
  };

  return (
    <div className="container my-4">
      <button className="btn btn-link mb-3" onClick={() => navigate("/client/factures")}>
        ← Retour aux factures
      </button>

      <div className="card shadow-sm">
        <div className="card-header bg-primary text-white d-flex justify-content-between align-items-center">
          <div>
            <h5 className="card-title mb-0">Facture {facture.numeroFacture}</h5>
            <small>Date: {new Date(facture.dateFacture).toLocaleString()}</small>
          </div>
          <span className={`badge ${getBadgeClass(facture.statut)} fs-6`}>
            {facture.statut.replace(/_/g, " ")}
          </span>
        </div>

        <div className="card-body">
          <div className="row mb-3">
            <div className="col-md-6 mb-2">
              <h6>Informations Client</h6>
              <p className="mb-1"><strong>Nom:</strong> {facture.nomClient || "N/A"}</p>
              <p className="mb-1"><strong>Email:</strong> {facture.emailClient || "N/A"}</p>
              {facture.telephoneClient && <p className="mb-1"><strong>Téléphone:</strong> {facture.telephoneClient}</p>}
            </div>
            <div className="col-md-6 mb-2">
              <h6>Informations Commande</h6>
              <p className="mb-1"><strong>N° Commande:</strong> {facture.numeroCommande || "N/A"}</p>
              {facture.statutCommande && <p className="mb-1"><strong>Statut Commande:</strong> {facture.statutCommande}</p>}
              {facture.datePaiement && <p className="mb-1"><strong>Date Paiement:</strong> {new Date(facture.datePaiement).toLocaleDateString()}</p>}
            </div>
          </div>

          <h6>Détails des montants</h6>
          <ul className="list-group mb-3">
            <li className="list-group-item d-flex justify-content-between">
              <span>Montant HT</span>
              <span>{facture.montantHT?.toFixed(2)} TND</span>
            </li>
            <li className="list-group-item d-flex justify-content-between">
              <span>TVA</span>
              <span>{facture.montantTVA?.toFixed(2)} TND</span>
            </li>
            <li className="list-group-item d-flex justify-content-between fw-bold">
              <span>Total TTC</span>
              <span>{facture.montantTTC?.toFixed(2)} TND</span>
            </li>
          </ul>

          {facture.modePaiement && (
            <div className="alert alert-info">
              💳 <strong>Mode de paiement:</strong> {facture.modePaiement.replace(/_/g, " ")}
            </div>
          )}

          <div className="d-flex flex-wrap gap-2">
            <button className="btn btn-primary" onClick={handlePreviewPDF}>
              👁️ Prévisualiser PDF
            </button>
            <button className="btn btn-success" onClick={handleTelechargerPDF}>
              📥 Télécharger PDF
            </button>
          </div>

          <small className="text-muted d-block mt-3">
            Créé le: {new Date(facture.dateCreation).toLocaleString()}<br/>
            {facture.dateModification && <>Dernière modification: {new Date(facture.dateModification).toLocaleString()}</>}
          </small>
        </div>
      </div>
    </div>
  );
};

export default DetailsFacture;
