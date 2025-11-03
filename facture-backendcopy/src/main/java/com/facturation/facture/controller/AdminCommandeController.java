package com.facturation.facture.controller;

import com.facturation.facture.dto.CommandeDTO;
import com.facturation.facture.model.Commande;
import com.facturation.facture.model.enums.StatutCommande;
import com.facturation.facture.model.enums.ModePaiement;
import com.facturation.facture.service.CommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/commandes")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminCommandeController {

    private final CommandeService commandeService;

    @Autowired
    public AdminCommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }
    //**
     //     * Obtenir toutes les commandes
     //     */
    @GetMapping
    public ResponseEntity<?> obtenirToutesLesCommandes() {
        try {
            List<Commande> commandes = commandeService.obtenirToutesLesCommandes();
            List<CommandeDTO> commandesDTO = commandes.stream()
                    .map(CommandeDTO::fromEntity)
                   .collect(Collectors.toList());
           return ResponseEntity.ok(commandesDTO);
      } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(Map.of("error", "Erreur lors de la récupération des commandes"));
        }
   }
    @PatchMapping("/{id}/statut")
    public ResponseEntity<?> changerStatutCommande(@PathVariable Long id,
                                                   @RequestParam StatutCommande statut) {
        try {
            Commande commande = commandeService.changerStatutCommande(id, statut);
            return ResponseEntity.ok(CommandeDTO.fromEntity(commande));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @PatchMapping("/{id}/mode-paiement")
    public ResponseEntity<?> changerModePaiement(@PathVariable Long id,
                                                 @RequestParam ModePaiement modePaiement) {
        try {
            Commande commande = commandeService.changerModePaiement(id, modePaiement);
            return ResponseEntity.ok(CommandeDTO.fromEntity(commande));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenirCommandeAvecDetails(@PathVariable Long id) {
        try {
            return commandeService.obtenirCommandeAvecDetails(id)
                    .map(commande -> ResponseEntity.ok(CommandeDTO.fromEntity(commande)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la récupération de la commande"));
        }
    }

}
