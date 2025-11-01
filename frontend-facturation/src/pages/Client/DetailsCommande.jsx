// pages/Client/DetailsCommandeBootstrap.jsx
import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  getCommandeByIdAdmin,
  ajouterProduitACommande,
  supprimerProduitDeCommande,
  modifierQuantiteProduit,
  getAllProduitsClient,
} from "../../api/axios";

const DetailsCommande = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [commande, setCommande] = useState(null);
  const [produits, setProduits] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showAjoutProduit, setShowAjoutProduit] = useState(false);
  const [selectedProduit, setSelectedProduit] = useState(null);
  const [quantite, setQuantite] = useState(1);

  useEffect(() => {
    fetchCommande();
    fetchProduits();
  }, [id]);

  const fetchCommande = async () => {
    try {
      setLoading(true);
      const response = await getCommandeByIdAdmin(id);
      setCommande(response.data);
    } catch (err) {
      console.error("Erreur chargement commande:", err);
      alert("Erreur lors du chargement de la commande");
    } finally {
      setLoading(false);
    }
  };

  const fetchProduits = async () => {
    try {
      const response = await getAllProduitsClient();
      setProduits(response.data);
    } catch (err) {
      console.error("Erreur chargement produits:", err);
    }
  };

  const handleAjouterProduit = async () => {
    if (!selectedProduit || quantite <= 0) {
      alert("Veuillez sélectionner un produit et une quantité valide");
      return;
    }
    try {
      await ajouterProduitACommande(id, selectedProduit, quantite);
      alert("Produit ajouté avec succès");
      setShowAjoutProduit(false);
      setSelectedProduit(null);
      setQuantite(1);
      fetchCommande();
    } catch (err) {
      alert(err.response?.data?.error || "Erreur lors de l'ajout du produit");
    }
  };

  const handleSupprimerProduit = async (produitId) => {
    if (!window.confirm("Voulez-vous vraiment supprimer ce produit?")) return;
    try {
      await supprimerProduitDeCommande(id, produitId);
      alert("Produit supprimé avec succès");
      fetchCommande();
    } catch (err) {
      alert(err.response?.data?.error || "Erreur lors de la suppression");
    }
  };

  const handleModifierQuantite = async (produitId, nouvelleQuantite) => {
    if (nouvelleQuantite <= 0) {
      handleSupprimerProduit(produitId);
      return;
    }
    try {
      await modifierQuantiteProduit(id, produitId, nouvelleQuantite);
      fetchCommande();
    } catch (err) {
      alert(err.response?.data?.error || "Erreur lors de la modification");
    }
  };

  const getBadgeClass = (statut) => {
    const colors = {
      EN_ATTENTE: "bg-warning text-dark",
      CONFIRMEE: "bg-primary text-white",
      EN_PREPARATION: "bg-purple text-white",
      PRETE: "bg-info text-white",
      EN_LIVRAISON: "bg-orange text-white",
      LIVREE: "bg-success text-white",
      ANNULEE: "bg-danger text-white",
      PAYEE: "bg-teal text-white",
    };
    return colors[statut] || "bg-secondary text-white";
  };

  if (loading) return <div className="text-center mt-5">Chargement...</div>;
  if (!commande)
    return (
      <div className="text-center mt-5">
        <h4 className="text-danger">Commande non trouvée</h4>
        <button
          onClick={() => navigate("/client/commandes")}
          className="btn btn-primary mt-3"
        >
          Retour à mes commandes
        </button>
      </div>
    );

  const peutModifier = commande.statut === "EN_ATTENTE";

  return (
    <div className="container my-5">
      {/* Header */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Commande {commande.numeroCommande}</h2>
        <button className="btn btn-link" onClick={() => navigate("/client/commandes")}>
          ← Retour
        </button>
      </div>

      {/* Infos commande */}
      <div className="card mb-4">
        <div className="card-body">
          <div className="row">
            <div className="col-md-4">
              <small className="text-muted">Date de commande</small>
              <p>{new Date(commande.dateCommande).toLocaleString()}</p>
            </div>
            <div className="col-md-4">
              <small className="text-muted">Statut</small>
              <span className={`badge ${getBadgeClass(commande.statut)}`}>
                {commande.statut.replace(/_/g, " ")}
              </span>
            </div>
            <div className="col-md-4">
              <small className="text-muted">Total</small>
              <p className="fw-bold text-success">{commande.totalTTC?.toFixed(2)} €</p>
            </div>
          </div>

          {commande.commentaire && (
            <div className="alert alert-secondary mt-3">
              <small>Commentaire:</small>
              <p>{commande.commentaire}</p>
            </div>
          )}

          {!peutModifier && (
            <div className="alert alert-warning mt-3">
              ⚠️ Cette commande ne peut plus être modifiée (statut: {commande.statut})
            </div>
          )}
        </div>
      </div>

      {/* Produits */}
      <div className="card mb-4">
        <div className="card-body">
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h5>Produits commandés</h5>
            {peutModifier && (
              <button
                className="btn btn-primary"
                onClick={() => setShowAjoutProduit(!showAjoutProduit)}
              >
                {showAjoutProduit ? "Annuler" : "+ Ajouter un produit"}
              </button>
            )}
          </div>

          {showAjoutProduit && (
            <div className="border rounded p-3 mb-4 bg-light">
              <div className="row g-3">
                <div className="col-md-8">
                  <label className="form-label">Sélectionner un produit</label>
                  <select
                    value={selectedProduit || ""}
                    onChange={(e) => setSelectedProduit(Number(e.target.value))}
                    className="form-select"
                  >
                    <option value="">-- Choisir un produit --</option>
                    {produits.map((p) => (
                      <option
                        key={p.id}
                        value={p.id}
                        disabled={p.quantiteStock === 0}
                      >
                        {p.nom} - {p.prixVente?.toFixed(2)} €
                        {p.quantiteStock === 0
                          ? " (Rupture)"
                          : ` (Stock: ${p.quantiteStock})`}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="col-md-4">
                  <label className="form-label">Quantité</label>
                  <input
                    type="number"
                    min="1"
                    value={quantite}
                    onChange={(e) => setQuantite(Number(e.target.value))}
                    className="form-control"
                  />
                </div>
              </div>
              <button
                className="btn btn-success mt-3"
                onClick={handleAjouterProduit}
              >
                Confirmer l'ajout
              </button>
            </div>
          )}

          <div className="table-responsive">
            <table className="table table-hover align-middle">
              <thead className="table-light">
                <tr>
                  <th>Produit</th>
                  <th className="text-end">Prix unitaire</th>
                  <th className="text-center">Quantité</th>
                  <th className="text-end">Sous-total</th>
                  {peutModifier && <th className="text-center">Actions</th>}
                </tr>
              </thead>
              <tbody>
                {commande.lignesCommande?.map((ligne) => (
                  <tr key={ligne.id}>
                    <td>
                      <strong>{ligne.produit?.nom}</strong>
                      <br />
                      <small className="text-muted">{ligne.produit?.description}</small>
                    </td>
                    <td className="text-end">{ligne.prixUnitaire?.toFixed(2)} €</td>
                    <td className="text-center">
                      {peutModifier ? (
                        <div className="d-flex justify-content-center align-items-center gap-1">
                          <button
                            className="btn btn-outline-secondary btn-sm"
                            onClick={() =>
                              handleModifierQuantite(ligne.produit.id, ligne.quantite - 1)
                            }
                          >
                            -
                          </button>
                          <span className="px-2">{ligne.quantite}</span>
                          <button
                            className="btn btn-outline-secondary btn-sm"
                            onClick={() =>
                              handleModifierQuantite(ligne.produit.id, ligne.quantite + 1)
                            }
                          >
                            +
                          </button>
                        </div>
                      ) : (
                        ligne.quantite
                      )}
                    </td>
                    <td className="text-end fw-bold">{ligne.sousTotal?.toFixed(2)} €</td>
                    {peutModifier && (
                      <td className="text-center">
                        <button
                          className="btn btn-sm btn-danger"
                          onClick={() => handleSupprimerProduit(ligne.produit.id)}
                        >
                          Supprimer
                        </button>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
            {!commande.lignesCommande?.length && (
              <div className="text-center py-3 text-muted">Aucun produit dans cette commande</div>
            )}
          </div>

          {/* Totaux */}
          <div className="d-flex justify-content-end mt-3">
            <div className="w-100 w-md-50 w-lg-33">
              <div className="d-flex justify-content-between">
                <span>Sous-total HT</span>
                <span className="fw-semibold">{commande.sousTotal?.toFixed(2)} €</span>
              </div>
              <div className="d-flex justify-content-between">
                <span>TVA ({commande.tauxTVA}%)</span>
                <span className="fw-semibold">{commande.montantTVA?.toFixed(2)} €</span>
              </div>
              <div className="d-flex justify-content-between border-top pt-2 fw-bold fs-5">
                <span>Total TTC</span>
                <span className="text-success">{commande.totalTTC?.toFixed(2)} €</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DetailsCommande;
