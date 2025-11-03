package com.facturation.facture.controller;

import com.facturation.facture.dto.CommandeDTO;
import com.facturation.facture.model.Commande;
import com.facturation.facture.service.CommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/client/commandes")
@CrossOrigin(origins = "http://localhost:3000")
public class ClientCommandeController {

    private final CommandeService commandeService;

    @Autowired
    public ClientCommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    /**
     * Récupérer toutes les commandes d'un client
     */
    @GetMapping("/mes-commandes/{idClient}")
    public ResponseEntity<?> getMesCommandes(@PathVariable Long idClient) {
        try {
            List<Commande> commandes = commandeService.getCommandesParClient(idClient);
            List<CommandeDTO> commandeDTOs = commandes.stream()
                    .map(CommandeDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(commandeDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la récupération des commandes"));
        }
    }

    /**
     * Récupérer une commande spécifique d'un client
     */
    @GetMapping("/{idCommande}/client/{idClient}")
    public ResponseEntity<?> getCommandeById(
            @PathVariable Long idCommande,
            @PathVariable Long idClient) {
        try {
            Commande commande = commandeService.getCommandeById(idCommande);

            // Vérifier que la commande appartient bien au client
            if (!commande.getClient().getId().equals(idClient)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Accès non autorisé à cette commande"));
            }

            return ResponseEntity.ok(CommandeDTO.fromEntity(commande));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Créer une commande avec des produits directement
     * IMPORTANT: Spécifier l'ID du client dans le CommandeDTO.client.id
     */
    @PostMapping("/creer-avec-produits")
    public ResponseEntity<?> creerCommandeAvecProduits(
            @RequestBody CommandeDTO commandeDTO,
            @RequestParam Long idClient) {
        try {
            // S'assurer que l'ID client est défini dans le DTO
            if (commandeDTO.getClient() == null) {
                commandeDTO.setClient(new com.facturation.facture.dto.UserDTO());
            }
            commandeDTO.getClient().setId(idClient);

            Commande commande = commandeService.creerCommandeAvecProduits(commandeDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(CommandeDTO.fromEntity(commande));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    /**
     * Créer une commande vide pour un client
     */
    @PostMapping("/{idClient}")
    public ResponseEntity<?> creerCommande(@PathVariable Long idClient) {
        try {
            Commande commande = commandeService.creerCommande(idClient);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CommandeDTO.fromEntity(commande));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la création de la commande"));
        }
    }

    /**
     * Ajouter un produit à une commande existante
     */
    @PostMapping("/{idCommande}/produits/{idProduit}")
    public ResponseEntity<?> ajouterProduitACommande(
            @PathVariable Long idCommande,
            @PathVariable Long idProduit,
            @RequestParam Integer quantite,
            @RequestParam Long idClient) {
        try {
            // Vérifier que la commande appartient au client
            Commande commandeExistante = commandeService.getCommandeById(idCommande);
            if (!commandeExistante.getClient().getId().equals(idClient)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Accès non autorisé à cette commande"));
            }

            Commande commande = commandeService.ajouterProduitACommande(idCommande, idProduit, quantite);
            return ResponseEntity.ok(CommandeDTO.fromEntity(commande));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'ajout du produit"));
        }
    }

    /**
     * Supprimer un produit d'une commande
     */
    @DeleteMapping("/{idCommande}/produits/{idProduit}")
    public ResponseEntity<?> supprimerProduitDeCommande(
            @PathVariable Long idCommande,
            @PathVariable Long idProduit,
            @RequestParam Long idClient) {
        try {
            // Vérifier que la commande appartient au client
            Commande commandeExistante = commandeService.getCommandeById(idCommande);
            if (!commandeExistante.getClient().getId().equals(idClient)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Accès non autorisé à cette commande"));
            }

            Commande commande = commandeService.supprimerProduitDeCommande(idCommande, idProduit);
            return ResponseEntity.ok(CommandeDTO.fromEntity(commande));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la suppression du produit"));
        }
    }

    /**
     * Modifier la quantité d'un produit dans une commande
     */
    @PutMapping("/{idCommande}/produits/{idProduit}")
    public ResponseEntity<?> modifierQuantiteProduit(
            @PathVariable Long idCommande,
            @PathVariable Long idProduit,
            @RequestParam Integer nouvelleQuantite,
            @RequestParam Long idClient) {
        try {
            // Vérifier que la commande appartient au client
            Commande commandeExistante = commandeService.getCommandeById(idCommande);
            if (!commandeExistante.getClient().getId().equals(idClient)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Accès non autorisé à cette commande"));
            }

            Commande commande = commandeService.modifierQuantiteProduit(idCommande, idProduit, nouvelleQuantite);
            return ResponseEntity.ok(CommandeDTO.fromEntity(commande));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la modification de la quantité"));
        }
    }

    /**
     * Annuler une commande (si le statut le permet)
     */
    @PutMapping("/{idCommande}/annuler")
    public ResponseEntity<?> annulerCommande(
            @PathVariable Long idCommande,
            @RequestParam Long idClient) {
        try {
            // Vérifier que la commande appartient au client
            Commande commandeExistante = commandeService.getCommandeById(idCommande);
            if (!commandeExistante.getClient().getId().equals(idClient)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Accès non autorisé à cette commande"));
            }

            Commande commande = commandeService.annulerCommande(idCommande);
            return ResponseEntity.ok(CommandeDTO.fromEntity(commande));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}