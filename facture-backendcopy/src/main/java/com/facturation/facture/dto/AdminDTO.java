
// ============================================
// AdminDTO spécialisé (optionnel)
// ============================================
package com.facturation.facture.dto;

import com.facturation.facture.model.User;
import com.facturation.facture.model.enums.Role;

/**
 * DTO spécialisé pour les administrateurs uniquement
 */
public class AdminDTO extends UserDTO {

    public AdminDTO() {
        super();
        setRole(Role.ADMIN);
    }

    public AdminDTO(Long id, String nom, String prenom, String email, String telephone, String adresse) {
        super(id, nom, prenom, email, telephone, adresse, Role.ADMIN);
    }

    // Méthode de conversion depuis l'entité (version admin)
    public static AdminDTO fromEntity(User user) {
        if (user == null) return null;

        // Vérifier que c'est bien un admin
        if (user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("L'utilisateur n'est pas un administrateur");
        }

        AdminDTO dto = new AdminDTO();
        dto.setId(user.getId());
        dto.setNom(user.getNom());
        dto.setPrenom(user.getPrenom());
        dto.setEmail(user.getEmail());
        dto.setTelephone(user.getTelephone());
        dto.setAdresse(user.getAdresse());
        dto.setActif(user.getActif());
        dto.setDateCreation(user.getDateCreation());
        dto.setDateModification(user.getDateModification());
        dto.setNomComplet(user.getPrenom() + " " + user.getNom());
        return dto;
    }

    // Override pour forcer le rôle ADMIN
    @Override
    public void setRole(Role role) {
        super.setRole(Role.ADMIN);
    }

    @Override
    public User toEntity() {
        User user = super.toEntity();
        user.setRole(Role.ADMIN); // Force le rôle ADMIN
        return user;
    }
}