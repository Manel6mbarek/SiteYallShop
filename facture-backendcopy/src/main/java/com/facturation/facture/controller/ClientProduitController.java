package com.facturation.facture.controller;

import com.facturation.facture.dto.ProduitDTO;
import com.facturation.facture.service.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clients/produits")
@CrossOrigin(origins = "http://localhost:3000")
public class ClientProduitController {

    private final ProduitService produitService;

    @Autowired
    public ClientProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @GetMapping
    public ResponseEntity<List<ProduitDTO>> getAllProduits() {
        return ResponseEntity.ok(
                produitService.obtenirProduitsDisponibles()
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

    @GetMapping("/prix")
    public ResponseEntity<List<ProduitDTO>> getProduitsParFourchettePrix(
            @RequestParam BigDecimal prixMin,
            @RequestParam BigDecimal prixMax) {
        return ResponseEntity.ok(
                produitService.obtenirProduitsParFourchettePrix(prixMin, prixMax)
                        .stream()
                        .map(ProduitDTO::fromEntity)
                        .collect(Collectors.toList())
        );
    }
}
