package com.facturation.facture.controller;

import com.facturation.facture.dto.ClientDTO;
import com.facturation.facture.model.User;
import com.facturation.facture.model.enums.Role;
import com.facturation.facture.security.JwtUtils;
import com.facturation.facture.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils; // 👈 ajoute ceci

    // ✅ 1. Inscription client
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody ClientDTO clientDTO) {
        // Vérifie si email existe déjà
        Optional<User> existant = userService.trouverParEmail(clientDTO.getEmail());
        if (existant.isPresent()) {
            return ResponseEntity.badRequest().body("Email déjà utilisé");
        }

        User newUser = clientDTO.toEntity();
        newUser.setRole(Role.CLIENT);
        User saved = userService.sauvegarderClient(newUser);

        saved.setMotDePasse(null);
        return ResponseEntity.ok(saved);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        Optional<User> userOpt = userService.trouverParEmail(loginRequest.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Email non trouvé");
        }

        User user = userOpt.get();

        if (!user.getMotDePasse().equals(loginRequest.getMotDePasse())) {
            return ResponseEntity.badRequest().body("Mot de passe incorrect");
        }

        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody User loginRequest) {
//        Optional<User> userOpt = userService.trouverParEmail(loginRequest.getEmail());
//
//        if (userOpt.isEmpty()) {
//            return ResponseEntity.badRequest().body("Email non trouvé");
//        }
//
//        User user = userOpt.get();
//
//        if (!user.getMotDePasse().equals(loginRequest.getMotDePasse())) {
//            return ResponseEntity.badRequest().body("Mot de passe incorrect");
//        }
//
//        // cache le mot de passe avant retour
//        user.setMotDePasse(null);
//
//        // Créer une réponse avec role
//        Map<String, Object> response = new HashMap<>();
//        response.put("id", user.getId());
//        response.put("email", user.getEmail());
//        response.put("role", user.getRole()); // ADMIN ou CLIENT
//
//        return ResponseEntity.ok(response);
//    }


}
