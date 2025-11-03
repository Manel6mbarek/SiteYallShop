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
@RequestMapping("/api/admin/lignes-commande")
@CrossOrigin(origins = "*")
public class AdminLigneCommandeController {

    private final LigneCommandeService ligneCommandeService;

    @Autowired
    public AdminLigneCommandeController(LigneCommandeService ligneCommandeService) {
        this.ligneCommandeService = ligneCommandeService;
    }

    @GetMapping
    public ResponseEntity<List<LigneCommandeDTO>> getAllLignes() {
        return ResponseEntity.ok(ligneCommandeService.obtenirToutesLesLignesCommande());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LigneCommandeDTO> getLigneById(@PathVariable Long id) {
        return ResponseEntity.ok(ligneCommandeService.obtenirLigneCommandeParId(id));
    }

    @PostMapping
    public ResponseEntity<LigneCommandeDTO> creerLigne(@Valid @RequestBody LigneCommandeDTO dto) {
        LigneCommandeDTO nouvelleLigne = ligneCommandeService.creerLigneCommande(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleLigne);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LigneCommandeDTO> updateLigne(@PathVariable Long id, @Valid @RequestBody LigneCommandeDTO dto) {
        LigneCommandeDTO ligneUpdated = ligneCommandeService.mettreAJourLigneCommande(id, dto);
        return ResponseEntity.ok(ligneUpdated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLigne(@PathVariable Long id) {
        ligneCommandeService.supprimerLigneCommande(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/commande/{commandeId}")
    public ResponseEntity<List<LigneCommandeDTO>> getLignesByCommande(@PathVariable Long commandeId) {
        return ResponseEntity.ok(ligneCommandeService.obtenirLignesCommandeParCommandeId(commandeId));
    }

    // Statistiques et rapports → accessibles uniquement par l’admin
}
