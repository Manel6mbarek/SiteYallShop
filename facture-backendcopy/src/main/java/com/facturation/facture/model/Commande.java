package com.facturation.facture.model;

import com.facturation.facture.model.enums.StatutCommande;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Entity
@Table(name = "commandes")
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_commande", unique = true, nullable = false)
    private String numeroCommande;

    @Column(name = "date_commande", nullable = false)
    private LocalDateTime dateCommande;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutCommande statut;

    @DecimalMin(value = "0.0", message = "Le sous-total ne peut pas être négatif")
    @Column(name = "sous_total", precision = 10, scale = 2)
    private BigDecimal sousTotal;

    @DecimalMin(value = "0.0", message = "Le taux TVA ne peut pas être négatif")
    @Column(name = "taux_tva", precision = 5, scale = 2)
    private BigDecimal tauxTVA;

    @DecimalMin(value = "0.0", message = "Le montant TVA ne peut pas être négatif")
    @Column(name = "montant_tva", precision = 10, scale = 2)
    private BigDecimal montantTVA;

    @DecimalMin(value = "0.0", message = "Le total HT ne peut pas être négatif")
    @Column(name = "total_ht", precision = 10, scale = 2)
    private BigDecimal totalHT;

    @DecimalMin(value = "0.0", message = "Le total TTC ne peut pas être négatif")
    @Column(name = "total_ttc", precision = 10, scale = 2)
    private BigDecimal totalTTC;

    @Column(name = "commentaire")
    private String commentaire;

    // Relation avec User (client)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    // Relations
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LigneCommande> lignesCommande = new ArrayList<>();

    @OneToOne(mappedBy = "commande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Facture facture;

    // Constructeurs
    public Commande() {
        this.dateCommande = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
        this.statut = StatutCommande.EN_ATTENTE;
        this.sousTotal = BigDecimal.ZERO;
        this.tauxTVA = new BigDecimal("20.00");
        this.montantTVA = BigDecimal.ZERO;
        this.totalHT = BigDecimal.ZERO;
        this.totalTTC = BigDecimal.ZERO;
        this.lignesCommande = new ArrayList<>();
    }

    public Commande(User client) {
        this();
        this.client = client;
    }

    // Méthode appelée avant la mise à jour
    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    // Méthodes métier

    /**
     * Vérifier si la commande peut être modifiée
     */
    public boolean peutEtreModifiee() {
        return this.statut == StatutCommande.EN_ATTENTE;
    }

    /**
     * Ajouter une ligne de commande
     */
    public void ajouterLigneCommande(LigneCommande ligneCommande) {
        if (this.lignesCommande == null) {
            this.lignesCommande = new ArrayList<>();
        }
        this.lignesCommande.add(ligneCommande);
        ligneCommande.setCommande(this);
    }

    /**
     * Supprimer une ligne de commande
     */
    public void supprimerLigneCommande(LigneCommande ligneCommande) {
        if (this.lignesCommande != null) {
            this.lignesCommande.remove(ligneCommande);
            ligneCommande.setCommande(null);
        }
    }

    /**
     * Calculer les totaux de la commande
     */
    public void calculerTotaux() {
        BigDecimal sousTotalCalcule = BigDecimal.ZERO;

        if (this.lignesCommande != null) {
            for (LigneCommande ligne : this.lignesCommande) {
                ligne.calculerSousTotal();
                sousTotalCalcule = sousTotalCalcule.add(ligne.getSousTotal());
            }
        }

        this.sousTotal = sousTotalCalcule;
        this.totalHT = sousTotalCalcule;

        // Calculer le montant de la TVA
        if (this.tauxTVA != null) {
            this.montantTVA = sousTotalCalcule.multiply(this.tauxTVA).divide(new BigDecimal("100"));
        } else {
            this.montantTVA = BigDecimal.ZERO;
        }

        // Calculer le total TTC
        this.totalTTC = this.totalHT.add(this.montantTVA);

        this.dateModification = LocalDateTime.now();
    }

    /**
     * Obtenir le nombre total d'articles dans la commande
     */
    public Integer getNombreTotalArticles() {
        if (this.lignesCommande == null || this.lignesCommande.isEmpty()) {
            return 0;
        }

        return this.lignesCommande.stream()
                .mapToInt(LigneCommande::getQuantite)
                .sum();
    }

    /**
     * Vérifier si la commande est vide
     */
    public boolean estVide() {
        return this.lignesCommande == null || this.lignesCommande.isEmpty();
    }

    /**
     * Obtenir une ligne de commande par produit
     */
    public LigneCommande getLigneCommandeParProduit(Long idProduit) {
        if (this.lignesCommande == null) {
            return null;
        }

        return this.lignesCommande.stream()
                .filter(ligne -> ligne.getProduit().getId().equals(idProduit))
                .findFirst()
                .orElse(null);
    }

    /**
     * Vérifier si un produit est déjà dans la commande
     */
    public boolean contientProduit(Long idProduit) {
        return getLigneCommandeParProduit(idProduit) != null;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroCommande() {
        return numeroCommande;
    }

    public void setNumeroCommande(String numeroCommande) {
        this.numeroCommande = numeroCommande;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDateTime dateCommande) {
        this.dateCommande = dateCommande;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public StatutCommande getStatut() {
        return statut;
    }

    public void setStatut(StatutCommande statut) {
        this.statut = statut;
    }

    public BigDecimal getSousTotal() {
        return sousTotal;
    }

    public void setSousTotal(BigDecimal sousTotal) {
        this.sousTotal = sousTotal;
    }

    public BigDecimal getTauxTVA() {
        return tauxTVA;
    }

    public void setTauxTVA(BigDecimal tauxTVA) {
        this.tauxTVA = tauxTVA;
    }

    public BigDecimal getMontantTVA() {
        return montantTVA;
    }

    public void setMontantTVA(BigDecimal montantTVA) {
        this.montantTVA = montantTVA;
    }

    public BigDecimal getTotalHT() {
        return totalHT;
    }

    public void setTotalHT(BigDecimal totalHT) {
        this.totalHT = totalHT;
    }

    public BigDecimal getTotalTTC() {
        return totalTTC;
    }

    public void setTotalTTC(BigDecimal totalTTC) {
        this.totalTTC = totalTTC;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public List<LigneCommande> getLignesCommande() {
        return lignesCommande;
    }

    public void setLignesCommande(List<LigneCommande> lignesCommande) {
        this.lignesCommande = lignesCommande;
    }

    public Facture getFacture() {
        return facture;
    }

    public void setFacture(Facture facture) {
        this.facture = facture;
    }

    // Méthodes toString, equals et hashCode
    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", numeroCommande='" + numeroCommande + '\'' +
                ", dateCommande=" + dateCommande +
                ", statut=" + statut +
                ", totalTTC=" + totalTTC +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Commande)) return false;
        Commande commande = (Commande) o;
        return id != null && id.equals(commande.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}