// Ajoutez ces méthodes dans votre interface FactureRepository

package com.facturation.facture.repository;

import com.facturation.facture.model.Facture;
import com.facturation.facture.model.enums.StatutFacture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {

    // Recherches par statut
    List<Facture> findByStatut(StatutFacture statut);
    List<Facture> findByStatutOrderByDateCreationAsc(StatutFacture statut);
    List<Facture> findByStatutOrderByDateCreationDesc(StatutFacture statut);

    // Recherches par date
    List<Facture> findByDateFactureBetween(LocalDateTime dateDebut, LocalDateTime dateFin);
    List<Facture> findByDateCreationBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    // Recherches combinées statut + date
    List<Facture> findByStatutAndDateFactureBetween(
            StatutFacture statut,
            LocalDateTime dateDebut,
            LocalDateTime dateFin
    );

    List<Facture> findByStatutAndDateCreationBetween(
            StatutFacture statut,
            LocalDateTime dateDebut,
            LocalDateTime dateFin
    );

    // Recherches par client
    List<Facture> findByCommande_Client_Id(Long clientId);
    List<Facture> findByCommande_Client_IdOrderByDateFactureDesc(Long clientId);

    // Recherches combinées client + statut + date
    List<Facture> findByStatutAndDateFactureBetweenAndCommande_Client_Id(
            StatutFacture statut,
            LocalDateTime dateDebut,
            LocalDateTime dateFin,
            Long clientId
    );

    // Recherches par numéro
    Facture findByNumeroFacture(String numeroFacture);
    List<Facture> findByNumeroFactureContaining(String numeroPartiel);

    // Comptage par statut
    Long countByStatut(StatutFacture statut);

    @Query("SELECT COUNT(f) FROM Facture f WHERE f.statut = :statut AND f.dateFacture BETWEEN :dateDebut AND :dateFin")
    Long countByStatutAndDateFactureBetween(
            @Param("statut") StatutFacture statut,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin
    );

    // Calculs de montants
    @Query("SELECT SUM(f.montantTTC) FROM Facture f WHERE f.statut = :statut")
    BigDecimal sumMontantTTCByStatut(@Param("statut") StatutFacture statut);

    @Query("SELECT SUM(f.montantTTC) FROM Facture f WHERE f.statut = :statut AND f.dateFacture BETWEEN :dateDebut AND :dateFin")
    BigDecimal sumMontantTTCByStatutAndDateFactureBetween(
            @Param("statut") StatutFacture statut,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin
    );

    @Query("SELECT SUM(f.montantHT) FROM Facture f WHERE f.statut = :statut AND f.dateFacture BETWEEN :dateDebut AND :dateFin")
    BigDecimal sumMontantHTByStatutAndDateFactureBetween(
            @Param("statut") StatutFacture statut,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin
    );

    // Statistiques avancées
    @Query("SELECT f.statut, COUNT(f) FROM Facture f GROUP BY f.statut")
    List<Object[]> countFacturesByStatut();

    @Query("SELECT f.modePaiement, COUNT(f) FROM Facture f WHERE f.statut = :statut GROUP BY f.modePaiement")
    List<Object[]> countFacturesByModePaiementAndStatut(@Param("statut") StatutFacture statut);

    // Recherches des factures récentes
    @Query("SELECT f FROM Facture f WHERE f.statut = :statut ORDER BY f.dateCreation DESC")
    List<Facture> findTopByStatutOrderByDateCreationDesc(@Param("statut") StatutFacture statut);

    // Factures du jour
    @Query("SELECT f FROM Facture f WHERE DATE(f.dateFacture) = CURRENT_DATE")
    List<Facture> findFacturesDuJour();

    // Factures de la semaine
    @Query("SELECT f FROM Facture f WHERE f.dateFacture >= :debutSemaine AND f.dateFacture <= :finSemaine")
    List<Facture> findFacturesDeLaSemaine(
            @Param("debutSemaine") LocalDateTime debutSemaine,
            @Param("finSemaine") LocalDateTime finSemaine
    );

    // Factures en retard de paiement (à personnaliser selon vos règles métier)
    @Query("SELECT f FROM Facture f WHERE f.statut = :statut AND f.dateFacture < :dateLimite")
    List<Facture> findFacturesEnRetard(
            @Param("statut") StatutFacture statut,
            @Param("dateLimite") LocalDateTime dateLimite
    );

    // Recherche par commande
    Facture findByCommande_Id(Long commandeId);
    List<Facture> findByCommande_NumeroCommande(String numeroCommande);

    // Recherches avec jointures optimisées
    @Query("SELECT f FROM Facture f LEFT JOIN FETCH f.commande c LEFT JOIN FETCH c.client WHERE f.id = :id")
    Facture findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT f FROM Facture f LEFT JOIN FETCH f.commande c LEFT JOIN FETCH c.lignesCommande WHERE f.id = :id")
    Facture findByIdWithCommandeAndLignes(@Param("id") Long id);
}