package com.facturation.facture.controller;

import com.facturation.facture.dto.LigneCommandeDTO;
import com.facturation.facture.service.LigneCommandeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lignes-commande")
@CrossOrigin(origins = "*")
public class LigneCommandeController {

    @Autowired
    private LigneCommandeService ligneCommandeService;

    /**
     * Créer une nouvelle ligne de commande
     */
    @PostMapping
    public ResponseEntity<LigneCommandeDTO> creerLigneCommande(@Valid @RequestBody LigneCommandeDTO ligneCommandeDTO) {
        LigneCommandeDTO nouvelleLigne = ligneCommandeService.creerLigneCommande(ligneCommandeDTO);
        return new ResponseEntity<>(nouvelleLigne, HttpStatus.CREATED);
    }

    /**
     * Obtenir toutes les lignes de commande
     */
    @GetMapping
    public ResponseEntity<List<LigneCommandeDTO>> obtenirToutesLesLignesCommande() {
        List<LigneCommandeDTO> lignes = ligneCommandeService.obtenirToutesLesLignesCommande();
        return ResponseEntity.ok(lignes);
    }

    /**
     * Obtenir une ligne de commande par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<LigneCommandeDTO> obtenirLigneCommandeParId(@PathVariable Long id) {
        LigneCommandeDTO ligne = ligneCommandeService.obtenirLigneCommandeParId(id);
        return ResponseEntity.ok(ligne);
    }

    /**
     * Obtenir les lignes de commande par ID de commande
     */
    @GetMapping("/commande/{commandeId}")
    public ResponseEntity<List<LigneCommandeDTO>> obtenirLignesParCommande(@PathVariable Long commandeId) {
        List<LigneCommandeDTO> lignes = ligneCommandeService.obtenirLignesCommandeParCommandeId(commandeId);
        return ResponseEntity.ok(lignes);
    }

    /**
     * Obtenir les lignes de commande par ID de produit
     */
    @GetMapping("/produit/{produitId}")
    public ResponseEntity<List<LigneCommandeDTO>> obtenirLignesParProduit(@PathVariable Long produitId) {
        List<LigneCommandeDTO> lignes = ligneCommandeService.obtenirLignesCommandeParProduitId(produitId);
        return ResponseEntity.ok(lignes);
    }

    /**
     * Mettre à jour une ligne de commande
     */
    @PutMapping("/{id}")
    public ResponseEntity<LigneCommandeDTO> mettreAJourLigneCommande(
            @PathVariable Long id,
            @Valid @RequestBody LigneCommandeDTO ligneCommandeDTO) {
        LigneCommandeDTO ligneUpdated = ligneCommandeService.mettreAJourLigneCommande(id, ligneCommandeDTO);
        return ResponseEntity.ok(ligneUpdated);
    }

    /**
     * Supprimer une ligne de commande
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> supprimerLigneCommande(@PathVariable Long id) {
        ligneCommandeService.supprimerLigneCommande(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Ligne de commande supprimée avec succès");
        return ResponseEntity.ok(response);
    }

    /**
     * Supprimer toutes les lignes d'une commande
     */
    @DeleteMapping("/commande/{commandeId}")
    public ResponseEntity<Map<String, String>> supprimerLignesParCommande(@PathVariable Long commandeId) {
        ligneCommandeService.supprimerLignesCommandeParCommandeId(commandeId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Toutes les lignes de la commande ont été supprimées avec succès");
        return ResponseEntity.ok(response);
    }

    /**
     * Calculer le total d'une commande
     */
    @GetMapping("/commande/{commandeId}/total")
    public ResponseEntity<Map<String, Object>> calculerTotalCommande(@PathVariable Long commandeId) {
        BigDecimal total = ligneCommandeService.calculerTotalCommande(commandeId);
        Long nbArticles = ligneCommandeService.compterArticlesInCommande(commandeId);
        Long quantiteTotale = ligneCommandeService.calculerQuantiteTotaleInCommande(commandeId);

        Map<String, Object> response = new HashMap<>();
        response.put("commandeId", commandeId);
        response.put("total", total);
        response.put("nombreArticles", nbArticles);
        response.put("quantiteTotale", quantiteTotale);

        return ResponseEntity.ok(response);
    }

    /**
     * Obtenir les statistiques d'un produit
     */
    @GetMapping("/statistiques/produit/{produitId}")
    public ResponseEntity<Map<String, Object>> obtenirStatistiquesProduit(@PathVariable Long produitId) {
        Object[] stats = ligneCommandeService.obtenirStatistiquesProduit(produitId);
        Long nbCommandes = ligneCommandeService.compterCommandesAvecProduit(produitId);
        BigDecimal chiffreAffaires = ligneCommandeService.calculerChiffreAffairesProduit(produitId);

        Map<String, Object> response = new HashMap<>();
        response.put("produitId", produitId);
        if (stats != null && stats.length >= 3) {
            response.put("nombreLignesCommande", stats[0]);
            response.put("quantiteTotaleVendue", stats[1]);
            response.put("chiffreAffairesTotal", stats[2]);
        }
        response.put("nombreCommandesDistinctes", nbCommandes);
        response.put("chiffreAffaires", chiffreAffaires);

        return ResponseEntity.ok(response);
    }

    /**
     * Rechercher des lignes de commande par nom de produit
     */
    @GetMapping("/recherche")
    public ResponseEntity<List<LigneCommandeDTO>> rechercherParNomProduit(@RequestParam String nomProduit) {
        List<LigneCommandeDTO> lignes = ligneCommandeService.rechercherParNomProduit(nomProduit);
        return ResponseEntity.ok(lignes);
    }

    /**
     * Obtenir les lignes de commande par tranche de prix
     */
    @GetMapping("/prix")
    public ResponseEntity<List<LigneCommandeDTO>> obtenirLignesParTranchePrix(
            @RequestParam BigDecimal prixMin,
            @RequestParam BigDecimal prixMax) {
        List<LigneCommandeDTO> lignes = ligneCommandeService.obtenirLignesParTranchePrix(prixMin, prixMax);
        return ResponseEntity.ok(lignes);
    }

    /**
     * Obtenir les produits les plus vendus
     */
    @GetMapping("/statistiques/produits-populaires")
    public ResponseEntity<List<Object[]>> obtenirProduitsLesPlusVendus() {
        List<Object[]> produits = ligneCommandeService.obtenirProduitsLesPlusVendus();
        return ResponseEntity.ok(produits);
    }

    /**
     * Obtenir les produits par chiffre d'affaires
     */
    @GetMapping("/statistiques/produits-rentables")
    public ResponseEntity<List<Object[]>> obtenirProduitsParChiffreAffaires() {
        List<Object[]> produits = ligneCommandeService.obtenirProduitsParChiffreAffaires();
        return ResponseEntity.ok(produits);
    }
}