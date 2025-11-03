
// ============================================
// CONTROLLER UPDATED
// ============================================
package com.facturation.facture.controller;

import com.facturation.facture.dto.ClientDTO;
import com.facturation.facture.model.User;
import com.facturation.facture.model.enums.Role;
import com.facturation.facture.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Obtenir tous les clients
     */
    @GetMapping
    public ResponseEntity<List<ClientDTO>> obtenirTousLesClients() {
        try {
            List<User> clients = userService.obtenirTousLesClients();
            List<ClientDTO> clientsDTO = clients.stream()
                    .map(ClientDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(clientsDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir un client par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> obtenirClientParId(@PathVariable Long id) {
        try {
            Optional<User> client = userService.obtenirClientParId(id);
            if (client.isPresent()) {
                return ResponseEntity.ok(ClientDTO.fromEntity(client.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Créer un nouveau client
//     */
    @PostMapping
    public ResponseEntity<ClientDTO> creerClient(@Valid @RequestBody ClientDTO clientDTO) {
        try {
            System.out.println("=== DEBUG: ClientDTO reçu: " + clientDTO);
            User client = clientDTO.toEntity();
            System.out.println("=== DEBUG: User converti: " + client);
            client.setRole(Role.CLIENT);
            User clientSauvegarde = userService.sauvegarderClient(client);
            System.out.println("=== DEBUG: User sauvegardé: " + clientSauvegarde);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ClientDTO.fromEntity(clientSauvegarde));
        } catch (RuntimeException e) {
            System.out.println("=== DEBUG: RuntimeException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.out.println("=== DEBUG: Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * Mettre à jour un client
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> mettreAJourClient(@PathVariable Long id,
                                                       @Valid @RequestBody ClientDTO clientDTO) {
        try {
            User clientMisAJour = clientDTO.toEntity();
            User clientSauvegarde = userService.mettreAJourClient(id, clientMisAJour);
            return ResponseEntity.ok(ClientDTO.fromEntity(clientSauvegarde));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Supprimer un client
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerClient(@PathVariable Long id) {
        try {
            userService.supprimerClient(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Rechercher des clients
     */
    @GetMapping("/recherche")
    public ResponseEntity<List<ClientDTO>> rechercherClients(@RequestParam String terme) {
        try {
            List<User> clients = userService.rechercherClients(terme);
            List<ClientDTO> clientsDTO = clients.stream()
                    .map(ClientDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(clientsDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Vérifier si un email existe
     */
    @GetMapping("/email-existe")
    public ResponseEntity<Boolean> emailExiste(@RequestParam String email) {
        try {
            boolean existe = userService.emailExiste(email);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir le nombre total de clients
     */
    @GetMapping("/statistiques/total")
    public ResponseEntity<Long> compterClients() {
        try {
            Long total = userService.compterClients();
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir un client avec ses commandes
     */
    @GetMapping("/{id}/commandes")
    public ResponseEntity<ClientDTO> obtenirClientAvecCommandes(@PathVariable Long id) {
        try {
            Optional<User> client = userService.obtenirClientAvecCommandes(id);
            if (client.isPresent()) {
                return ResponseEntity.ok(ClientDTO.fromEntity(client.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Activer/désactiver un client
     */
    @PatchMapping("/{id}/statut")
    public ResponseEntity<ClientDTO> changerStatutClient(@PathVariable Long id,
                                                         @RequestParam boolean actif) {
        try {
            User client = userService.changerStatutUtilisateur(id, actif);
            if (client.getRole() == Role.CLIENT) {
                return ResponseEntity.ok(ClientDTO.fromEntity(client));
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir les clients récents
     */
    @GetMapping("/recents")
    public ResponseEntity<List<ClientDTO>> obtenirClientsRecents() {
        try {
            List<User> clients = userService.obtenirClientsRecents();
            List<ClientDTO> clientsDTO = clients.stream()
                    .map(ClientDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(clientsDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}