package com.facturation.facture.controller;

import com.facturation.facture.dto.FactureDTO;
import com.facturation.facture.model.enums.StatutFacture;
import com.facturation.facture.model.enums.ModePaiement;
import com.facturation.facture.service.FactureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/factures")
@CrossOrigin(origins = "http://localhost:3000")
public class FactureController {

    private final FactureService factureService;

    @Autowired
    public FactureController(FactureService factureService) {
        this.factureService = factureService;
    }

    /**
     * Exporter une facture en PDF
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<ByteArrayResource> exporterFacturePDF(@PathVariable Long id) {
        try {
            ByteArrayResource pdfResource = factureService.exporterFacturePDF(id);

            String filename = "facture_" + id + "_" + LocalDate.now().toString() + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfResource);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Prévisualiser une facture en PDF (dans le navigateur)
     */
    @GetMapping("/{id}/pdf/preview")
    public ResponseEntity<ByteArrayResource> previsualiserFacturePDF(@PathVariable Long id) {
        try {
            ByteArrayResource pdfResource = factureService.exporterFacturePDF(id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfResource);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir toutes les factures avec filtres optionnels
     */
    @GetMapping
    public ResponseEntity<?> obtenirFactures(
            @RequestParam(required = false) StatutFacture statut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) Long clientId) {
        try {
            List<FactureDTO> factures = factureService.obtenirFacturesAvecFiltres(statut, dateDebut, dateFin, clientId);
            return ResponseEntity.ok(factures);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la récupération des factures"));
        }
    }

    /**
     * Obtenir une facture par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenirFactureParId(@PathVariable Long id) {
        try {
            Optional<FactureDTO> facture = factureService.obtenirFactureParId(id);
            if (facture.isPresent()) {
                return ResponseEntity.ok(facture.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la récupération de la facture"));
        }
    }

    /**
     * Obtenir les factures non traitées (pour l'admin)
     */
    @GetMapping("/non-traitees")
    public ResponseEntity<?> obtenirFacturesNonTraitees() {
        try {
            List<FactureDTO> factures = factureService.obtenirFacturesNonTraitees();
            return ResponseEntity.ok(factures);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la récupération des factures non traitées"));
        }
    }

    /**
     * Obtenir les factures payées du mois (pour l'admin)
     */
    @GetMapping("/payees-mois")
    public ResponseEntity<?> obtenirFacturesPayeesDuMois() {
        try {
            List<FactureDTO> factures = factureService.obtenirFacturesPayeesDuMois();
            return ResponseEntity.ok(factures);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la récupération des factures du mois"));
        }
    }

    /**
     * Obtenir l'historique des factures d'un client
     */
    @GetMapping("/client/{clientId}/historique")
    public ResponseEntity<?> obtenirHistoriqueFacturesClient(@PathVariable Long clientId) {
        try {
            List<FactureDTO> factures = factureService.obtenirHistoriqueFacturesClient(clientId);
            return ResponseEntity.ok(factures);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la récupération de l'historique"));
        }
    }

    /**
     * Obtenir les factures par période
     */
    @GetMapping("/periode")
    public ResponseEntity<?> obtenirFacturesParPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            List<FactureDTO> factures = factureService.obtenirFacturesAvecFiltres(null, dateDebut, dateFin, null);
            return ResponseEntity.ok(Map.of(
                    "factures", factures,
                    "periode", Map.of(
                            "debut", dateDebut.toString(),
                            "fin", dateFin.toString(),
                            "nombre", factures.size()
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la récupération des factures par période"));
        }
    }

    /**
     * Obtenir les statistiques des factures (pour l'admin)
     */
    @GetMapping("/statistiques")
    public ResponseEntity<?> obtenirStatistiquesFactures() {
        try {
            FactureService.FactureStatistiques stats = factureService.obtenirStatistiquesFactures();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du calcul des statistiques"));
        }
    }

    /**
     * Marquer une facture comme payée
     */
    @PatchMapping("/{id}/marquer-payee")
    public ResponseEntity<?> marquerFacturePayee(
            @PathVariable Long id,
            @RequestParam ModePaiement modePaiement) {
        try {
            FactureDTO facture = factureService.marquerFacturePayee(id, modePaiement);
            return ResponseEntity.ok(facture);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du marquage de la facture"));
        }
    }

    /**
     * Obtenir les statuts de facture possibles
     */
    @GetMapping("/statuts")
    public ResponseEntity<List<Map<String, String>>> obtenirStatutsPossibles() {
        try {
            List<Map<String, String>> statuts = Arrays.stream(StatutFacture.values())
                    .map(statut -> Map.of(
                            "value", statut.name(),
                            "label", getStatutLibelle(statut)
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(statuts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir les modes de paiement possibles
     */
    @GetMapping("/modes-paiement")
    public ResponseEntity<List<Map<String, String>>> obtenirModesPaiement() {
        try {
            List<Map<String, String>> modes = Arrays.stream(ModePaiement.values())
                    .map(mode -> Map.of(
                            "value", mode.name(),
                            "label", getModePaiementLibelle(mode)
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(modes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Rechercher factures par numéro
     */
    @GetMapping("/numero/{numeroFacture}")
    public ResponseEntity<?> rechercherFactureParNumero(@PathVariable String numeroFacture) {
        try {
            // Cette méthode nécessiterait d'être ajoutée au service
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(Map.of("message", "Recherche par numéro à implémenter"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la recherche"));
        }
    }

    /**
     * Export multiple de factures en PDF (pour une période)
     */
    @GetMapping("/export-periode/pdf")
    public ResponseEntity<?> exporterFacturesPeriodenPDF(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) StatutFacture statut) {
        try {
            // Cette fonctionnalité pourrait générer un ZIP avec toutes les factures de la période
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(Map.of("message", "Export multiple à implémenter"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'export"));
        }
    }

    /**
     * Dashboard admin - Résumé des factures
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> obtenirDashboardFactures() {
        try {
            FactureService.FactureStatistiques stats = factureService.obtenirStatistiquesFactures();
            List<FactureDTO> facturesNonTraitees = factureService.obtenirFacturesNonTraitees();
            List<FactureDTO> facturesPayeesMois = factureService.obtenirFacturesPayeesDuMois();

            return ResponseEntity.ok(Map.of(
                    "statistiques", stats,
                    "facturesNonTraitees", facturesNonTraitees.size(),
                    "dernieresFacturesNonTraitees", facturesNonTraitees.stream().limit(5).collect(Collectors.toList()),
                    "facturesPayeesMois", facturesPayeesMois.size(),
                    "dernieresFacturesPayees", facturesPayeesMois.stream().limit(5).collect(Collectors.toList())
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la récupération du dashboard"));
        }
    }

    /**
     * Filtres avancés pour les factures
     */
    @PostMapping("/recherche-avancee")
    public ResponseEntity<?> rechercheAvanceeFactures(@RequestBody Map<String, Object> filtres) {
        try {
            // Extraire les filtres du body
            StatutFacture statut = null;
            LocalDate dateDebut = null;
            LocalDate dateFin = null;
            Long clientId = null;

            if (filtres.containsKey("statut") && filtres.get("statut") != null) {
                statut = StatutFacture.valueOf(filtres.get("statut").toString());
            }
            if (filtres.containsKey("dateDebut") && filtres.get("dateDebut") != null) {
                dateDebut = LocalDate.parse(filtres.get("dateDebut").toString());
            }
            if (filtres.containsKey("dateFin") && filtres.get("dateFin") != null) {
                dateFin = LocalDate.parse(filtres.get("dateFin").toString());
            }
            if (filtres.containsKey("clientId") && filtres.get("clientId") != null) {
                clientId = Long.valueOf(filtres.get("clientId").toString());
            }

            List<FactureDTO> factures = factureService.obtenirFacturesAvecFiltres(statut, dateDebut, dateFin, clientId);

            return ResponseEntity.ok(Map.of(
                    "factures", factures,
                    "total", factures.size(),
                    "filtresAppliques", filtres
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Erreur dans les filtres de recherche: " + e.getMessage()));
        }
    }

    // Méthodes utilitaires privées
    private String getStatutLibelle(StatutFacture statut) {
        switch (statut) {
            case EN_ATTENTE:
                return "En attente";
            case PAYEE:
                return "Payée";
            case ANNULEE:
                return "Annulée";
            default:
                return statut.name();
        }
    }

    private String getModePaiementLibelle(ModePaiement mode) {
        switch (mode) {
            case ESPECES:
                return "Espèces";
            case CARTE_BANCAIRE:
                return "Carte bancaire";
            case CHEQUE:
                return "Chèque";
            case VIREMENT:
                return "Virement";
            default:
                return mode.name();
        }
    }
}