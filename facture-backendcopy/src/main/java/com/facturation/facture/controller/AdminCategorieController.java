package com.facturation.facture.controller;
import com.facturation.facture.dto.CategorieDTO;
import com.facturation.facture.service.CategorieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")

@CrossOrigin(origins = "http://localhost:3000")
public class AdminCategorieController {

    private final CategorieService categorieService;

    @Autowired
    public AdminCategorieController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    // Création d'une catégorie
    @PostMapping
    public ResponseEntity<CategorieDTO> createCategorie(@Valid @RequestBody CategorieDTO dto) {
        return ResponseEntity.ok(categorieService.createCategorie(dto));
    }

    // Mise à jour d'une catégorie
    @PutMapping("/{id}")
    public ResponseEntity<CategorieDTO> updateCategorie(@PathVariable Long id,
                                                        @Valid @RequestBody CategorieDTO dto) {
        return categorieService.updateCategorie(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Suppression d'une catégorie
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        categorieService.deleteCategorie(id);
        return ResponseEntity.noContent().build();
    }

    // Lecture d'une catégorie spécifique (optionnel, peut être aussi dans Client)
    @GetMapping("/{id}")
    public ResponseEntity<CategorieDTO> getCategorieById(@PathVariable Long id) {
        return categorieService.getCategorieById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Liste de toutes les catégories
    @GetMapping
    public ResponseEntity<List<CategorieDTO>> getAllCategories() {
        return ResponseEntity.ok(categorieService.getAllCategories());
    }
}
