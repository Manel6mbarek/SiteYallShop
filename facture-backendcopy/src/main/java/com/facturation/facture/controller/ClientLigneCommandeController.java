package com.facturation.facture.controller;

import com.facturation.facture.dto.LigneCommandeDTO;
import com.facturation.facture.service.LigneCommandeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients/lignes-commande")
@CrossOrigin(origins = "*")
public class ClientLigneCommandeController {

    private final LigneCommandeService ligneCommandeService;

    @Autowired
    public ClientLigneCommandeController(LigneCommandeService ligneCommandeService) {
        this.ligneCommandeService = ligneCommandeService;
    }

    @GetMapping("/commande/{commandeId}")
    public ResponseEntity<List<LigneCommandeDTO>> getLignesCommandeClient(
            @PathVariable Long commandeId,
            @RequestParam Long clientId) {
        // Vérifier que la commande appartient au client
        boolean appartientAuClient = ligneCommandeService.commandeAppartientAuClient(commandeId, clientId);
        if (!appartientAuClient) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<LigneCommandeDTO> lignes = ligneCommandeService.obtenirLignesCommandeParCommandeId(commandeId);
        return ResponseEntity.ok(lignes);
    }

    @PostMapping
    public ResponseEntity<LigneCommandeDTO> creerLigneCommandeClient(
            @Valid @RequestBody LigneCommandeDTO dto,
            @RequestParam Long clientId) {
        if (!ligneCommandeService.commandeAppartientAuClient(dto.getCommandeId(), clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        LigneCommandeDTO nouvelleLigne = ligneCommandeService.creerLigneCommande(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleLigne);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LigneCommandeDTO> updateLigneClient(
            @PathVariable Long id,
            @Valid @RequestBody LigneCommandeDTO dto,
            @RequestParam Long clientId) {
        if (!ligneCommandeService.commandeAppartientAuClient(dto.getCommandeId(), clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        LigneCommandeDTO ligneUpdated = ligneCommandeService.mettreAJourLigneCommande(id, dto);
        return ResponseEntity.ok(ligneUpdated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerLigneClient(@PathVariable Long id, @RequestParam Long clientId) {
        Long commandeId = ligneCommandeService.getCommandeIdByLigne(id);
        if (!ligneCommandeService.commandeAppartientAuClient(commandeId, clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ligneCommandeService.supprimerLigneCommande(id);
        return ResponseEntity.noContent().build();
    }

    // Les endpoints de statistiques, produits les plus vendus → cachés pour le client
}
