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
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produits")
@CrossOrigin(origins = "http://localhost:3000")
public class ProduitController {

    private final ProduitService produitService;

    @Autowired
    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    /**
     * Obtenir tous les produits
     */
    @GetMapping
    public ResponseEntity<List<ProduitDTO>> obtenirTousLesProduits() {
        try {
            List<Produit> produits = produitService.obtenirTousLesProduits();
            List<ProduitDTO> produitsDTO = produits.stream()
                    .map(ProduitDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(produitsDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir tous les produits disponibles
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<ProduitDTO>> obtenirProduitsDisponibles() {
        try {
            List<Produit> produits = produitService.obtenirProduitsDisponibles();
            List<ProduitDTO> produitsDTO = produits.stream()
                    .map(ProduitDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(produitsDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir un produit par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProduitDTO> obtenirProduitParId(@PathVariable Long id) {
        try {
            Optional<Produit> produit = produitService.obtenirProduitParId(id);
            if (produit.isPresent()) {
                return ResponseEntity.ok(ProduitDTO.fromEntity(produit.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Créer un nouveau produit
     */
    @PostMapping
    public ResponseEntity<ProduitDTO> creerProduit(@Valid @RequestBody ProduitDTO produitDTO) {
        try {
            Produit produit = produitDTO.toEntity();
            Long categorieId = produitDTO.getCategorieId(); // <--- ajouté
            Produit produitSauvegarde = produitService.sauvegarderProduit(produit, categorieId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ProduitDTO.fromEntity(produitSauvegarde));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * Mettre à jour un produit
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProduitDTO> mettreAJourProduit(@PathVariable Long id,
                                                         @Valid @RequestBody ProduitDTO produitDTO) {
        try {
            Produit produitMisAJour = produitDTO.toEntity();
            Long categorieId = produitDTO.getCategorieId(); // <--- ajouté
            Produit produitSauvegarde = produitService.mettreAJourProduit(id, produitMisAJour, categorieId);
            return ResponseEntity.ok(ProduitDTO.fromEntity(produitSauvegarde));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * Supprimer un produit
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerProduit(@PathVariable Long id) {
        try {
            produitService.supprimerProduit(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Rechercher des produits
     */
    @GetMapping("/recherche")
    public ResponseEntity<List<ProduitDTO>> rechercherProduits(@RequestParam String terme) {
        try {
            List<Produit> produits = produitService.rechercherProduits(terme);
            List<ProduitDTO> produitsDTO = produits.stream()
                    .map(ProduitDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(produitsDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Marquer un produit comme indisponible
     */
    @PatchMapping("/{id}/indisponible")
    public ResponseEntity<ProduitDTO> marquerIndisponible(@PathVariable Long id) {
        try {
            Produit produit = produitService.marquerIndisponible(id);
            return ResponseEntity.ok(ProduitDTO.fromEntity(produit));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Marquer un produit comme disponible
     */
    @PatchMapping("/{id}/disponible")
    public ResponseEntity<ProduitDTO> marquerDisponible(@PathVariable Long id) {
        try {
            Produit produit = produitService.marquerDisponible(id);
            return ResponseEntity.ok(ProduitDTO.fromEntity(produit));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir des produits par fourchette de prix
     */
    @GetMapping("/prix")
    public ResponseEntity<List<ProduitDTO>> obtenirProduitsParFourchettePrix(
            @RequestParam BigDecimal prixMin,
            @RequestParam BigDecimal prixMax) {
        try {
            List<Produit> produits = produitService.obtenirProduitsParFourchettePrix(prixMin, prixMax);
            List<ProduitDTO> produitsDTO = produits.stream()
                    .map(ProduitDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(produitsDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir le prix moyen des produits
     */
    @GetMapping("/statistiques/prix-moyen")
    public ResponseEntity<BigDecimal> obtenirPrixMoyen() {
        try {
            BigDecimal prixMoyen = produitService.obtenirPrixMoyen();
            return ResponseEntity.ok(prixMoyen);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}