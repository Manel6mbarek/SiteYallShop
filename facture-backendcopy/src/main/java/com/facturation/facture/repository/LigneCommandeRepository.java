package com.facturation.facture.repository;

import com.facturation.facture.model.LigneCommande;
import com.facturation.facture.model.Commande;
import com.facturation.facture.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {

    // Méthodes de recherche automatiques Spring Data JPA

    /**
     * Recherche des lignes de commande par commande
     */
    List<LigneCommande> findByCommande(Commande commande);

    /**
     * Recherche des lignes de commande par ID de commande
     */
    List<LigneCommande> findByCommandeId(Long commandeId);

    /**
     * Recherche des lignes de commande par produit
     */
    List<LigneCommande> findByProduit(Produit produit);

    /**
     * Recherche des lignes de commande par ID de produit
     */
    List<LigneCommande> findByProduitId(Long produitId);

    /**
     * Recherche une ligne de commande spécifique par commande et produit
     */
    Optional<LigneCommande> findByCommandeAndProduit(Commande commande, Produit produit);

    /**
     * Recherche des lignes de commande par quantité minimum
     */
    List<LigneCommande> findByQuantiteGreaterThanEqual(Integer quantiteMinimum);

    /**
     * Recherche par nom de produit
     */
    List<LigneCommande> findByNomProduitContainingIgnoreCase(String nomProduit);

    // Requêtes JPQL personnalisées

    /**
     * Obtenir les lignes de commande avec les détails du produit
     */
    @Query("SELECT lc FROM LigneCommande lc " +
            "LEFT JOIN FETCH lc.produit " +
            "WHERE lc.commande.id = :commandeId")
    List<LigneCommande> findByCommandeIdWithProduit(@Param("commandeId") Long commandeId);

    /**
     * Calculer le total d'une commande
     */
    @Query("SELECT SUM(lc.sousTotal) FROM LigneCommande lc " +
            "WHERE lc.commande.id = :commandeId")
    BigDecimal calculateTotalCommande(@Param("commandeId") Long commandeId);

    /**
     * Compter le nombre d'articles dans une commande
     */
    @Query("SELECT COUNT(lc) FROM LigneCommande lc WHERE lc.commande.id = :commandeId")
    Long countArticlesInCommande(@Param("commandeId") Long commandeId);

    /**
     * Calculer la quantité totale d'articles dans une commande
     */
    @Query("SELECT SUM(lc.quantite) FROM LigneCommande lc WHERE lc.commande.id = :commandeId")
    Long sumQuantiteInCommande(@Param("commandeId") Long commandeId);

    // Statistiques sur les produits

    /**
     * Obtenir les produits les plus vendus (par quantité)
     */
    @Query("SELECT lc.produit, SUM(lc.quantite) as totalQuantite FROM LigneCommande lc " +
            "GROUP BY lc.produit ORDER BY totalQuantite DESC")
    List<Object[]> findProduitsLesPlusVendus();

    /**
     * Obtenir les produits générant le plus de chiffre d'affaires
     */
    @Query("SELECT lc.produit, SUM(lc.sousTotal) as chiffreAffaires " +
            "FROM LigneCommande lc " +
            "JOIN lc.commande c WHERE c.statut = 'PAYEE' " +
            "GROUP BY lc.produit ORDER BY chiffreAffaires DESC")
    List<Object[]> findProduitsParChiffreAffaires();

    /**
     * Obtenir les statistiques de vente d'un produit
     */
    @Query("SELECT COUNT(lc), SUM(lc.quantite), SUM(lc.sousTotal) " +
            "FROM LigneCommande lc " +
            "WHERE lc.produit.id = :produitId")
    Object[] getStatistiquesProduit(@Param("produitId") Long produitId);

    /**
     * Obtenir le nombre de fois qu'un produit a été commandé
     */
    @Query("SELECT COUNT(DISTINCT lc.commande) FROM LigneCommande lc " +
            "WHERE lc.produit.id = :produitId")
    Long countCommandesWithProduit(@Param("produitId") Long produitId);

    /**
     * Calculer le chiffre d'affaires total d'un produit
     */
    @Query("SELECT SUM(lc.sousTotal) FROM LigneCommande lc " +
            "WHERE lc.produit.id = :produitId")
    BigDecimal calculateChiffreAffairesProduit(@Param("produitId") Long produitId);

    /**
     * Obtenir les lignes de commande par tranche de prix
     */
    @Query("SELECT lc FROM LigneCommande lc WHERE lc.prixUnitaire BETWEEN :prixMin AND :prixMax")
    List<LigneCommande> findByPrixUnitaireBetween(@Param("prixMin") BigDecimal prixMin,
                                                  @Param("prixMax") BigDecimal prixMax);

    /**
     * Supprimer les lignes de commande d'une commande spécifique
     */
    void deleteByCommandeId(Long commandeId);
}