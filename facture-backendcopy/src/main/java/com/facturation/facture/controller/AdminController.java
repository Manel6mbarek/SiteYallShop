package com.facturation.facture.controller;

import com.facturation.facture.dto.CommandeDTO;
import com.facturation.facture.dto.FactureDTO;
import com.facturation.facture.dto.ProduitDTO;
import com.facturation.facture.model.Commande;
import com.facturation.facture.model.Produit;
import com.facturation.facture.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


}

