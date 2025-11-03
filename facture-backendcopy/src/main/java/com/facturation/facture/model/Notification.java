// 11. Entité Notification
package com.facturation.facture.model;

import com.facturation.facture.model.enums.TypeNotification;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 100, message = "Le titre ne peut pas dépasser 100 caractères")
    @Column(name = "titre", nullable = false, length = 100)
    private String titre;

    @NotBlank(message = "Le message est obligatoire")
    @Column(name = "message", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeNotification type;

    @Column(name = "lue", nullable = false)
    private Boolean lue = false;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    // Relation avec User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructeurs
    public Notification() {
        this.dateCreation = LocalDateTime.now();
        this.lue = false;
    }

    public Notification(String titre, String message, TypeNotification type, User user) {
        this();
        this.titre = titre;
        this.message = message;
        this.type = type;
        this.user = user;
    }

    // Getters et Setters complets...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TypeNotification getType() {
        return type;
    }

    public void setType(TypeNotification type) {
        this.type = type;
    }

    public Boolean getLue() {
        return lue;
    }

    public void setLue(Boolean lue) {
        this.lue = lue;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}