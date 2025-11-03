package com.facturation.facture.dto;

import com.facturation.facture.model.Commande;
import com.facturation.facture.model.enums.StatutCommande;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommandeDTO {

    private Long id;
    private String numeroCommande;
    private LocalDateTime dateCommande;
    private LocalDateTime dateModification;

    private StatutCommande statut;

    @DecimalMin(value = "0.0", message = "Le sous-total ne peut pas être négatif")
    private BigDecimal sousTotal;

    @DecimalMin(value = "0.0", message = "Le taux TVA ne peut pas être négatif")
    private BigDecimal tauxTVA;

    @DecimalMin(value = "0.0", message = "Le montant TVA ne peut pas être négatif")
    private BigDecimal montantTVA;

    @DecimalMin(value = "0.0", message = "Le total HT ne peut pas être négatif")
    private BigDecimal totalHT;

    @DecimalMin(value = "0.0", message = "Le total TTC ne peut pas être négatif")
    private BigDecimal totalTTC;

    private String commentaire;

    @NotNull(message = "Le client est obligatoire")
    private UserDTO client;

    private List<LigneCommandeDTO> lignesCommande;
    private Integer nombreArticles;

    // Constructeurs
    public CommandeDTO() {}

    // Méthode de conversion depuis l'entité
    public static CommandeDTO fromEntity(Commande commande) {
        if (commande == null) return null;

        CommandeDTO dto = new CommandeDTO();
        dto.setId(commande.getId());
        dto.setNumeroCommande(commande.getNumeroCommande());
        dto.setDateCommande(commande.getDateCommande());
        dto.setDateModification(commande.getDateModification());
        dto.setStatut(commande.getStatut());
        dto.setSousTotal(commande.getSousTotal());
        dto.setTauxTVA(commande.getTauxTVA());
        dto.setMontantTVA(commande.getMontantTVA());
        dto.setTotalHT(commande.getTotalHT());
        dto.setTotalTTC(commande.getTotalTTC());
        dto.setCommentaire(commande.getCommentaire());
        dto.setClient(UserDTO.fromEntity(commande.getClient()));

        if (commande.getLignesCommande() != null) {
            dto.setLignesCommande(commande.getLignesCommande().stream()
                    .map(LigneCommandeDTO::fromEntity)
                    .collect(Collectors.toList()));
            dto.setNombreArticles(commande.getLignesCommande().size());
        }

        return dto;
    }

    // Méthode de conversion vers l'entité
    public Commande toEntity() {
        Commande commande = new Commande();
        commande.setId(this.id);
        commande.setNumeroCommande(this.numeroCommande);
        commande.setDateCommande(this.dateCommande);
        commande.setDateModification(this.dateModification);
        commande.setStatut(this.statut);
        commande.setSousTotal(this.sousTotal);
        commande.setTauxTVA(this.tauxTVA);
        commande.setMontantTVA(this.montantTVA);
        commande.setTotalHT(this.totalHT);
        commande.setTotalTTC(this.totalTTC);
        commande.setCommentaire(this.commentaire);

        if (this.client != null) {
            commande.setClient(this.client.toEntity());
        }

        return commande;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroCommande() { return numeroCommande; }
    public void setNumeroCommande(String numeroCommande) { this.numeroCommande = numeroCommande; }

    public LocalDateTime getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDateTime dateCommande) { this.dateCommande = dateCommande; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    public StatutCommande getStatut() { return statut; }
    public void setStatut(StatutCommande statut) { this.statut = statut; }

    public BigDecimal getSousTotal() { return sousTotal; }
    public void setSousTotal(BigDecimal sousTotal) { this.sousTotal = sousTotal; }

    public BigDecimal getTauxTVA() { return tauxTVA; }
    public void setTauxTVA(BigDecimal tauxTVA) { this.tauxTVA = tauxTVA; }

    public BigDecimal getMontantTVA() { return montantTVA; }
    public void setMontantTVA(BigDecimal montantTVA) { this.montantTVA = montantTVA; }

    public BigDecimal getTotalHT() { return totalHT; }
    public void setTotalHT(BigDecimal totalHT) { this.totalHT = totalHT; }

    public BigDecimal getTotalTTC() { return totalTTC; }
    public void setTotalTTC(BigDecimal totalTTC) { this.totalTTC = totalTTC; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public UserDTO getClient() { return client; }
    public void setClient(UserDTO client) { this.client = client; }

    public List<LigneCommandeDTO> getLignesCommande() { return lignesCommande; }
    public void setLignesCommande(List<LigneCommandeDTO> lignesCommande) { this.lignesCommande = lignesCommande; }

    public Integer getNombreArticles() { return nombreArticles; }
    public void setNombreArticles(Integer nombreArticles) { this.nombreArticles = nombreArticles; }
}