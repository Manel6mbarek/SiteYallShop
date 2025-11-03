package com.facturation.facture.controller;

import com.facturation.facture.dto.FactureDTO;
import com.facturation.facture.model.enums.ModePaiement;
import com.facturation.facture.model.enums.StatutFacture;
import com.facturation.facture.service.FactureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/factures")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminFactureController {

    private final FactureService factureService;

    @Autowired
    public AdminFactureController(FactureService factureService) {
        this.factureService = factureService;
    }

    @GetMapping
    public ResponseEntity<List<FactureDTO>> getAllFactures(
            @RequestParam(required = false) StatutFacture statut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) Long clientId
    ) {
        return ResponseEntity.ok(factureService.obtenirFacturesAvecFiltres(statut, dateDebut, dateFin, clientId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FactureDTO> getFactureById(@PathVariable Long id) {
        return factureService.obtenirFactureParId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/marquer-payee")
    public ResponseEntity<FactureDTO> marquerPayee(@PathVariable Long id, @RequestParam ModePaiement modePaiement) {
        FactureDTO facture = factureService.marquerFacturePayee(id, modePaiement);
        return ResponseEntity.ok(facture);
    }

    @GetMapping("/statistiques")
    public ResponseEntity<FactureService.FactureStatistiques> getStats() {
        return ResponseEntity.ok(factureService.obtenirStatistiquesFactures());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        FactureService.FactureStatistiques stats = factureService.obtenirStatistiquesFactures();
        List<FactureDTO> nonTraitees = factureService.obtenirFacturesNonTraitees();
        List<FactureDTO> payeesMois = factureService.obtenirFacturesPayeesDuMois();
        return ResponseEntity.ok(Map.of(
                "statistiques", stats,
                "facturesNonTraitees", nonTraitees.size(),
                "dernieresFacturesNonTraitees", nonTraitees.stream().limit(5).toList(),
                "facturesPayeesMois", payeesMois.size(),
                "dernieresFacturesPayees", payeesMois.stream().limit(5).toList()
        ));
    }

    /**
     * Export PDF d'une seule facture
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<ByteArrayResource> exporterFacturePDF(@PathVariable Long id) {
        ByteArrayResource resource = factureService.exporterFacturePDF(id);

        String filename = "facture_" + id + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    /**
     * Export PDF multiple avec filtres
     */
    @GetMapping("/export/multiple")
    public ResponseEntity<ByteArrayResource> exporterMultipleFacturesPDF(
            @RequestParam(required = false) StatutFacture statut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) List<Long> factureIds
    ) {
        ByteArrayResource resource = factureService.exporterMultipleFacturesPDF(
                statut, dateDebut, dateFin, clientId, factureIds
        );

        String filename = "factures_export_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    /**
     * Export ZIP contenant plusieurs PDFs (une facture par fichier)
     */
    @GetMapping("/export/zip")
    public ResponseEntity<ByteArrayResource> exporterFacturesZIP(
            @RequestParam(required = false) StatutFacture statut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) List<Long> factureIds
    ) {
        ByteArrayResource resource = factureService.exporterFacturesZIP(
                statut, dateDebut, dateFin, clientId, factureIds
        );

        String filename = "factures_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".zip";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}