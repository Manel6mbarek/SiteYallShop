import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getDashboardFactures } from "../../api/axios";

const FacturesWidget = () => {
  const navigate = useNavigate();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    chargerDonnees();
  }, []);

  const chargerDonnees = async () => {
    try {
      const response = await getDashboardFactures();
      setData(response.data);
    } catch (error) {
      console.error("Erreur chargement dashboard factures:", error);
    } finally {
      setLoading(false);
    }
  };

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

  if (!data) return null;

  const { statistiques, facturesNonTraitees, dernieresFacturesNonTraitees } = data;

  return (
    <div className="bg-white rounded-lg shadow-lg overflow-hidden">
      {/* Header */}
      <div className="bg-gradient-to-r from-green-600 to-green-700 p-4">
        <div className="flex justify-between items-center">
          <h3 className="text-lg font-bold text-white">💰 Factures</h3>
          <button
            onClick={() => navigate("/admin/factures")}
            className="px-3 py-1 bg-white text-green-600 rounded-lg text-sm font-medium hover:bg-green-50 transition-colors"
          >
            Voir tout
          </button>
        </div>
      </div>

      {/* Statistiques principales */}
      <div className="p-4">
        <div className="grid grid-cols-2 gap-4 mb-4">
          <div className="bg-blue-50 p-3 rounded-lg">
            <div className="text-xs text-blue-600 mb-1">Total Factures</div>
            <div className="text-xl font-bold text-blue-700">
              {statistiques.nombreTotal}
            </div>
          </div>
          <div className="bg-green-50 p-3 rounded-lg">
            <div className="text-xs text-green-600 mb-1">Payées</div>
            <div className="text-xl font-bold text-green-700">
              {statistiques.nombrePayees}
            </div>
          </div>
          <div className="bg-yellow-50 p-3 rounded-lg">
            <div className="text-xs text-yellow-600 mb-1">En Attente</div>
            <div className="text-xl font-bold text-yellow-700">
              {statistiques.nombreEnAttente}
            </div>
          </div>
          <div className="bg-purple-50 p-3 rounded-lg">
            <div className="text-xs text-purple-600 mb-1">Total TTC</div>
            <div className="text-lg font-bold text-purple-700">
              {statistiques.montantTotalTTC?.toFixed(0)} TND
            </div>
          </div>
        </div>

        {/* Alerte factures non traitées */}
        {facturesNonTraitees > 0 && (
          <div className="bg-yellow-50 border-l-4 border-yellow-400 p-3 mb-4">
            <div className="flex items-center">
              <span className="text-yellow-600 mr-2">⚠️</span>
              <div>
                <p className="text-sm font-semibold text-yellow-800">
                  {facturesNonTraitees} facture{facturesNonTraitees > 1 ? "s" : ""} en
                  attente
                </p>
              </div>
            </div>
          </div>
        )}

        {/* Dernières factures non traitées */}
        {dernieresFacturesNonTraitees && dernieresFacturesNonTraitees.length > 0 && (
          <div>
            <h4 className="text-sm font-semibold text-gray-700 mb-2">
              Factures à traiter
            </h4>
            <div className="space-y-2">
              {dernieresFacturesNonTraitees.map((facture) => (
                <div
                  key={facture.id}
                  className="flex justify-between items-center p-2 bg-gray-50 rounded hover:bg-gray-100 cursor-pointer"
                  onClick={() => navigate("/admin/factures")}
                >
                  <div className="flex-1">
                    <p className="text-sm font-medium text-gray-800">
                      {facture.numeroFacture}
                    </p>
                    <p className="text-xs text-gray-500">{facture.nomClient}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-sm font-bold text-gray-800">
                      {facture.montantTTC?.toFixed(2)} TND
                    </p>
                    <span className="text-xs px-2 py-0.5 bg-yellow-100 text-yellow-800 rounded-full">
                      En attente
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Message si aucune facture en attente */}
        {facturesNonTraitees === 0 && (
          <div className="text-center py-4">
            <div className="text-4xl mb-2">✅</div>
            <p className="text-sm text-gray-600">
              Toutes les factures sont traitées
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default FacturesWidget;