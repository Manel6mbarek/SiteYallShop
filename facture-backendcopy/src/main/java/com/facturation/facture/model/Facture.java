// 9. Entité Facture
package com.facturation.facture.model;

import com.facturation.facture.model.enums.ModePaiement;
import com.facturation.facture.model.enums.StatutFacture;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "factures")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_facture", unique = true, nullable = false)
    private String numeroFacture;

    @Column(name = "date_facture", nullable = false)
    private LocalDateTime dateFacture;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "date_paiement")
    private LocalDateTime datePaiement;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutFacture statut;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement")
    private ModePaiement modePaiement;

    @DecimalMin(value = "0.0", message = "Le montant HT ne peut pas être négatif")
    @Column(name = "montant_ht", precision = 10, scale = 2)
    private BigDecimal montantHT;

    @DecimalMin(value = "0.0", message = "Le montant TVA ne peut pas être négatif")
    @Column(name = "montant_tva", precision = 10, scale = 2)
    private BigDecimal montantTVA;

    @DecimalMin(value = "0.0", message = "Le montant TTC ne peut pas être négatif")
    @Column(name = "montant_ttc", precision = 10, scale = 2)
    private BigDecimal montantTTC;

    @Column(name = "commentaire")
    private String commentaire;

    // Relation avec Commande
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;
    @Column(name = "nom_client")
    private String nomClient;

    @Column(name = "statut_commande")
    private String statutCommande;


    // Constructeurs et méthodes...
    public Facture() {
        this.dateFacture = LocalDateTime.now();
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
        this.statut = StatutFacture.PAYEE;

    }


    public Facture(Long id, String numeroFacture, LocalDateTime dateFacture, LocalDateTime dateCreation, LocalDateTime dateModification, LocalDateTime datePaiement, StatutFacture statut, ModePaiement modePaiement, BigDecimal montantHT, BigDecimal montantTVA, BigDecimal montantTTC, String commentaire, Commande commande) {
        this.id = id;
        this.numeroFacture = numeroFacture;
        this.dateFacture = dateFacture;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
        this.datePaiement = datePaiement;
        this.statut = statut;
        this.modePaiement = modePaiement;
        this.montantHT = montantHT;
        this.montantTVA = montantTVA;
        this.montantTTC = montantTTC;
        this.commentaire = commentaire;
        this.commande = commande;
    }

    public Facture(Commande commande, ModePaiement modePaiement) {
        this.commande = commande;
        this.modePaiement = modePaiement;
        this.nomClient = commande.getClient() != null ? commande.getClient().getNom() : null;
        this.statutCommande = commande.getStatut() != null ? commande.getStatut().name() : null;
    }


    public boolean getPayee() {
        return this.statut == StatutFacture.PAYEE;
    }

    public void marquerCommePayee() {
        this.statut = StatutFacture.PAYEE;
        this.datePaiement = LocalDateTime.now();
    }

    public void marquerCommeNonPayee() {
        this.statut = StatutFacture.EN_ATTENTE; // ou un autre statut non payé
        this.datePaiement = null;
    }


    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    // Getters et Setters complets...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroFacture() {
        return numeroFacture;
    }

    public void setNumeroFacture(String numeroFacture) {
        this.numeroFacture = numeroFacture;
    }

    public LocalDateTime getDateFacture() {
        return dateFacture;
    }

    public void setDateFacture(LocalDateTime dateFacture) {
        this.dateFacture = dateFacture;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }

    public StatutFacture getStatut() {
        return statut;
    }

    public void setStatut(StatutFacture statut) {
        this.statut = statut;
    }

    public ModePaiement getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(ModePaiement modePaiement) {
        this.modePaiement = modePaiement;
    }

    public BigDecimal getMontantHT() {
        return montantHT;
    }

    public void setMontantHT(BigDecimal montantHT) {
        this.montantHT = montantHT;
    }

    public BigDecimal getMontantTVA() {
        return montantTVA;
    }

    public void setMontantTVA(BigDecimal montantTVA) {
        this.montantTVA = montantTVA;
    }

    public BigDecimal getMontantTTC() {
        return montantTTC;
    }

    public void setMontantTTC(BigDecimal montantTTC) {
        this.montantTTC = montantTTC;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getStatutCommande() {
        return statutCommande;
    }

    public void setStatutCommande(String statutCommande) {
        this.statutCommande = statutCommande;
    }
}
