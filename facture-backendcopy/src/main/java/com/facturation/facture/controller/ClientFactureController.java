package com.facturation.facture.controller;

import com.facturation.facture.dto.FactureDTO;
import com.facturation.facture.service.FactureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients/factures")
@CrossOrigin(origins = "http://localhost:3000")
public class ClientFactureController {

    private final FactureService factureService;

    @Autowired
    public ClientFactureController(FactureService factureService) {
        this.factureService = factureService;
    }

    @GetMapping("/historique")
    public ResponseEntity<List<FactureDTO>> getHistoriqueFacturesClient(@RequestParam Long clientId) {
        List<FactureDTO> factures = factureService.obtenirHistoriqueFacturesClient(clientId);
        return ResponseEntity.ok(factures);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FactureDTO> getFactureParId(@PathVariable Long id, @RequestParam Long clientId) {
        Optional<FactureDTO> facture = factureService.obtenirFactureParId(id);
        if (facture.isEmpty() || !factureService.factureAppartientAuClient(id, clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(facture.get());
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<ByteArrayResource> exporterFacturePDFClient(@PathVariable Long id, @RequestParam Long clientId) {
        if (!factureService.factureAppartientAuClient(id, clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ByteArrayResource pdf = factureService.exporterFacturePDF(id);
        String filename = "facture_" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/{id}/pdf/preview")
    public ResponseEntity<ByteArrayResource> previewPDFClient(@PathVariable Long id, @RequestParam Long clientId) {
        if (!factureService.factureAppartientAuClient(id, clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ByteArrayResource pdf = factureService.exporterFacturePDF(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
