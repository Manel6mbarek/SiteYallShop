
// ============================================
// ClientDTO spécialisé (optionnel)
// ============================================
package com.facturation.facture.dto;

import com.facturation.facture.model.User;
import com.facturation.facture.model.enums.Role;

/**
 * DTO spécialisé pour les clients uniquement
 * Hérite de UserDTO mais force le rôle CLIENT
 */
public class ClientDTO extends UserDTO {

    public ClientDTO() {
        super();
        setRole(Role.CLIENT);
    }

    public ClientDTO(Long id, String nom, String prenom, String email, String telephone, String adresse) {
        super(id, nom, prenom, email, telephone, adresse, Role.CLIENT);
    }

    // Méthode de conversion depuis l'entité (version client)
    public static ClientDTO fromEntity(User user) {
        if (user == null) return null;

        // Vérifier que c'est bien un client
        if (user.getRole() != Role.CLIENT) {
            throw new IllegalArgumentException("L'utilisateur n'est pas un client");
        }

        ClientDTO dto = new ClientDTO();
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

    // Override pour forcer le rôle CLIENT
    @Override
    public void setRole(Role role) {
        super.setRole(Role.CLIENT);
    }

    @Override
    public User toEntity() {
        User user = super.toEntity();
        user.setRole(Role.CLIENT); // Force le rôle CLIENT
        return user;
    }
}
