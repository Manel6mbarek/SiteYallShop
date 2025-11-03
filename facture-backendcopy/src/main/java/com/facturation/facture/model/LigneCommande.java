// 7. Entité LigneCommande
package com.facturation.facture.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "lignes_commande")
public class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "La quantité doit être au moins de 1")
    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix unitaire doit être positif")
    @Column(name = "prix_unitaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @DecimalMin(value = "0.0", message = "Le sous-total ne peut pas être négatif")
    @Column(name = "sous_total", precision = 10, scale = 2)
    private BigDecimal sousTotal;

    @Column(name = "nom_produit", length = 100)
    private String nomProduit;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    // ... tes champs existants ...

    public void calculerSousTotal() {
        if (this.quantite != null && this.prixUnitaire != null) {
            this.sousTotal = this.prixUnitaire.multiply(BigDecimal.valueOf(this.quantite));
        } else {
            this.sousTotal = BigDecimal.ZERO;
        }
    }



    // Constructeurs et méthodes...
    public LigneCommande() {}

    public LigneCommande(Integer quantite, BigDecimal prixUnitaire, Commande commande, Produit produit) {
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.commande = commande;
        this.produit = produit;
        this.nomProduit = produit.getNom();
        this.sousTotal = prixUnitaire.multiply(BigDecimal.valueOf(quantite));
    }

    // Getters et Setters complets...


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
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
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

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }
}