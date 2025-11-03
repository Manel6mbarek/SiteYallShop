//package com.facturation.facture.controller;
//
//import com.facturation.facture.dto.CommandeDTO;
//import com.facturation.facture.model.Commande;
//import com.facturation.facture.model.enums.StatutCommande;
//import com.facturation.facture.model.enums.ModePaiement;
//import com.facturation.facture.service.CommandeService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/commandes")
//@CrossOrigin(origins = "http://localhost:3000")
//public class CommandeController {
//
//    private final CommandeService commandeService;
//
//    @Autowired
//    public CommandeController(CommandeService commandeService) {
//        this.commandeService = commandeService;
//    }
//
//    /**
//     * Créer une commande complète avec produits (recommandé)
//     */
//    @PostMapping("/creer-avec-produits")
//    public ResponseEntity<?> creerCommandeAvecProduits(@RequestBody CommandeDTO commandeDTO) {
//        try {
//            Commande commande = commandeService.creerCommandeAvecProduits(commandeDTO);
//            return ResponseEntity.status(HttpStatus.CREATED).body(CommandeDTO.fromEntity(commande));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Erreur interne du serveur"));
//        }
//    }
//
//    /**
//     * Changer le statut d'une commande (pour l'admin)
//     */
//    @PatchMapping("/{id}/statut")
//    public ResponseEntity<?> changerStatutCommande(@PathVariable Long id,
//                                                   @RequestParam StatutCommande statut) {
//        try {
//            Commande commande = commandeService.changerStatutCommande(id, statut);
//            return ResponseEntity.ok(CommandeDTO.fromEntity(commande));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Erreur interne du serveur"));
//        }
//    }
//
//    /**
//     * Changer le mode de paiement d'une commande
//     */
//    @PatchMapping("/{id}/mode-paiement")
//    public ResponseEntity<?> changerModePaiement(@PathVariable Long id,
//                                                 @RequestParam ModePaiement modePaiement) {
//        try {
//            Commande commande = commandeService.changerModePaiement(id, modePaiement);
//            return ResponseEntity.ok(CommandeDTO.fromEntity(commande));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Erreur interne du serveur"));
//        }
//    }
//
//    /**
//     * Obtenir toutes les commandes
//     */
////    @GetMapping
////    public ResponseEntity<?> obtenirToutesLesCommandes() {
////        try {
////            List<Commande> commandes = commandeService.obtenirToutesLesCommandes();
////            List<CommandeDTO> commandesDTO = commandes.stream()
////                    .map(CommandeDTO::fromEntity)
////                    .collect(Collectors.toList());
////            return ResponseEntity.ok(commandesDTO);
////        } catch (Exception e) {
////            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
////                    .body(Map.of("error", "Erreur lors de la récupération des commandes"));
////        }
////    }
//
//    /**
//     * Obtenir une commande par son ID avec détails
//     */
//    @GetMapping("/{id}")
//    public ResponseEntity<?> obtenirCommandeAvecDetails(@PathVariable Long id) {
//        try {
//            Optional<Commande> commande = commandeService.obtenirCommandeAvecDetails(id);
//            if (commande.isPresent()) {
//                return ResponseEntity.ok(CommandeDTO.fromEntity(commande.get()));
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Erreur lors de la récupération de la commande"));
//        }
//    }
//
//    /**
//     * Créer une nouvelle commande vide (pour compatibilité)
//     */
//    @PostMapping("/client/{idClient}")
//    public ResponseEntity<?> creerCommande(@PathVariable Long idClient) {
//        try {
//            Commande commande = commandeService.creerCommande(idClient);
//            return ResponseEntity.status(HttpStatus.CREATED)
//                    .body(CommandeDTO.fromEntity(commande));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Erreur lors de la création de la commande"));
//        }
//    }
//
//    /**
//     * Ajouter un produit à une commande
//     */
//    @PostMapping("/{idCommande}/produits/{idProduit}")
//    public ResponseEntity<?> ajouterProduitACommande(
//            @PathVariable Long idCommande,
//            @PathVariable Long idProduit,
//            @RequestParam Integer quantite) {
//        try {
//            Commande commande = commandeService.ajouterProduitACommande(idCommande, idProduit, quantite);
//            return ResponseEntity.ok(CommandeDTO.fromEntity(commande));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Erreur lors de l'ajout du produit"));
//        }
//    }
//
//    /**
//     * Supprimer un produit d'une commande
//     */
//    @DeleteMapping("/{idCommande}/produits/{idProduit}")
//    public ResponseEntity<?> supprimerProduitDeCommande(
//            @PathVariable Long idCommande,
//            @PathVariable Long idProduit) {
//        try {
//            Commande commande = commandeService.supprimerProduitDeCommande(idCommande, idProduit);
//            return ResponseEntity.ok(CommandeDTO.fromEntity(commande));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Erreur lors de la suppression du produit"));
//        }
//    }
//
//    /**
//     * Modifier la quantité d'un produit dans une commande
//     */
//    @PutMapping("/{idCommande}/produits/{idProduit}")
//    public ResponseEntity<?> modifierQuantiteProduit(
//            @PathVariable Long idCommande,
//            @PathVariable Long idProduit,
//            @RequestParam Integer nouvelleQuantite) {
//        try {
//            Commande commande = commandeService.modifierQuantiteProduit(idCommande, idProduit, nouvelleQuantite);
//            return ResponseEntity.ok(CommandeDTO.fromEntity(commande));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Erreur lors de la modification de la quantité"));
//        }
//    }
//
//}