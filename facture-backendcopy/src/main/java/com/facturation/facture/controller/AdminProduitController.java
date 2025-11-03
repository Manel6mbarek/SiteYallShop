package com.facturation.facture.controller;

import com.facturation.facture.dto.ProduitDTO;
import com.facturation.facture.model.Produit;
import com.facturation.facture.service.ProduitService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/produits")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminProduitController {

    private final ProduitService produitService;

    @Autowired
    public AdminProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @GetMapping
    public ResponseEntity<List<ProduitDTO>> getAllProduits() {
        return ResponseEntity.ok(
                produitService.obtenirTousLesProduits()
                        .stream()
                        .map(ProduitDTO::fromEntity)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProduitDTO> getProduitById(@PathVariable Long id) {
        return produitService.obtenirProduitParId(id)
                .map(ProduitDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProduitDTO> createProduit(@Valid @RequestBody ProduitDTO produitDTO) {
        Produit produit = produitDTO.toEntity();
        Produit produitSauvegarde = produitService.sauvegarderProduit(produit, produitDTO.getCategorieId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProduitDTO.fromEntity(produitSauvegarde));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable Long id) {
        produitService.supprimerProduit(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/indisponible")
    public ResponseEntity<ProduitDTO> marquerIndisponible(@PathVariable Long id) {
        return ResponseEntity.ok(
                ProduitDTO.fromEntity(produitService.marquerIndisponible(id))
        );
    }

    @PatchMapping("/{id}/disponible")
    public ResponseEntity<ProduitDTO> marquerDisponible(@PathVariable Long id) {
        return ResponseEntity.ok(
                ProduitDTO.fromEntity(produitService.marquerDisponible(id))
        );
    }

    @GetMapping("/recherche")
    public ResponseEntity<List<ProduitDTO>> rechercherProduits(@RequestParam String terme) {
        return ResponseEntity.ok(
                produitService.rechercherProduits(terme)
                        .stream()
                        .map(ProduitDTO::fromEntity)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/statistiques/prix-moyen")
    public ResponseEntity<BigDecimal> obtenirPrixMoyen() {
        return ResponseEntity.ok(produitService.obtenirPrixMoyen());
    }
}
