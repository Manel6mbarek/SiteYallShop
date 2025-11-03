package com.facturation.facture.repository;

import com.facturation.facture.model.Commande;
import com.facturation.facture.model.User;
import com.facturation.facture.model.enums.StatutCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    // Méthodes de recherche automatiques Spring Data JPA

    /**
     * Recherche des commandes par client
     */
    List<Commande> findByClient(User client);

    /**
     * Recherche des commandes par ID client
     */
    @Query("SELECT c FROM Commande c WHERE c.client.id = :idClient")
    List<Commande> findByClientId(@Param("idClient") Long idClient);

    /**
     * Recherche des commandes par numéro
     */
    Optional<Commande> findByNumeroCommande(String numeroCommande);

    /**
     * Recherche des commandes par statut
     */
    List<Commande> findByStatut(StatutCommande statut);

    /**
     * Recherche des commandes par statut et client
     */
    List<Commande> findByStatutAndClient(StatutCommande statut, User client);

    /**
     * Recherche des commandes entre deux dates
     */
    List<Commande> findByDateCommandeBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    /**
     * Recherche des commandes par montant minimum
     */
    List<Commande> findByTotalTTCGreaterThanEqual(BigDecimal montantMinimum);

    /**
     * Recherche des commandes d'aujourd'hui
     */
    @Query("SELECT c FROM Commande c WHERE DATE(c.dateCommande) = CURRENT_DATE")
    List<Commande> findCommandesAujourdhui();

    // Requêtes JPQL personnalisées avec relations

    /**
     * Obtenir une commande avec ses lignes de commande et produits
     */
    @Query("SELECT DISTINCT c FROM Commande c " +
            "LEFT JOIN FETCH c.lignesCommande lc " +
            "LEFT JOIN FETCH lc.produit " +
            "WHERE c.id = :id")
    Optional<Commande> findByIdWithLignesCommande(@Param("id") Long id);

    /**
     * Obtenir une commande avec son client et ses lignes
     */
    @Query("SELECT DISTINCT c FROM Commande c " +
            "LEFT JOIN FETCH c.client " +
            "LEFT JOIN FETCH c.lignesCommande lc " +
            "LEFT JOIN FETCH lc.produit " +
            "WHERE c.id = :id")
    Optional<Commande> findByIdWithDetails(@Param("id") Long id);

    /**
     * Recherche des commandes par période avec détails client
     */
    @Query("SELECT DISTINCT c FROM Commande c " +
            "LEFT JOIN FETCH c.client " +
            "WHERE c.dateCommande BETWEEN :dateDebut AND :dateFin " +
            "ORDER BY c.dateCommande DESC")
    List<Commande> findByPeriodeWithClient(@Param("dateDebut") LocalDateTime dateDebut,
                                           @Param("dateFin") LocalDateTime dateFin);

    /**
     * Obtenir les commandes d'un client avec pagination
     */
    @Query("SELECT c FROM Commande c WHERE c.client.id = :idClient ORDER BY c.dateCommande DESC")
    List<Commande> findByClientIdOrderByDateCommandeDesc(@Param("idClient") Long idClient);

    // Requêtes de statistiques

    /**
     * Compter les commandes par statut
     */
    @Query("SELECT COUNT(c) FROM Commande c WHERE c.statut = :statut")
    Long countByStatut(@Param("statut") StatutCommande statut);

    /**
     * Calculer le chiffre d'affaires total
     */
    @Query("SELECT COALESCE(SUM(c.totalTTC), 0) FROM Commande c WHERE c.statut = :statut")
    BigDecimal calculateChiffreAffaires(@Param("statut") StatutCommande statut);

    /**
     * Calculer le chiffre d'affaires par période
     */
    @Query("SELECT COALESCE(SUM(c.totalTTC), 0) FROM Commande c WHERE c.statut = 'PAYEE' " +
            "AND c.dateCommande BETWEEN :dateDebut AND :dateFin")
    BigDecimal calculateChiffreAffairesPeriode(@Param("dateDebut") LocalDateTime dateDebut,
                                               @Param("dateFin") LocalDateTime dateFin);

    /**
     * Obtenir la valeur moyenne des commandes
     */
    @Query("SELECT COALESCE(AVG(c.totalTTC), 0) FROM Commande c WHERE c.statut = 'PAYEE'")
    BigDecimal getValeurMoyenneCommandes();

    /**
     * Obtenir le nombre de commandes par client
     */
    @Query("SELECT COUNT(c) FROM Commande c WHERE c.client.id = :idClient")
    Long countByClientId(@Param("idClient") Long idClient);

    /**
     * Obtenir les meilleurs clients (par nombre de commandes)
     */
    @Query("SELECT c.client, COUNT(c) as nbCommandes FROM Commande c " +
            "GROUP BY c.client ORDER BY nbCommandes DESC")
    List<Object[]> findTopClientsByNombreCommandes();

    /**
     * Obtenir les meilleurs clients (par chiffre d'affaires)
     */
    @Query("SELECT c.client, SUM(c.totalTTC) as chiffresAffaires FROM Commande c " +
            "WHERE c.statut = 'PAYEE' " +
            "GROUP BY c.client ORDER BY chiffresAffaires DESC")
    List<Object[]> findTopClientsByChiffreAffaires();

    /**
     * Obtenir les commandes récentes (dernières 24h)
     */
    @Query("SELECT c FROM Commande c WHERE c.dateCommande >= :date24hAgo ORDER BY c.dateCommande DESC")
    List<Commande> findCommandesRecentes(@Param("date24hAgo") LocalDateTime date24hAgo);

    /**
     * Recherche des commandes en attente de paiement
     */
    @Query("SELECT c FROM Commande c WHERE c.statut = 'EN_ATTENTE' " +
            "AND c.dateCommande <= :dateExpiration")
    List<Commande> findCommandesEnAttente(@Param("dateExpiration") LocalDateTime dateExpiration);

    /**
     * Trouver le dernier numéro de commande pour générer le prochain
     */
    @Query("SELECT c.numeroCommande FROM Commande c ORDER BY c.id DESC LIMIT 1")
    Optional<String> findLastNumeroCommande();

    /**
     * Recherche par commentaire (recherche textuelle)
     */
    @Query("SELECT c FROM Commande c WHERE c.commentaire LIKE %:keyword%")
    List<Commande> findByCommentaireContaining(@Param("keyword") String keyword);

    /**
     * Obtenir les commandes modifiées après une date
     */
    List<Commande> findByDateModificationAfter(LocalDateTime dateModification);

    /**
     * Calculer le sous-total total pour une période
     */
    @Query("SELECT COALESCE(SUM(c.sousTotal), 0) FROM Commande c " +
            "WHERE c.dateCommande BETWEEN :dateDebut AND :dateFin")
    BigDecimal calculateSousTotalPeriode(@Param("dateDebut") LocalDateTime dateDebut,
                                         @Param("dateFin") LocalDateTime dateFin);

    /**
     * Calculer le montant TVA total pour une période
     */
    @Query("SELECT COALESCE(SUM(c.montantTVA), 0) FROM Commande c " +
            "WHERE c.dateCommande BETWEEN :dateDebut AND :dateFin")
    BigDecimal calculateMontantTVAPeriode(@Param("dateDebut") LocalDateTime dateDebut,
                                          @Param("dateFin") LocalDateTime dateFin);
}