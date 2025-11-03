package com.facturation.facture.repository;

import com.facturation.facture.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    List<Produit> findByNomContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String nom, String description);
    // Méthodes de recherche automatiques Spring Data JPA

    /**
     * Recherche un produit par nom
     */
    Optional<Produit> findByNom(String nom);

    /**
     * Recherche un produit par nom (ignore la casse)
     */
    Optional<Produit> findByNomIgnoreCase(String nom);

    /**
     * Vérifier si un nom de produit existe déjà
     */
    boolean existsByNomIgnoreCase(String nom);

    /**
     * Recherche des produits par nom contenant une chaîne
     */
    List<Produit> findByNomContainingIgnoreCase(String nom);

    /**
     * Recherche des produits disponibles
     */
    List<Produit> findByDisponibleTrue();

    /**
     * Recherche des produits non disponibles
     */
    List<Produit> findByDisponibleFalse();

    /**
     * Recherche des produits par prix minimum
     */
    List<Produit> findByPrixGreaterThanEqual(BigDecimal prixMinimum);

    /**
     * Recherche des produits par prix maximum
     */
    List<Produit> findByPrixLessThanEqual(BigDecimal prixMaximum);

    /**
     * Recherche des produits par fourchette de prix
     */
    List<Produit> findByPrixBetween(BigDecimal prixMin, BigDecimal prixMax);

    /**
     * Recherche des produits par catégorie
     */
    List<Produit> findByCategorieId(Long categorieId);

    /**
     * Recherche des produits disponibles par catégorie
     */
    List<Produit> findByCategorieIdAndDisponibleTrue(Long categorieId);

    /**
     * Recherche des produits avec stock faible
     */
    @Query("SELECT p FROM Produit p WHERE p.quantiteStock <= p.seuilAlerte AND p.disponible = true")
    List<Produit> findProduitsStockFaible();

    /**
     * Recherche des produits en rupture de stock
     */
    List<Produit> findByQuantiteStockLessThanEqual(Integer quantite);

    // Requêtes JPQL personnalisées

    /**
     * Recherche globale de produits
     */
    @Query("SELECT p FROM Produit p WHERE " +
            "LOWER(p.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Produit> rechercherProduits(@Param("searchTerm") String searchTerm);

    /**
     * Obtenir tous les produits disponibles triés par nom
     */
    @Query("SELECT p FROM Produit p WHERE p.disponible = true ORDER BY p.nom ASC")
    List<Produit> findAllDisponiblesOrderByNom();

    /**
     * Obtenir tous les produits avec leurs catégories triés par catégorie puis par nom
     */
    @Query("SELECT p FROM Produit p JOIN FETCH p.categorie ORDER BY p.categorie.nom ASC, p.nom ASC")
    List<Produit> findAllWithCategoriesOrderByCategorieAndNom();

    /**
     * Compter le nombre total de produits
     */
    @Query("SELECT COUNT(p) FROM Produit p")
    Long countTotalProduits();

    /**
     * Compter les produits disponibles
     */
    @Query("SELECT COUNT(p) FROM Produit p WHERE p.disponible = true")
    Long countProduitsDisponibles();

    /**
     * Compter les produits avec stock faible
     */
    @Query("SELECT COUNT(p) FROM Produit p WHERE p.quantiteStock <= p.seuilAlerte AND p.disponible = true")
    Long countProduitsStockFaible();

    /**
     * Compter les produits en rupture de stock
     */
    @Query("SELECT COUNT(p) FROM Produit p WHERE p.quantiteStock = 0 AND p.disponible = true")
    Long countProduitsRuptureStock();

    /**
     * Obtenir le prix moyen des produits
     */
    @Query("SELECT AVG(p.prix) FROM Produit p WHERE p.disponible = true")
    BigDecimal getPrixMoyenProduits();

    /**
     * Obtenir la valeur totale du stock
     */
    @Query("SELECT SUM(p.prix * p.quantiteStock) FROM Produit p WHERE p.disponible = true")
    BigDecimal getValeurTotaleStock();

    /**
     * Obtenir les produits les plus chers
     */
    @Query("SELECT p FROM Produit p WHERE p.disponible = true ORDER BY p.prix DESC")
    List<Produit> findProduitsLesPlusCher();

    /**
     * Obtenir les produits les moins chers
     */
    @Query("SELECT p FROM Produit p WHERE p.disponible = true ORDER BY p.prix ASC")
    List<Produit> findProduitsLeMoinsCher();

    /**
     * Obtenir les produits par catégorie avec stock disponible
     */
    @Query("SELECT p FROM Produit p WHERE p.categorie.id = :categorieId AND p.quantiteStock > 0 AND p.disponible = true")
    List<Produit> findByCategorieIdWithStock(@Param("categorieId") Long categorieId);

    /**
     * Recherche de produits par nom avec stock disponible
     */
    @Query("SELECT p FROM Produit p WHERE " +
            "LOWER(p.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND " +
            "p.quantiteStock > 0 AND p.disponible = true")
    List<Produit> rechercherProduitsAvecStock(@Param("searchTerm") String searchTerm);

    /**
     * Obtenir les produits récemment ajoutés
     */
    @Query("SELECT p FROM Produit p WHERE p.dateCreation >= :dateDebut ORDER BY p.dateCreation DESC")
    List<Produit> findProduitsRecents(@Param("dateDebut") LocalDateTime dateDebut);

    /**
     * Obtenir les statistiques par catégorie
     */
    @Query("SELECT p.categorie.nom, COUNT(p), AVG(p.prix), SUM(p.quantiteStock) " +
            "FROM Produit p WHERE p.disponible = true GROUP BY p.categorie.id, p.categorie.nom")
    List<Object[]> getStatistiquesParCategorie();
}