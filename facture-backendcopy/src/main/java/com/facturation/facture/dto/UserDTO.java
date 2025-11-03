// UserDTO.java
package com.facturation.facture.dto;

import com.facturation.facture.model.User;
import com.facturation.facture.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class UserDTO {

    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String prenom;

    @Email(message = "Format d'email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @Size(min = 8, max = 15, message = "Le téléphone doit contenir entre 8 et 15 chiffres")
    private String telephone;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;


    private String adresse;

    @NotNull(message = "Le rôle est obligatoire")
    private Role role;

    private Boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private String nomComplet;

    // Constructeurs
    public UserDTO() {
        this.actif = true; // Par défaut actif
    }


    public UserDTO(Long id, String nom, String prenom, String email, String telephone, String adresse, Role role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
        this.role = role;
        this.actif = true;
        this.nomComplet = prenom + " " + nom;
    }

    // Méthode de conversion depuis l'entité
    public static UserDTO fromEntity(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setNom(user.getNom());
        dto.setPrenom(user.getPrenom());
        dto.setEmail(user.getEmail());
        dto.setTelephone(user.getTelephone());
        dto.setAdresse(user.getAdresse());
        dto.setRole(user.getRole());
        dto.setActif(user.getActif());
        dto.setDateCreation(user.getDateCreation());
        dto.setDateModification(user.getDateModification());
        dto.setNomComplet(user.getPrenom() + " " + user.getNom());
        return dto;
    }

    // Méthode de conversion vers l'entité
    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setNom(this.nom);
        user.setPrenom(this.prenom);
        user.setEmail(this.email);
        user.setMotDePasse(this.motDePasse); // AJOUTER CETTE LIGNE
        user.setTelephone(this.telephone);
        user.setAdresse(this.adresse);
        user.setRole(this.role != null ? this.role : Role.CLIENT); // Par défaut CLIENT
        user.setActif(this.actif != null ? this.actif : true);
        return user;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) {
        this.nom = nom;
        updateNomComplet();
    }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
        updateNomComplet();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    public String getNomComplet() { return nomComplet; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }

    // Méthodes utilitaires
    private void updateNomComplet() {
        if (this.prenom != null && this.nom != null) {
            this.nomComplet = this.prenom + " " + this.nom;
        }
    }

    public boolean estClient() {
        return Role.CLIENT.equals(this.role);
    }

    public boolean estAdmin() {
        return Role.ADMIN.equals(this.role);
    }

    public boolean estActif() {
        return Boolean.TRUE.equals(this.actif);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", actif=" + actif +
                '}';
    }
}
