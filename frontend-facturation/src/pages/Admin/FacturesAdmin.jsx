import React, { useState, useEffect } from "react";
import {
  getAllFacturesAdmin,
  marquerFacturePayee,
  getStatistiquesFactures,
  exporterFacturePDFAdmin,
  exporterMultipleFacturesPDFAdmin,
  exporterFacturesZIPAdmin,
} from "../../api/axios";
import {
  FileText,
  Search,
  Download,
  CheckCircle,
  Clock,
  DollarSign,
  Filter,
  Calendar,
  FileArchive,
  X
} from "lucide-react";

const FacturesAdmin = () => {
  const [factures, setFactures] = useState([]);
  const [statistiques, setStatistiques] = useState(null);
  const [loading, setLoading] = useState(true);
  const [filtreStatut, setFiltreStatut] = useState("TOUS");
  const [searchTerm, setSearchTerm] = useState("");
  const [dateDebut, setDateDebut] = useState("");
  const [dateFin, setDateFin] = useState("");
  const [showExportModal, setShowExportModal] = useState(false);
  const [selectedFactures, setSelectedFactures] = useState([]);

  useEffect(() => {
    chargerDonnees();
  }, []);

  const chargerDonnees = async () => {
    try {
      setLoading(true);
      const [facturesRes, statsRes] = await Promise.all([
        getAllFacturesAdmin(),
        getStatistiquesFactures(),
      ]);
      setFactures(facturesRes.data);
      setStatistiques(statsRes.data);
    } catch (error) {
      console.error("Erreur chargement:", error);
      alert("Erreur lors du chargement des données");
    } finally {
      setLoading(false);
    }
  };

  const handleMarquerPayee = async (id) => {
    const modePaiement = prompt(
      "Mode de paiement ?\n1. ESPECES\n2. CARTE_BANCAIRE\n3. CHEQUE\n4. VIREMENT"
    );
    const modes = ["", "ESPECES", "CARTE_BANCAIRE", "CHEQUE", "VIREMENT"];
    const mode = modes[parseInt(modePaiement)] || "ESPECES";

    try {
      await marquerFacturePayee(id, mode);
      alert("Facture marquée comme payée !");
      chargerDonnees();
    } catch (error) {
      console.error("Erreur:", error);
      alert("Erreur lors de la mise à jour");
    }
  };

  const handleExportSingle = async (id) => {
    try {
      const response = await exporterFacturePDFAdmin(id);
      const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `facture_${id}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("Erreur export PDF:", error);
      alert("Erreur lors de l'export du PDF");
    }
  };

  const handleExportMultiple = async () => {
    try {
      const params = {};
      
      if (selectedFactures.length > 0) {
        params.factureIds = selectedFactures;
      } else {
        if (filtreStatut !== 'TOUS') params.statut = filtreStatut;
        if (dateDebut) params.dateDebut = dateDebut;
        if (dateFin) params.dateFin = dateFin;
      }
      
      const response = await exporterMultipleFacturesPDFAdmin(params);
      const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `factures_${new Date().toISOString().split('T')[0]}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
      
      setShowExportModal(false);
    } catch (error) {
      console.error("Erreur export multiple:", error);
      alert("Erreur lors de l'export multiple");
    }
  };

  const handleExportZip = async () => {
    try {
      const params = {};
      
      if (selectedFactures.length > 0) {
        params.factureIds = selectedFactures;
      } else {
        if (filtreStatut !== 'TOUS') params.statut = filtreStatut;
        if (dateDebut) params.dateDebut = dateDebut;
        if (dateFin) params.dateFin = dateFin;
      }
      
      const response = await exporterFacturesZIPAdmin(params);
      const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/zip' }));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `factures_${new Date().toISOString().split('T')[0]}.zip`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
      
      setShowExportModal(false);
    } catch (error) {
      console.error("Erreur export ZIP:", error);
      alert("Erreur lors de l'export ZIP");
    }
  };

  const toggleSelectFacture = (id) => {
    setSelectedFactures(prev => 
      prev.includes(id) ? prev.filter(fId => fId !== id) : [...prev, id]
    );
  };

  const toggleSelectAll = () => {
    if (selectedFactures.length === facturesFiltrees.length) {
      setSelectedFactures([]);
    } else {
      setSelectedFactures(facturesFiltrees.map(f => f.id));
    }
  };

  const facturesFiltrees = factures.filter((f) => {
    const matchStatut = filtreStatut === "TOUS" || f.statut === filtreStatut;
    const matchSearch =
      f.numeroFacture?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      f.nomClient?.toLowerCase().includes(searchTerm.toLowerCase());
    
    let matchDate = true;
    if (dateDebut || dateFin) {
      const factureDate = new Date(f.dateFacture);
      if (dateDebut && factureDate < new Date(dateDebut)) matchDate = false;
      if (dateFin && factureDate > new Date(dateFin + 'T23:59:59')) matchDate = false;
    }
    
    return matchStatut && matchSearch && matchDate;
  });

  if (loading) {
    return (
      <div style={{display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh'}}>
        <div style={{textAlign: 'center'}}>
          <div style={{width: '40px', height: '40px', border: '4px solid #f3f4f6', borderTop: '4px solid #3b82f6', borderRadius: '50%', animation: 'spin 1s linear infinite', margin: '0 auto'}}></div>
          <p style={{marginTop: '16px', color: '#6b7280'}}>Chargement...</p>
        </div>
      </div>
    );
  }

  return (
    <div style={{padding: '24px', backgroundColor: '#f9fafb', minHeight: '100vh'}}>
      <div style={{maxWidth: '1400px', margin: '0 auto'}}>
        {/* Header */}
        <div style={{marginBottom: '32px'}}>
          <div style={{display: 'flex', alignItems: 'center', gap: '16px', marginBottom: '8px'}}>
            <div style={{padding: '12px', background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', borderRadius: '12px', color: 'white'}}>
              <FileText size={28} />
            </div>
            <div>
              <h1 style={{fontSize: '28px', fontWeight: 'bold', margin: 0, color: '#111827'}}>Gestion des Factures</h1>
              <p style={{margin: 0, color: '#6b7280'}}>Administration des factures clients</p>
            </div>
          </div>
        </div>

        {/* Statistiques */}
        {statistiques && (
          <div style={{display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px', marginBottom: '24px'}}>
            <div style={{background: 'white', padding: '24px', borderRadius: '12px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)'}}>
              <div style={{display: 'flex', alignItems: 'center', gap: '16px'}}>
                <div style={{padding: '12px', backgroundColor: '#f3f4f6', borderRadius: '10px'}}>
                  <FileText size={24} color="#6b7280" />
                </div>
                <div>
                  <p style={{margin: 0, fontSize: '14px', color: '#6b7280'}}>Total Factures</p>
                  <h3 style={{margin: '4px 0 0 0', fontSize: '28px', fontWeight: 'bold'}}>{statistiques.totalFactures}</h3>
                </div>
              </div>
            </div>
            
            <div style={{background: 'white', padding: '24px', borderRadius: '12px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)'}}>
              <div style={{display: 'flex', alignItems: 'center', gap: '16px'}}>
                <div style={{padding: '12px', backgroundColor: '#d1fae5', borderRadius: '10px'}}>
                  <CheckCircle size={24} color="#10b981" />
                </div>
                <div>
                  <p style={{margin: 0, fontSize: '14px', color: '#6b7280'}}>Payées</p>
                  <h3 style={{margin: '4px 0 0 0', fontSize: '28px', fontWeight: 'bold'}}>{statistiques.facturesPayees}</h3>
                  <p style={{margin: '4px 0 0 0', fontSize: '13px', color: '#10b981'}}>{statistiques.chiffreAffairesTotal?.toFixed(2)} TND</p>
                </div>
              </div>
            </div>
            
            <div style={{background: 'white', padding: '24px', borderRadius: '12px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)'}}>
              <div style={{display: 'flex', alignItems: 'center', gap: '16px'}}>
                <div style={{padding: '12px', backgroundColor: '#fef3c7', borderRadius: '10px'}}>
                  <Clock size={24} color="#f59e0b" />
                </div>
                <div>
                  <p style={{margin: 0, fontSize: '14px', color: '#6b7280'}}>En Attente</p>
                  <h3 style={{margin: '4px 0 0 0', fontSize: '28px', fontWeight: 'bold'}}>{statistiques.facturesEnAttente}</h3>
                </div>
              </div>
            </div>
            
            <div style={{background: 'white', padding: '24px', borderRadius: '12px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)'}}>
              <div style={{display: 'flex', alignItems: 'center', gap: '16px'}}>
                <div style={{padding: '12px', backgroundColor: '#dbeafe', borderRadius: '10px'}}>
                  <DollarSign size={24} color="#3b82f6" />
                </div>
                <div>
                  <p style={{margin: 0, fontSize: '14px', color: '#6b7280'}}>CA du Mois</p>
                  <h3 style={{margin: '4px 0 0 0', fontSize: '28px', fontWeight: 'bold'}}>{statistiques.chiffreAffairesMois?.toFixed(2)} TND</h3>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Filtres */}
        <div style={{background: 'white', padding: '24px', borderRadius: '12px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)', marginBottom: '24px'}}>
          <div style={{display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px'}}>
            <div>
              <label style={{display: 'block', marginBottom: '8px', fontSize: '14px', fontWeight: '500', color: '#374151'}}>
                <Search size={16} style={{display: 'inline', marginRight: '8px', verticalAlign: 'middle'}} />
                Rechercher
              </label>
              <input
                type="text"
                placeholder="N° facture, client..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                style={{width: '100%', padding: '10px 12px', border: '1px solid #d1d5db', borderRadius: '8px', fontSize: '14px'}}
              />
            </div>
            
            <div>
              <label style={{display: 'block', marginBottom: '8px', fontSize: '14px', fontWeight: '500', color: '#374151'}}>
                <Filter size={16} style={{display: 'inline', marginRight: '8px', verticalAlign: 'middle'}} />
                Statut
              </label>
              <select
                value={filtreStatut}
                onChange={(e) => setFiltreStatut(e.target.value)}
                style={{width: '100%', padding: '10px 12px', border: '1px solid #d1d5db', borderRadius: '8px', fontSize: '14px'}}
              >
                <option value="TOUS">Tous les statuts</option>
                <option value="EN_ATTENTE">En attente</option>
                <option value="PAYEE">Payée</option>
                <option value="ANNULEE">Annulée</option>
              </select>
            </div>
            
            <div>
              <label style={{display: 'block', marginBottom: '8px', fontSize: '14px', fontWeight: '500', color: '#374151'}}>
                <Calendar size={16} style={{display: 'inline', marginRight: '8px', verticalAlign: 'middle'}} />
                Date Début
              </label>
              <input
                type="date"
                value={dateDebut}
                onChange={(e) => setDateDebut(e.target.value)}
                style={{width: '100%', padding: '10px 12px', border: '1px solid #d1d5db', borderRadius: '8px', fontSize: '14px'}}
              />
            </div>
            
            <div>
              <label style={{display: 'block', marginBottom: '8px', fontSize: '14px', fontWeight: '500', color: '#374151'}}>
                <Calendar size={16} style={{display: 'inline', marginRight: '8px', verticalAlign: 'middle'}} />
                Date Fin
              </label>
              <input
                type="date"
                value={dateFin}
                onChange={(e) => setDateFin(e.target.value)}
                style={{width: '100%', padding: '10px 12px', border: '1px solid #d1d5db', borderRadius: '8px', fontSize: '14px'}}
              />
            </div>
          </div>
          
          <div style={{display: 'flex', gap: '12px', marginTop: '16px', flexWrap: 'wrap'}}>
            <button
              onClick={() => setShowExportModal(true)}
              style={{padding: '10px 20px', background: '#3b82f6', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px', fontWeight: '500'}}
            >
              <Download size={18} />
              Exporter
            </button>
            
            {selectedFactures.length > 0 && (
              <div style={{padding: '10px 20px', background: '#f3f4f6', borderRadius: '8px', display: 'flex', alignItems: 'center', gap: '8px'}}>
                <span style={{fontWeight: '500', color: '#374151'}}>{selectedFactures.length} sélectionnée(s)</span>
                <button
                  onClick={() => setSelectedFactures([])}
                  style={{background: 'none', border: 'none', cursor: 'pointer', color: '#6b7280'}}
                >
                  <X size={18} />
                </button>
              </div>
            )}
          </div>
        </div>

        {/* Table */}
        <div style={{background: 'white', borderRadius: '12px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)', overflow: 'hidden'}}>
          <div style={{overflowX: 'auto'}}>
            <table style={{width: '100%', borderCollapse: 'collapse'}}>
              <thead style={{backgroundColor: '#f9fafb', borderBottom: '2px solid #e5e7eb'}}>
                <tr>
                  <th style={{padding: '16px', textAlign: 'left'}}>
                    <input
                      type="checkbox"
                      checked={selectedFactures.length === facturesFiltrees.length && facturesFiltrees.length > 0}
                      onChange={toggleSelectAll}
                      style={{cursor: 'pointer'}}
                    />
                  </th>
                  <th style={{padding: '16px', textAlign: 'left', fontWeight: '600', color: '#374151'}}>N° Facture</th>
                  <th style={{padding: '16px', textAlign: 'left', fontWeight: '600', color: '#374151'}}>Client</th>
                  <th style={{padding: '16px', textAlign: 'left', fontWeight: '600', color: '#374151'}}>Date</th>
                  <th style={{padding: '16px', textAlign: 'left', fontWeight: '600', color: '#374151'}}>Montant TTC</th>
                  <th style={{padding: '16px', textAlign: 'left', fontWeight: '600', color: '#374151'}}>Statut</th>
                  <th style={{padding: '16px', textAlign: 'center', fontWeight: '600', color: '#374151'}}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {facturesFiltrees.length === 0 ? (
                  <tr>
                    <td colSpan="7" style={{padding: '48px', textAlign: 'center'}}>
                      <FileText size={48} color="#d1d5db" style={{margin: '0 auto 16px'}} />
                      <p style={{color: '#6b7280', margin: 0}}>Aucune facture trouvée</p>
                    </td>
                  </tr>
                ) : (
                  facturesFiltrees.map((facture) => (
                    <tr key={facture.id} style={{borderBottom: '1px solid #f3f4f6'}}>
                      <td style={{padding: '16px'}}>
                        <input
                          type="checkbox"
                          checked={selectedFactures.includes(facture.id)}
                          onChange={() => toggleSelectFacture(facture.id)}
                          style={{cursor: 'pointer'}}
                        />
                      </td>
                      <td style={{padding: '16px', fontWeight: '600', color: '#111827'}}>{facture.numeroFacture}</td>
                      <td style={{padding: '16px', color: '#374151'}}>{facture.nomClient}</td>
                      <td style={{padding: '16px', color: '#6b7280', fontSize: '14px'}}>
                        {new Date(facture.dateFacture).toLocaleDateString("fr-FR")}
                      </td>
                      <td style={{padding: '16px', fontWeight: '600', color: '#111827'}}>{facture.montantTTC?.toFixed(2)} TND</td>
                      <td style={{padding: '16px'}}>
                        <span style={{
                          padding: '6px 12px',
                          borderRadius: '6px',
                          fontSize: '13px',
                          fontWeight: '500',
                          backgroundColor: facture.statut === "PAYEE" ? '#d1fae5' : facture.statut === "EN_ATTENTE" ? '#fef3c7' : '#fee2e2',
                          color: facture.statut === "PAYEE" ? '#065f46' : facture.statut === "EN_ATTENTE" ? '#92400e' : '#991b1b'
                        }}>
                          {facture.statut}
                        </span>
                      </td>
                      <td style={{padding: '16px', textAlign: 'center'}}>
                        <div style={{display: 'flex', gap: '8px', justifyContent: 'center'}}>
                          <button
                            onClick={() => handleExportSingle(facture.id)}
                            style={{padding: '8px 12px', background: '#3b82f6', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '6px'}}
                            title="Télécharger PDF"
                          >
                            <Download size={16} />
                          </button>
                          {facture.statut === "EN_ATTENTE" && (
                            <button
                              onClick={() => handleMarquerPayee(facture.id)}
                              style={{padding: '8px 12px', background: '#10b981', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '6px'}}
                              title="Marquer payée"
                            >
                              <CheckCircle size={16} />
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Modal Export */}
      {showExportModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.5)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1000
        }}>
          <div style={{
            background: 'white',
            borderRadius: '12px',
            padding: '32px',
            maxWidth: '500px',
            width: '90%',
            boxShadow: '0 20px 25px -5px rgba(0,0,0,0.1)'
          }}>
            <h3 style={{margin: '0 0 24px 0', fontSize: '20px', fontWeight: 'bold', color: '#111827'}}>Options d'export</h3>
            
            <div style={{marginBottom: '24px'}}>
              <p style={{margin: '0 0 16px 0', color: '#6b7280', fontSize: '14px'}}>
                {selectedFactures.length > 0 
                  ? `${selectedFactures.length} facture(s) sélectionnée(s)`
                  : `${facturesFiltrees.length} facture(s) selon vos filtres`}
              </p>
            </div>
            
            <div style={{display: 'flex', flexDirection: 'column', gap: '12px', marginBottom: '24px'}}>
              <button
                onClick={handleExportMultiple}
                style={{
                  padding: '14px 20px',
                  background: '#3b82f6',
                  color: 'white',
                  border: 'none',
                  borderRadius: '8px',
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '12px',
                  fontWeight: '500',
                  fontSize: '15px'
                }}
              >
                <FileText size={20} />
                <div style={{textAlign: 'left', flex: 1}}>
                  <div>Export PDF unique</div>
                  <div style={{fontSize: '13px', opacity: 0.9}}>Toutes les factures dans un seul PDF</div>
                </div>
              </button>
              
              <button
                onClick={handleExportZip}
                style={{
                  padding: '14px 20px',
                  background: '#8b5cf6',
                  color: 'white',
                  border: 'none',
                  borderRadius: '8px',
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '12px',
                  fontWeight: '500',
                  fontSize: '15px'
                }}
              >
                <FileArchive size={20} />
                <div style={{textAlign: 'left', flex: 1}}>
                  <div>Export ZIP</div>
                  <div style={{fontSize: '13px', opacity: 0.9}}>Un PDF par facture dans un fichier ZIP</div>
                </div>
              </button>
            </div>
            
            <button
              onClick={() => setShowExportModal(false)}
              style={{
                width: '100%',
                padding: '12px',
                background: '#f3f4f6',
                color: '#374151',
                border: 'none',
                borderRadius: '8px',
                cursor: 'pointer',
                fontWeight: '500'
              }}
            >
              Annuler
            </button>
          </div>
        </div>
      )}

      <style>{`
        @keyframes spin {
          to { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
};

export default FacturesAdmin;