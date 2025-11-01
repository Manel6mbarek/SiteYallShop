// src/pages/Client/ProduitsClient.jsx
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getAllProduitsClient, creerCommandeAvecProduits } from "../../api/axios";
import { ShoppingCart, Plus, Minus, X, Search, LogOut, Trash2 } from "lucide-react";
import { useAuth } from "../../context/AuthContext";

const ProduitsClient = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  
  const [produits, setProduits] = useState([]);
  const [panier, setPanier] = useState([]);
  const [loading, setLoading] = useState(false);
  const [loadingProduits, setLoadingProduits] = useState(true);
  const [commentaire, setCommentaire] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [showPanier, setShowPanier] = useState(false);

  // Récupérer clientId
  const clientId = user?.id || JSON.parse(localStorage.getItem("user"))?.id;

  useEffect(() => {
    if (!clientId) {
      alert("Vous devez être connecté");
      navigate("/login");
      return;
    }
    fetchProduits();
    chargerPanier();
  }, [clientId]);

  useEffect(() => {
    sauvegarderPanier();
  }, [panier]);

  const fetchProduits = async () => {
    try {
      setLoadingProduits(true);
      const response = await getAllProduitsClient();
      setProduits(response.data || []);
    } catch (err) {
      console.error("Erreur chargement produits:", err);
      alert("Erreur lors du chargement des produits");
    } finally {
      setLoadingProduits(false);
    }
  };

  const chargerPanier = () => {
    const stored = localStorage.getItem("panier");
    if (stored) {
      setPanier(JSON.parse(stored));
    }
  };

  const sauvegarderPanier = () => {
    localStorage.setItem("panier", JSON.stringify(panier));
  };

  const handleAjouterAuPanier = (produit) => {
    const existant = panier.find((p) => p.id === produit.id);
    
    if (existant) {
      setPanier(
        panier.map((p) =>
          p.id === produit.id ? { ...p, quantite: p.quantite + 1 } : p
        )
      );
    } else {
      setPanier([
        ...panier,
        {
          id: produit.id,
          nom: produit.nom,
          prix: produit.prixVente,
          quantite: 1,
          description: produit.description,
        },
      ]);
    }
    
    // Afficher le panier automatiquement
    setShowPanier(true);
  };

  const handleRetirerDuPanier = (produitId) => {
    setPanier(panier.filter((p) => p.id !== produitId));
  };

  const handleModifierQuantite = (produitId, delta) => {
    setPanier(
      panier.map((p) => {
        if (p.id === produitId) {
          const nouvelleQuantite = p.quantite + delta;
          return nouvelleQuantite > 0 ? { ...p, quantite: nouvelleQuantite } : null;
        }
        return p;
      }).filter(Boolean)
    );
  };

  const viderPanier = () => {
    if (window.confirm("Voulez-vous vraiment vider le panier ?")) {
      setPanier([]);
      localStorage.removeItem("panier");
    }
  };

  const calculerTotal = () => {
    return panier.reduce((total, item) => total + item.prix * item.quantite, 0);
  };

  const handleValiderCommande = async () => {
    if (!clientId) {
      alert("Vous devez être connecté");
      navigate("/login");
      return;
    }

    if (panier.length === 0) {
      alert("Votre panier est vide");
      return;
    }

    try {
      setLoading(true);

      const commandeDTO = {
        client: { id: clientId },
        commentaire: commentaire.trim() || "",
        tauxTVA: 20,
        lignesCommande: panier.map((item) => ({
          quantite: item.quantite,
          prixUnitaire: item.prix,
          produit: { id: item.id },
        })),
      };

      console.log("📤 Envoi commande:", commandeDTO);

      const response = await creerCommandeAvecProduits(commandeDTO);
      
      console.log("✅ Commande créée:", response.data);

      alert(`✅ Commande créée avec succès !\nNuméro: ${response.data.numeroCommande}`);
      
      // Nettoyer et rediriger
      setPanier([]);
      setCommentaire("");
      localStorage.removeItem("panier");
      navigate("/client/commandes");
      
    } catch (err) {
      console.error("❌ Erreur:", err);
      const errorMsg = err.response?.data?.error || err.message || "Erreur lors de la création";
      alert(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  const produitsFiltres = produits.filter((p) =>
    p.nom.toLowerCase().includes(searchTerm.toLowerCase()) ||
    p.description?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const totalArticles = panier.reduce((sum, item) => sum + item.quantite, 0);

  return (
    <div className="container-fluid p-4" style={{ backgroundColor: "#f8f9fa", minHeight: "100vh" }}>
      {/* HEADER */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h1 className="h2 fw-bold mb-1">
            <ShoppingCart size={32} className="me-2" />
            Nos Produits
          </h1>
          <p className="text-muted">Parcourez notre catalogue et ajoutez vos articles au panier</p>
        </div>
        <div className="d-flex gap-2">
          <button
            onClick={() => navigate("/client/commandes")}
            className="btn btn-outline-primary"
          >
            📋 Mes commandes
          </button>
          <button onClick={logout} className="btn btn-danger">
            <LogOut size={18} className="me-1" />
            Déconnexion
          </button>
        </div>
      </div>

      <div className="row g-4">
        {/* SECTION PRODUITS */}
        <div className={`col-lg-${showPanier ? "8" : "12"}`}>
          <div className="card shadow-sm">
            <div className="card-body">
              {/* Barre de recherche */}
              <div className="mb-3 position-relative">
                <Search
                  size={20}
                  className="position-absolute"
                  style={{ left: "15px", top: "50%", transform: "translateY(-50%)", color: "#999" }}
                />
                <input
                  type="text"
                  className="form-control ps-5"
                  placeholder="Rechercher un produit..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>

              {/* Liste des produits */}
              {loadingProduits ? (
                <div className="text-center py-5">
                  <div className="spinner-border text-primary"></div>
                  <p className="mt-2 text-muted">Chargement des produits...</p>
                </div>
              ) : produitsFiltres.length === 0 ? (
                <div className="text-center py-5 text-muted">
                  <p>Aucun produit trouvé</p>
                </div>
              ) : (
                <div className="row g-3" style={{ maxHeight: "70vh", overflowY: "auto" }}>
                  {produitsFiltres.map((produit) => (
                    <div className="col-md-6 col-xl-4" key={produit.id}>
                      <div className="card h-100 border-0 shadow-sm hover-shadow">
                        <div className="card-body">
                          <div className="d-flex justify-content-between align-items-start mb-2">
                            <h5 className="card-title fw-bold mb-0">{produit.nom}</h5>
                            <span
                              className={`badge ${
                                produit.quantiteStock > 10
                                  ? "bg-success"
                                  : produit.quantiteStock > 0
                                  ? "bg-warning"
                                  : "bg-danger"
                              }`}
                            >
                              Stock: {produit.quantiteStock}
                            </span>
                          </div>
                          
                          {produit.description && (
                            <p className="card-text text-muted small mb-3">
                              {produit.description}
                            </p>
                          )}
                          
                          <div className="d-flex justify-content-between align-items-center">
                            <span className="h4 mb-0 text-success fw-bold">
                              {produit.prixVente?.toFixed(2)} €
                            </span>
                            <button
                              className="btn btn-primary btn-sm"
                              disabled={produit.quantiteStock === 0}
                              onClick={() => handleAjouterAuPanier(produit)}
                            >
                              <Plus size={16} className="me-1" />
                              Ajouter
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>

        {/* SECTION PANIER */}
        {showPanier && (
          <div className="col-lg-4">
            <div className="card shadow-sm sticky-top" style={{ top: "20px" }}>
              <div className="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                <h5 className="mb-0">
                  <ShoppingCart size={20} className="me-2" />
                  Mon Panier ({totalArticles})
                </h5>
                <button
                  className="btn btn-sm btn-light"
                  onClick={() => setShowPanier(false)}
                  title="Masquer le panier"
                >
                  <X size={18} />
                </button>
              </div>

              <div className="card-body">
                {panier.length === 0 ? (
                  <div className="text-center py-5 text-muted">
                    <ShoppingCart size={48} className="mb-3 opacity-25" />
                    <p>Votre panier est vide</p>
                  </div>
                ) : (
                  <>
                    {/* Articles du panier */}
                    <div style={{ maxHeight: "300px", overflowY: "auto" }} className="mb-3">
                      {panier.map((item) => (
                        <div key={item.id} className="border-bottom pb-2 mb-2">
                          <div className="d-flex justify-content-between align-items-start mb-1">
                            <h6 className="mb-0 small fw-bold">{item.nom}</h6>
                            <button
                              className="btn btn-sm btn-outline-danger p-1"
                              onClick={() => handleRetirerDuPanier(item.id)}
                              title="Retirer"
                            >
                              <X size={14} />
                            </button>
                          </div>
                          
                          <div className="d-flex justify-content-between align-items-center">
                            <div className="btn-group btn-group-sm">
                              <button
                                className="btn btn-outline-secondary"
                                onClick={() => handleModifierQuantite(item.id, -1)}
                              >
                                <Minus size={14} />
                              </button>
                              <button className="btn btn-outline-secondary" disabled>
                                {item.quantite}
                              </button>
                              <button
                                className="btn btn-outline-secondary"
                                onClick={() => handleModifierQuantite(item.id, 1)}
                              >
                                <Plus size={14} />
                              </button>
                            </div>
                            <span className="fw-bold text-success">
                              {(item.prix * item.quantite).toFixed(2)} €
                            </span>
                          </div>
                        </div>
                      ))}
                    </div>

                    {/* Commentaire */}
                    <textarea
                      className="form-control mb-3"
                      rows="2"
                      placeholder="Commentaire (optionnel)"
                      value={commentaire}
                      onChange={(e) => setCommentaire(e.target.value)}
                    />

                    {/* Totaux */}
                    <div className="border-top pt-3 mb-3">
                      <div className="d-flex justify-content-between mb-1">
                        <span>Sous-total HT :</span>
                        <span>{calculerTotal().toFixed(2)} €</span>
                      </div>
                      <div className="d-flex justify-content-between mb-1">
                        <span>TVA (20%) :</span>
                        <span>{(calculerTotal() * 0.2).toFixed(2)} €</span>
                      </div>
                      <div className="d-flex justify-content-between fw-bold text-success">
                        <span>Total TTC :</span>
                        <span className="h5 mb-0">
                          {(calculerTotal() * 1.2).toFixed(2)} €
                        </span>
                      </div>
                    </div>

                    {/* Boutons d'action */}
                    <button
                      className="btn btn-success w-100 mb-2"
                      onClick={handleValiderCommande}
                      disabled={loading}
                    >
                      {loading ? (
                        <>
                          <span className="spinner-border spinner-border-sm me-2"></span>
                          Création...
                        </>
                      ) : (
                        <>✅ Valider la commande</>
                      )}
                    </button>

                    <button
                      className="btn btn-outline-danger w-100 btn-sm"
                      onClick={viderPanier}
                    >
                      <Trash2 size={16} className="me-1" />
                      Vider le panier
                    </button>
                  </>
                )}
              </div>
            </div>
          </div>
        )}
      </div>

      {/* BOUTON FLOTTANT PANIER (si masqué) */}
      {!showPanier && panier.length > 0 && (
        <button
          className="btn btn-primary btn-lg rounded-circle shadow-lg position-fixed"
          style={{ bottom: "30px", right: "30px", width: "60px", height: "60px" }}
          onClick={() => setShowPanier(true)}
        >
          <ShoppingCart size={24} />
          <span
            className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger"
          >
            {totalArticles}
          </span>
        </button>
      )}

      <style jsx>{`
        .hover-shadow {
          transition: transform 0.2s, box-shadow 0.2s;
        }
        .hover-shadow:hover {
          transform: translateY(-5px);
          box-shadow: 0 8px 16px rgba(0,0,0,0.15) !important;
        }
      `}</style>
    </div>
  );
};

export default ProduitsClient;