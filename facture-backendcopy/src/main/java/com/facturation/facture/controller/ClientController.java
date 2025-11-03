package com.facturation.facture.controller;

import com.facturation.facture.dto.ClientDTO;
import com.facturation.facture.dto.CommandeDTO;
import com.facturation.facture.dto.FactureDTO;
import com.facturation.facture.dto.ProduitDTO;
import com.facturation.facture.model.Commande;
import com.facturation.facture.model.Facture;
import com.facturation.facture.model.User;
import com.facturation.facture.model.enums.Role;
import com.facturation.facture.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "http://localhost:3000")
public class ClientController {

    private final UserService userService;

    @Autowired
    public ClientController(UserService userService) {
        this.userService = userService;
    }
    // ✅ Inscription d’un client
    @PostMapping("/register")
    public ResponseEntity<ClientDTO> creerClient(@Valid @RequestBody ClientDTO clientDTO) {
        User client = clientDTO.toEntity();
        client.setRole(Role.CLIENT);
        User sauvegarde = userService.sauvegarderClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClientDTO.fromEntity(sauvegarde));
    }



}
