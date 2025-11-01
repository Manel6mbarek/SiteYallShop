import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: { "Content-Type": "application/json" },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// ============================
// 📁 ADMIN — CATÉGORIES
// ============================

export const getAllCategoriesAdmin = () => api.get("/admin/categories");
export const getCategorieByIdAdmin = (id) => api.get(`/admin/categories/${id}`);
export const createCategorie = (data) => api.post("/admin/categories", data);
export const updateCategorie = (id, data) => api.put(`/admin/categories/${id}`, data);
export const deleteCategorie = (id) => api.delete(`/admin/categories/${id}`);

// ============================
// 📁 CLIENT — CATÉGORIES
// ============================

export const getAllCategoriesClient = () => api.get("/clients/categories");
export const getCategorieByIdClient = (id) => api.get(`/clients/categories/${id}`);

// ============================
// 🛠 ADMIN — PRODUITS
// ============================

export const getAllProduitsAdmin = () => api.get("/admin/produits");
export const getProduitByIdAdmin = (id) => api.get(`/admin/produits/${id}`);
export const createProduit = (data) => api.post("/admin/produits", data);
export const updateProduit = (id, data) => api.put(`/admin/produits/${id}`, data);
export const deleteProduit = (id) => api.delete(`/admin/produits/${id}`);

// ============================
// 🛒 CLIENT — PRODUITS
// ============================

export const getAllProduitsClient = () => api.get("/clients/produits");
export const getProduitByIdClient = (id) => api.get(`/clients/produits/${id}`);
export const getProduitsClient = (categorieId) => 
  api.get("/clients/produits", { params: { categorieId } });

// ============================
// 🛠 ADMIN — COMMANDES
// ============================

export const getAllCommandesAdmin = () => api.get("/admin/commandes");
export const getCommandeByIdAdmin = (id) => api.get(`/admin/commandes/${id}`);
export const changerStatutCommande = (id, statut) => 
  api.patch(`/admin/commandes/${id}/statut`, null, { params: { statut } });
export const changerModePaiement = (id, modePaiement) => 
  api.patch(`/admin/commandes/${id}/mode-paiement`, null, { params: { modePaiement } });

// ============================
// 🛒 CLIENT — COMMANDES
// ============================

export const getCommandesClient = (clientId) => 
  api.get(`/client/commandes/mes-commandes/${clientId}`);

export const getCommandeClientById = (commandeId, clientId) => 
  api.get(`/client/commandes/${commandeId}/client/${clientId}`);

export const creerCommandeClient = (idClient) => 
  api.post(`/client/commandes/${idClient}`);

export const creerCommandeAvecProduits = (commandeDTO) => {
  const storedUser = localStorage.getItem('user');
  
  if (!storedUser) {
    console.error("❌ Aucun utilisateur trouvé dans localStorage");
    return Promise.reject(new Error('Client non authentifié'));
  }

  const user = JSON.parse(storedUser);
  const clientId = user?.id;
  
  if (!clientId) {
    console.error("❌ ID client introuvable");
    return Promise.reject(new Error('ID client introuvable'));
  }

  console.log("📤 Création commande pour client:", clientId);
  console.log("📦 Données:", commandeDTO);
  
  return api.post(
    `/client/commandes/creer-avec-produits?idClient=${clientId}`, 
    commandeDTO
  );
};

export const ajouterProduitACommande = (idCommande, idProduit, quantite) => {
  const user = JSON.parse(localStorage.getItem('user'));
  const clientId = user?.id;
  
  return api.post(`/client/commandes/${idCommande}/produits/${idProduit}`, null, { 
    params: { quantite, idClient: clientId } 
  });
};

export const supprimerProduitDeCommande = (idCommande, idProduit) => {
  const user = JSON.parse(localStorage.getItem('user'));
  const clientId = user?.id;
  
  return api.delete(`/client/commandes/${idCommande}/produits/${idProduit}`, {
    params: { idClient: clientId }
  });
};

export const modifierQuantiteProduit = (idCommande, idProduit, nouvelleQuantite) => {
  const user = JSON.parse(localStorage.getItem('user'));
  const clientId = user?.id;
  
  return api.put(`/client/commandes/${idCommande}/produits/${idProduit}`, null, { 
    params: { nouvelleQuantite, idClient: clientId } 
  });
};

export const annulerCommande = (idCommande) => {
  const user = JSON.parse(localStorage.getItem('user'));
  const clientId = user?.id;
  
  return api.put(`/client/commandes/${idCommande}/annuler`, null, {
    params: { idClient: clientId }
  });
};

// ============================
// 📋 LIGNES COMMANDE - ADMIN
// ============================

export const getAllLignesCommandeAdmin = () => 
  api.get("/admin/lignes-commande");

export const getLigneCommandeByIdAdmin = (id) => 
  api.get(`/admin/lignes-commande/${id}`);

export const getLignesByCommandeAdmin = (commandeId) => 
  api.get(`/admin/lignes-commande/commande/${commandeId}`);

export const creerLigneCommandeAdmin = (ligneDTO) => 
  api.post("/admin/lignes-commande", ligneDTO);

export const updateLigneCommandeAdmin = (id, ligneDTO) => 
  api.put(`/admin/lignes-commande/${id}`, ligneDTO);

export const deleteLigneCommandeAdmin = (id) => 
  api.delete(`/admin/lignes-commande/${id}`);

// ============================
// 📋 LIGNES COMMANDE - CLIENT
// ============================

export const getLignesCommandeClient = (commandeId, clientId) => 
  api.get(`/clients/lignes-commande/commande/${commandeId}`, { 
    params: { clientId } 
  });

export const creerLigneCommandeClient = (ligneDTO, clientId) => 
  api.post("/clients/lignes-commande", ligneDTO, { 
    params: { clientId } 
  });

export const updateLigneCommandeClient = (id, ligneDTO, clientId) => 
  api.put(`/clients/lignes-commande/${id}`, ligneDTO, { 
    params: { clientId } 
  });

export const deleteLigneCommandeClient = (id, clientId) => 
  api.delete(`/clients/lignes-commande/${id}`, { 
    params: { clientId } 
  });

// ============================
// 💰 ADMIN — FACTURES
// ============================

/**
 * Récupérer toutes les factures (Admin)
 */
export const getAllFacturesAdmin = () => 
  api.get("/admin/factures");

/**
 * Récupérer une facture par ID (Admin)
 */
export const getFactureByIdAdmin = (id) => 
  api.get(`/admin/factures/${id}`);

/**
 * Marquer une facture comme payée (Admin)
 */
export const marquerFacturePayee = (id, modePaiement) => 
  api.patch(`/admin/factures/${id}/marquer-payee`, null, { 
    params: { modePaiement } 
  });

/**
 * Récupérer les statistiques des factures (Admin)
 */
export const getStatistiquesFactures = () => 
  api.get("/admin/factures/statistiques");

/**
 * Récupérer les données du dashboard factures (Admin)
 */
export const getDashboardFactures = () => 
  api.get("/admin/factures/dashboard");

// ============================
// 🆕 EXPORT PDF — ADMIN
// ============================

/**
 * ✅ NOUVEAU: Exporter une seule facture en PDF (Admin)
 * @param {number} factureId - ID de la facture
 */
export const exporterFacturePDFAdmin = (factureId) => {
  console.log("📥 Admin Export PDF:", factureId);
  
  return api.get(`/admin/factures/${factureId}/pdf`, {
    responseType: "blob",
    timeout: 30000,
  }).then(response => {
    console.log("✅ PDF Admin reçu:", {
      size: response.data.size,
      type: response.data.type
    });
    return response;
  }).catch(error => {
    console.error("❌ Erreur export PDF Admin:", error);
    throw error;
  });
};

/**
 * ✅ NOUVEAU: Exporter plusieurs factures dans un seul PDF (Admin)
 * @param {object} params - Paramètres de filtrage
 * @param {string} params.statut - Statut des factures (optionnel)
 * @param {string} params.dateDebut - Date de début (optionnel)
 * @param {string} params.dateFin - Date de fin (optionnel)
 * @param {number} params.clientId - ID du client (optionnel)
 * @param {Array<number>} params.factureIds - Liste d'IDs de factures (optionnel)
 */
export const exporterMultipleFacturesPDFAdmin = (params = {}) => {
  console.log("📥 Admin Export Multiple PDF:", params);
  
  const queryParams = new URLSearchParams();
  
  if (params.statut) queryParams.append('statut', params.statut);
  if (params.dateDebut) queryParams.append('dateDebut', params.dateDebut);
  if (params.dateFin) queryParams.append('dateFin', params.dateFin);
  if (params.clientId) queryParams.append('clientId', params.clientId);
  if (params.factureIds && params.factureIds.length > 0) {
    params.factureIds.forEach(id => queryParams.append('factureIds', id));
  }
  
  return api.get(`/admin/factures/export/multiple?${queryParams.toString()}`, {
    responseType: "blob",
    timeout: 60000, // 60 secondes pour plusieurs factures
  }).then(response => {
    console.log("✅ PDF Multiple reçu:", {
      size: response.data.size,
      type: response.data.type
    });
    return response;
  }).catch(error => {
    console.error("❌ Erreur export multiple PDF:", error);
    throw error;
  });
};

/**
 * ✅ NOUVEAU: Exporter plusieurs factures dans un ZIP (Admin)
 * @param {object} params - Paramètres de filtrage (mêmes que exporterMultipleFacturesPDFAdmin)
 */
export const exporterFacturesZIPAdmin = (params = {}) => {
  console.log("📥 Admin Export ZIP:", params);
  
  const queryParams = new URLSearchParams();
  
  if (params.statut) queryParams.append('statut', params.statut);
  if (params.dateDebut) queryParams.append('dateDebut', params.dateDebut);
  if (params.dateFin) queryParams.append('dateFin', params.dateFin);
  if (params.clientId) queryParams.append('clientId', params.clientId);
  if (params.factureIds && params.factureIds.length > 0) {
    params.factureIds.forEach(id => queryParams.append('factureIds', id));
  }
  
  return api.get(`/admin/factures/export/zip?${queryParams.toString()}`, {
    responseType: "blob",
    timeout: 90000, // 90 secondes pour ZIP
  }).then(response => {
    console.log("✅ ZIP reçu:", {
      size: response.data.size,
      type: response.data.type
    });
    return response;
  }).catch(error => {
    console.error("❌ Erreur export ZIP:", error);
    throw error;
  });
};

// ============================
// 💰 CLIENT — FACTURES
// ============================

/**
 * Récupérer l'historique des factures d'un client
 */
export const getHistoriqueFacturesClient = (clientId) =>
  api.get(`/clients/factures/historique?clientId=${clientId}`);

/**
 * Récupérer une facture par ID (Client)
 */
export const getFactureByIdClient = (id, clientId) => 
  api.get(`/clients/factures/${id}?clientId=${clientId}`);

/**
 * Exporter une facture en PDF (Client)
 */
export const exporterFacturePDF = (factureId, clientId) => {
  console.log("📥 API Call - Export PDF:", { factureId, clientId });
  
  return api.get(`/clients/factures/${factureId}/pdf`, {
    params: { clientId },
    responseType: "blob",
    timeout: 30000,
  }).then(response => {
    console.log("✅ PDF reçu:", {
      size: response.data.size,
      type: response.data.type,
      headers: response.headers
    });
    return response;
  }).catch(error => {
    console.error("❌ Erreur export PDF:", {
      message: error.message,
      response: error.response?.data,
      status: error.response?.status
    });
    throw error;
  });
};

/**
 * Prévisualiser une facture en PDF (Client)
 */
export const previewFacturePDF = (factureId, clientId) => {
  console.log("👁️ API Call - Preview PDF:", { factureId, clientId });
  
  return api.get(`/clients/factures/${factureId}/pdf/preview`, {
    params: { clientId },
    responseType: "blob",
    timeout: 30000,
  }).then(response => {
    console.log("✅ Preview PDF reçu:", {
      size: response.data.size,
      type: response.data.type
    });
    return response;
  }).catch(error => {
    console.error("❌ Erreur preview PDF:", {
      message: error.message,
      status: error.response?.status
    });
    throw error;
  });
};
  
export default api;