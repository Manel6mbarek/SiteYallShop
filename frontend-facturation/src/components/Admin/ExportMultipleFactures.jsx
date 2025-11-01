import React, { useState } from "react";

const ExportMultipleFactures = ({ factures, onClose }) => {
  const [selectedFactures, setSelectedFactures] = useState([]);
  const [format, setFormat] = useState("PDF");
  const [loading, setLoading] = useState(false);

  const handleSelectAll = () => {
    if (selectedFactures.length === factures.length) {
      setSelectedFactures([]);
    } else {
      setSelectedFactures(factures.map((f) => f.id));
    }
  };

  const handleToggleFacture = (id) => {
    if (selectedFactures.includes(id)) {
      setSelectedFactures(selectedFactures.filter((fId) => fId !== id));
    } else {
      setSelectedFactures([...selectedFactures, id]);
    }
  };

  const handleExport = async () => {
    if (selectedFactures.length === 0) {
      alert("Veuillez sélectionner au moins une facture");
      return;
    }

    setLoading(true);
    try {
      // Simulation d'export
      // Dans la vraie implémentation, vous appelleriez votre API
      // Par exemple : await exportMultipleFactures(selectedFactures, format);
      
      await new Promise((resolve) => setTimeout(resolve, 2000));
      
      alert(
        `Export de ${selectedFactures.length} facture(s) en ${format} réussi !`
      );
      onClose();
    } catch (error) {
      console.error("Erreur export:", error);
      alert("Erreur lors de l'export");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl max-w-3xl w-full max-h-[90vh] overflow-hidden">
        {/* Header */}
        <div className="bg-gradient-to-r from-blue-600 to-blue-700 p-6 text-white">
          <div className="flex justify-between items-center">
            <div>
              <h2 className="text-2xl font-bold">Export Multiple</h2>
              <p className="text-blue-100 mt-1">
                Sélectionnez les factures à exporter
              </p>
            </div>
            <button
              onClick={onClose}
              className="text-white hover:text-gray-200 text-2xl"
              disabled={loading}
            >
              ✕
            </button>
          </div>
        </div>

        {/* Contenu */}
        <div className="p-6">
          {/* Options d'export */}
          <div className="mb-6 flex flex-col md:flex-row gap-4">
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Format d'export
              </label>
              <select
                value={format}
                onChange={(e) => setFormat(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                disabled={loading}
              >
                <option value="PDF">PDF</option>
                <option value="EXCEL">Excel</option>
                <option value="CSV">CSV</option>
              </select>
            </div>
            <div className="flex items-end">
              <button
                onClick={handleSelectAll}
                className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
                disabled={loading}
              >
                {selectedFactures.length === factures.length
                  ? "Désélectionner tout"
                  : "Sélectionner tout"}
              </button>
            </div>
          </div>

          {/* Compteur */}
          <div className="bg-blue-50 border-l-4 border-blue-400 p-4 mb-4">
            <p className="text-sm text-blue-800">
              <span className="font-semibold">
                {selectedFactures.length}
              </span>{" "}
              facture(s) sélectionnée(s) sur {factures.length}
            </p>
          </div>

          {/* Liste des factures */}
          <div className="border rounded-lg overflow-hidden max-h-96 overflow-y-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50 sticky top-0">
                <tr>
                  <th className="px-4 py-3 text-left">
                    <input
                      type="checkbox"
                      checked={selectedFactures.length === factures.length}
                      onChange={handleSelectAll}
                      className="w-4 h-4 text-blue-600 rounded focus:ring-blue-500"
                      disabled={loading}
                    />
                  </th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    N° Facture
                  </th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Client
                  </th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Date
                  </th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Montant
                  </th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Statut
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {factures.map((facture) => (
                  <tr
                    key={facture.id}
                    className={`hover:bg-gray-50 ${
                      selectedFactures.includes(facture.id) ? "bg-blue-50" : ""
                    }`}
                  >
                    <td className="px-4 py-3">
                      <input
                        type="checkbox"
                        checked={selectedFactures.includes(facture.id)}
                        onChange={() => handleToggleFacture(facture.id)}
                        className="w-4 h-4 text-blue-600 rounded focus:ring-blue-500"
                        disabled={loading}
                      />
                    </td>
                    <td className="px-4 py-3 text-sm font-medium text-gray-900">
                      {facture.numeroFacture}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-900">
                      {facture.nomClient}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-500">
                      {new Date(facture.dateFacture).toLocaleDateString("fr-FR")}
                    </td>
                    <td className="px-4 py-3 text-sm font-semibold text-gray-900">
                      {facture.montantTTC?.toFixed(2)} TND
                    </td>
                    <td className="px-4 py-3">
                      <span
                        className={`px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full ${
                          facture.statut === "PAYEE"
                            ? "bg-green-100 text-green-800"
                            : facture.statut === "EN_ATTENTE"
                            ? "bg-yellow-100 text-yellow-800"
                            : "bg-red-100 text-red-800"
                        }`}
                      >
                        {facture.statut}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Footer avec actions */}
        <div className="bg-gray-50 px-6 py-4 flex justify-end gap-3">
          <button
            onClick={onClose}
            className="px-6 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-100 transition-colors"
            disabled={loading}
          >
            Annuler
          </button>
          <button
            onClick={handleExport}
            disabled={loading || selectedFactures.length === 0}
            className={`px-6 py-2 rounded-lg font-medium transition-colors ${
              loading || selectedFactures.length === 0
                ? "bg-gray-300 text-gray-500 cursor-not-allowed"
                : "bg-blue-600 text-white hover:bg-blue-700"
            }`}
          >
            {loading ? (
              <span className="flex items-center">
                <svg
                  className="animate-spin -ml-1 mr-2 h-4 w-4 text-white"
                  fill="none"
                  viewBox="0 0 24 24"
                >
                  <circle
                    className="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    strokeWidth="4"
                  />
                  <path
                    className="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                  />
                </svg>
                Export en cours...
              </span>
            ) : (
              `Exporter ${selectedFactures.length} facture(s)`
            )}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ExportMultipleFactures;