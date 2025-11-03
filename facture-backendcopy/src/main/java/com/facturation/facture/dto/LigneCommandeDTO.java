package com.facturation.facture.dto;

import com.facturation.facture.model.LigneCommande;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class LigneCommandeDTO {

    private Long id;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au minimum 1")
    private Integer quantite;

    @NotNull(message = "Le prix unitaire est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix unitaire doit être positif")
    private BigDecimal prixUnitaire;

    @DecimalMin(value = "0.0", message = "Le sous-total ne peut pas être négatif")
    private BigDecimal sousTotal;

    private String nomProduit;

    @NotNull(message = "Le produit est obligatoire")
    private ProduitDTO produit;

    private Long commandeId;

    // Constructeurs
    public LigneCommandeDTO() {}

    public LigneCommandeDTO(Integer quantite, BigDecimal prixUnitaire, ProduitDTO produit) {
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.produit = produit;
        this.nomProduit = produit != null ? produit.getNom() : null;
        calculerSousTotal();
    }

    // Méthode de conversion depuis l'entité
    public static LigneCommandeDTO fromEntity(LigneCommande ligneCommande) {
        if (ligneCommande == null) return null;

        LigneCommandeDTO dto = new LigneCommandeDTO();
        dto.setId(ligneCommande.getId());
        dto.setQuantite(ligneCommande.getQuantite());
        dto.setPrixUnitaire(ligneCommande.getPrixUnitaire());
        dto.setSousTotal(ligneCommande.getSousTotal());
        dto.setNomProduit(ligneCommande.getNomProduit());
        dto.setProduit(ProduitDTO.fromEntity(ligneCommande.getProduit()));
        dto.setCommandeId(ligneCommande.getCommande() != null ? ligneCommande.getCommande().getId() : null);
        return dto;
    }

    // Méthode de conversion vers l'entité
    public LigneCommande toEntity() {
        LigneCommande entity = new LigneCommande();
        entity.setId(this.id);
        entity.setQuantite(this.quantite);
        entity.setPrixUnitaire(this.prixUnitaire);
        entity.setSousTotal(this.sousTotal);
        entity.setNomProduit(this.nomProduit);
        return entity;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
        calculerSousTotal();
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
        calculerSousTotal();
    }

    public BigDecimal getSousTotal() {
        return sousTotal;
    }

    public void setSousTotal(BigDecimal sousTotal) {
        this.sousTotal = sousTotal;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public ProduitDTO getProduit() {
        return produit;
    }

    public void setProduit(ProduitDTO produit) {
        this.produit = produit;
        if (produit != null) {
            this.nomProduit = produit.getNom();
        }
    }

    public Long getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(Long commandeId) {
        this.commandeId = commandeId;
    }

    // Méthode utilitaire pour calculer le sous-total
    private void calculerSousTotal() {
        if (this.quantite != null && this.prixUnitaire != null) {
            this.sousTotal = this.prixUnitaire.multiply(BigDecimal.valueOf(this.quantite));
        }
    }

    // Méthode pour recalculer le sous-total manuellement
    public void recalculerSousTotal() {
        calculerSousTotal();
    }

    @Override
    public String toString() {
        return "LigneCommandeDTO{" +
                "id=" + id +
                ", quantite=" + quantite +
                ", prixUnitaire=" + prixUnitaire +
                ", sousTotal=" + sousTotal +
                ", nomProduit='" + nomProduit + '\'' +
                ", commandeId=" + commandeId +
                '}';
    }
}