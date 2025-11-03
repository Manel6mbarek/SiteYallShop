package com.facturation.facture.dto;

import com.facturation.facture.model.Produit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProduitDTO {

    private Long id;

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    private BigDecimal prix;

    @Min(value = 0, message = "La quantité en stock ne peut pas être négative")
    private Integer quantiteStock = 0;

    @Min(value = 0, message = "Le seuil d'alerte ne peut pas être négatif")
    private Integer seuilAlerte = 0;

    private Boolean disponible = true;

    private String imagePath;

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    // ID de la catégorie pour éviter la circularité
    @NotNull(message = "La catégorie est obligatoire")
    private Long categorieId;

    // Nom de la catégorie pour l'affichage (optionnel)
    private String categorieNom;

    // Constructeurs
    public ProduitDTO() {}

    public ProduitDTO(Long id, String nom, String description, BigDecimal prix,
                      Integer quantiteStock, Integer seuilAlerte, Boolean disponible,
                      String imagePath, Long categorieId, String categorieNom) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
        this.seuilAlerte = seuilAlerte;
        this.disponible = disponible;
        this.imagePath = imagePath;
        this.categorieId = categorieId;
        this.categorieNom = categorieNom;
    }

    // Méthode de conversion depuis l'entité
    public static ProduitDTO fromEntity(Produit produit) {
        if (produit == null) return null;

        ProduitDTO dto = new ProduitDTO();
        dto.setId(produit.getId());
        dto.setNom(produit.getNom());
        dto.setDescription(produit.getDescription());
        dto.setPrix(produit.getPrix());
        dto.setQuantiteStock(produit.getQuantiteStock());
        dto.setSeuilAlerte(produit.getSeuilAlerte());
        dto.setDisponible(produit.getDisponible());
        dto.setImagePath(produit.getImagePath());
        dto.setDateCreation(produit.getDateCreation());
        dto.setDateModification(produit.getDateModification());

        // Gestion de la catégorie
        if (produit.getCategorie() != null) {
            dto.setCategorieId(produit.getCategorie().getId());
            dto.setCategorieNom(produit.getCategorie().getNom());
        }

        return dto;
    }

    // Méthode de conversion vers l'entité (partielle - la catégorie doit être gérée dans le service)
    public Produit toEntity() {
        Produit produit = new Produit();
        produit.setId(this.id);
        produit.setNom(this.nom);
        produit.setDescription(this.description);
        produit.setPrix(this.prix);
        produit.setQuantiteStock(this.quantiteStock);
        produit.setSeuilAlerte(this.seuilAlerte);
        produit.setDisponible(this.disponible);
        produit.setImagePath(this.imagePath);
        // Note: La catégorie doit être définie dans le service
        return produit;
    }

    // Méthode utilitaire pour vérifier si le stock est en dessous du seuil d'alerte
    public boolean isStockFaible() {
        return quantiteStock != null && seuilAlerte != null && quantiteStock <= seuilAlerte;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }

    public Integer getQuantiteStock() { return quantiteStock; }
    public void setQuantiteStock(Integer quantiteStock) { this.quantiteStock = quantiteStock; }

    public Integer getSeuilAlerte() { return seuilAlerte; }
    public void setSeuilAlerte(Integer seuilAlerte) { this.seuilAlerte = seuilAlerte; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    public Long getCategorieId() { return categorieId; }
    public void setCategorieId(Long categorieId) { this.categorieId = categorieId; }

    public String getCategorieNom() { return categorieNom; }
    public void setCategorieNom(String categorieNom) { this.categorieNom = categorieNom; }
}