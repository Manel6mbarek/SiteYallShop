package com.facturation.facture.dto;

import com.facturation.facture.model.Facture;
import com.facturation.facture.model.enums.StatutFacture;
import com.facturation.facture.model.enums.ModePaiement;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FactureDTO {

    private Long id;
    private String numeroFacture;
    private LocalDateTime dateCreation;
    private LocalDateTime dateFacture;
    private LocalDateTime dateModification;
    private LocalDateTime datePaiement;

    @NotNull(message = "Le statut est obligatoire")
    private StatutFacture statut;

    private ModePaiement modePaiement;

    @DecimalMin(value = "0.0", message = "Le montant HT ne peut pas être négatif")
    private BigDecimal montantHT;

    @DecimalMin(value = "0.0", message = "Le montant TVA ne peut pas être négatif")
    private BigDecimal montantTVA;

    @DecimalMin(value = "0.0", message = "Le montant TTC ne peut pas être négatif")
    private BigDecimal montantTTC;

    private String nomClient;
    private String statutCommande;

    // Informations de la commande associée
    private CommandeDTO commande;
    private Long commandeId;
    private String numeroCommande;

    // Informations client (pour éviter de charger toute la commande)
    private Long clientId;
    private String emailClient;
    private String telephoneClient;

    // Constructeurs
    public FactureDTO() {}

    // Méthode de conversion depuis l'entité
    public static FactureDTO fromEntity(Facture facture) {
        if (facture == null) return null;

        FactureDTO dto = new FactureDTO();
        dto.setId(facture.getId());
        dto.setNumeroFacture(facture.getNumeroFacture());
        dto.setDateCreation(facture.getDateCreation());
        dto.setDateFacture(facture.getDateFacture());
        dto.setDateModification(facture.getDateModification());
        dto.setDatePaiement(facture.getDatePaiement());
        dto.setStatut(facture.getStatut());
        dto.setModePaiement(facture.getModePaiement());
        dto.setMontantHT(facture.getMontantHT());
        dto.setMontantTVA(facture.getMontantTVA());
        dto.setMontantTTC(facture.getMontantTTC());
        dto.setNomClient(facture.getNomClient());
        dto.setStatutCommande(facture.getStatutCommande());

        // Informations de la commande
        if (facture.getCommande() != null) {
            dto.setCommandeId(facture.getCommande().getId());
            dto.setNumeroCommande(facture.getCommande().getNumeroCommande());

            // Informations client depuis la commande
            if (facture.getCommande().getClient() != null) {
                dto.setClientId(facture.getCommande().getClient().getId());
                dto.setEmailClient(facture.getCommande().getClient().getEmail());
                // Ajouter d'autres champs client si nécessaire
                // dto.setTelephoneClient(facture.getCommande().getClient().getTelephone());
            }

            // Inclure la commande complète si nécessaire (optionnel)
            // dto.setCommande(CommandeDTO.fromEntity(facture.getCommande()));
        }

        return dto;
    }

    // Méthode de conversion vers l'entité
    public Facture toEntity() {
        Facture facture = new Facture();
        facture.setId(this.id);
        facture.setNumeroFacture(this.numeroFacture);
        facture.setDateCreation(this.dateCreation);
        facture.setDateFacture(this.dateFacture);
        facture.setDateModification(this.dateModification);
        facture.setDatePaiement(this.datePaiement);
        facture.setStatut(this.statut);
        facture.setModePaiement(this.modePaiement);
        facture.setMontantHT(this.montantHT);
        facture.setMontantTVA(this.montantTVA);
        facture.setMontantTTC(this.montantTTC);
        facture.setNomClient(this.nomClient);
        facture.setStatutCommande(this.statutCommande);

        return facture;
    }

    // Version avec commande complète
    public static FactureDTO fromEntityWithCommande(Facture facture) {
        FactureDTO dto = fromEntity(facture);
        if (facture.getCommande() != null) {
            dto.setCommande(CommandeDTO.fromEntity(facture.getCommande()));
        }
        return dto;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroFacture() { return numeroFacture; }
    public void setNumeroFacture(String numeroFacture) { this.numeroFacture = numeroFacture; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateFacture() { return dateFacture; }
    public void setDateFacture(LocalDateTime dateFacture) { this.dateFacture = dateFacture; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    public LocalDateTime getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDateTime datePaiement) { this.datePaiement = datePaiement; }

    public StatutFacture getStatut() { return statut; }
    public void setStatut(StatutFacture statut) { this.statut = statut; }

    public ModePaiement getModePaiement() { return modePaiement; }
    public void setModePaiement(ModePaiement modePaiement) { this.modePaiement = modePaiement; }

    public BigDecimal getMontantHT() { return montantHT; }
    public void setMontantHT(BigDecimal montantHT) { this.montantHT = montantHT; }

    public BigDecimal getMontantTVA() { return montantTVA; }
    public void setMontantTVA(BigDecimal montantTVA) { this.montantTVA = montantTVA; }

    public BigDecimal getMontantTTC() { return montantTTC; }
    public void setMontantTTC(BigDecimal montantTTC) { this.montantTTC = montantTTC; }

    public String getNomClient() { return nomClient; }
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }

    public String getStatutCommande() { return statutCommande; }
    public void setStatutCommande(String statutCommande) { this.statutCommande = statutCommande; }

    public CommandeDTO getCommande() { return commande; }
    public void setCommande(CommandeDTO commande) { this.commande = commande; }

    public Long getCommandeId() { return commandeId; }
    public void setCommandeId(Long commandeId) { this.commandeId = commandeId; }

    public String getNumeroCommande() { return numeroCommande; }
    public void setNumeroCommande(String numeroCommande) { this.numeroCommande = numeroCommande; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getEmailClient() { return emailClient; }
    public void setEmailClient(String emailClient) { this.emailClient = emailClient; }

    public String getTelephoneClient() { return telephoneClient; }
    public void setTelephoneClient(String telephoneClient) { this.telephoneClient = telephoneClient; }

    // Méthodes utilitaires
    public boolean estPayee() {
        return StatutFacture.PAYEE.equals(this.statut);
    }

    public boolean estEnAttente() {
        return StatutFacture.EN_ATTENTE.equals(this.statut);
    }

    public boolean estAnnulee() {
        return StatutFacture.ANNULEE.equals(this.statut);
    }

    public String getStatutLibelle() {
        if (statut == null) return "";
        switch (statut) {
            case EN_ATTENTE: return "En attente";
            case PAYEE: return "Payée";
            case ANNULEE: return "Annulée";
            default: return statut.name();
        }
    }

    public String getModePaiementLibelle() {
        if (modePaiement == null) return "";
        switch (modePaiement) {
            case ESPECES: return "Espèces";
            case CARTE_BANCAIRE: return "Carte bancaire";
            case CHEQUE: return "Chèque";
            case VIREMENT: return "Virement";
            default: return modePaiement.name();
        }
    }
}