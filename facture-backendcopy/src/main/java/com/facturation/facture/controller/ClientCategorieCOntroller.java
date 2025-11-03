package com.facturation.facture.controller;

import com.facturation.facture.dto.CategorieDTO;
import com.facturation.facture.service.CategorieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients/categories")
@CrossOrigin(origins = "http://localhost:3000")
public class ClientCategorieCOntroller {

    private final CategorieService categorieService;

    @Autowired
    public ClientCategorieCOntroller(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    // Tous les clients peuvent voir la liste des catégories
    @GetMapping
    public ResponseEntity<List<CategorieDTO>> getAllCategories() {
        return ResponseEntity.ok(categorieService.getAllCategories());
    }

    // Voir une catégorie par ID
    @GetMapping("/{id}")
    public ResponseEntity<CategorieDTO> getCategorieById(@PathVariable Long id) {
        return categorieService.getCategorieById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
