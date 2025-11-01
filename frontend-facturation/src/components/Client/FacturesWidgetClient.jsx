import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getHistoriqueFacturesClient } from "../../api/axios";

const FacturesWidgetClient = () => {
  const navigate = useNavigate();
  const [factures, setFactures] = useState([]);
  const [loading, setLoading] = useState(true);

  const clientId = JSON.parse(localStorage.getItem("user"))?.id;

  useEffect(() => {
    if (clientId) {
      chargerFactures();
    }
  }, [clientId]);

  const chargerFactures = async () => {
    try {
      const response = await getHistoriqueFacturesClient(clientId);
      // Prendre les 5 dernières factures
      setFactures(response.data.slice(0, 5));
    } catch (error) {
      console.error("Erreur chargement factures:", error);
    } finally {
      setLoading(false);
    }
  };

  // Calculer les statistiques
  const stats = factures.reduce(
    (acc, f) => {
      acc.total++;
      if (f.statut === "PAYEE") acc.payees++;
      if (f.statut === "EN_ATTENTE") {
        acc.enAttente++;
        acc.montantEnAttente += f.montantTTC || 0;
      }
      return acc;
    },
    { total: 0, payees: 0, enAttente: 0, montantEnAttente: 0 }
  );

  if (loading) {
    return (
      <div className="bg-white p-6 rounded-lg shadow animate-pulse">
        <div className="h-6 bg-gray-200 rounded w-1/2 mb-4"></div>
        <div className="space-y-3">
          <div className="h-4 bg-gray-200 rounded"></div>
          <div className="h-4 bg-gray-200 rounded w-5/6"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-lg overflow-hidden">
      {/* Header */}
      <div className="bg-gradient-to-r from-purple-600 to-purple-700 p-4">
        <div className="flex justify-between items-center">
          <h3 className="text-lg font-bold text-white">💰 Mes Factures</h3>
          <button
            onClick={() => navigate("/client/factures")}
            className="px-3 py-1 bg-white text-purple-600 rounded-lg text-sm font-medium hover:bg-purple-50 transition-colors"
          >
            Voir tout
          </button>
        </div>
      </div>

      {/* Contenu */}
      <div className="p-4">
        {/* Statistiques rapides */}
        <div className="grid grid-cols-3 gap-3 mb-4">
          <div className="bg-blue-50 p-3 rounded-lg text-center">
            <div className="text-2xl font-bold text-blue-600">{stats.total}</div>
            <div className="text-xs text-blue-600">Total</div>
          </div>
          <div className="bg-green-50 p-3 rounded-lg text-center">
            <div className="text-2xl font-bold text-green-600">{stats.payees}</div>
            <div className="text-xs text-green-600">Payées</div>
          </div>
          <div className="bg-yellow-50 p-3 rounded-lg text-center">
            <div className="text-2xl font-bold text-yellow-600">
              {stats.enAttente}
            </div>
            <div className="text-xs text-yellow-600">En attente</div>
          </div>
        </div>

        {/* Alerte si factures en attente */}
        {stats.enAttente > 0 && (
          <div className="bg-yellow-50 border-l-4 border-yellow-400 p-3 mb-4">
            <div className="flex items-center">
              <span className="text-yellow-600 mr-2">⚠️</span>
              <div>
                <p className="text-sm font-semibold text-yellow-800">
                  {stats.enAttente} facture{stats.enAttente > 1 ? "s" : ""} en
                  attente
                </p>
                <p className="text-xs text-yellow-700">
                  Montant: {stats.montantEnAttente.toFixed(2)} TND
                </p>
              </div>
            </div>
          </div>
        )}

        {/* Liste des dernières factures */}
        {factures.length > 0 ? (
          <div>
            <h4 className="text-sm font-semibold text-gray-700 mb-2">
              Dernières factures
            </h4>
            <div className="space-y-2">
              {factures.map((facture) => (
                <div
                  key={facture.id}
                  className="flex justify-between items-center p-3 bg-gray-50 rounded-lg hover:bg-gray-100 cursor-pointer transition-colors"
                  onClick={() => navigate(`/client/factures/${facture.id}`)}
                >
                  <div className="flex-1">
                    <p className="text-sm font-medium text-gray-800">
                      {facture.numeroFacture}
                    </p>
                    <p className="text-xs text-gray-500">
                      {new Date(facture.dateFacture).toLocaleDateString("fr-FR")}
                    </p>
                  </div>
                  <div className="text-right">
                    <p className="text-sm font-bold text-gray-800">
                      {facture.montantTTC?.toFixed(2)} TND
                    </p>
                    <span
                      className={`text-xs px-2 py-0.5 rounded-full ${
                        facture.statut === "PAYEE"
                          ? "bg-green-100 text-green-800"
                          : facture.statut === "EN_ATTENTE"
                          ? "bg-yellow-100 text-yellow-800"
                          : "bg-red-100 text-red-800"
                      }`}
                    >
                      {facture.statut}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ) : (
          <div className="text-center py-6">
            <div className="text-5xl mb-3">📄</div>
            <p className="text-sm text-gray-600">Aucune facture disponible</p>
            <button
              onClick={() => navigate("/client/commandes")}
              className="mt-3 text-sm text-purple-600 hover:text-purple-800 font-medium"
            >
              Créer une commande →
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default FacturesWidgetClient;