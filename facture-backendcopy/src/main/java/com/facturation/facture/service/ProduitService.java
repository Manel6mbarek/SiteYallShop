package com.facturation.facture.service;

import com.facturation.facture.model.Produit;
import com.facturation.facture.model.Categorie;
import com.facturation.facture.repository.ProduitRepository;
import com.facturation.facture.repository.CategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final CategorieRepository categorieRepository;

    @Autowired
    public ProduitService(ProduitRepository produitRepository, CategorieRepository categorieRepository) {
        this.produitRepository = produitRepository;
        this.categorieRepository = categorieRepository;
    }

    /**
     * Sauvegarder un nouveau produit
     */
    public Produit sauvegarderProduit(Produit produit, Long categorieId) {
        // Validation avant sauvegarde
        validerProduit(produit);

        // Vérifier si le nom existe déjà
        if (produitRepository.existsByNomIgnoreCase(produit.getNom())) {
            throw new RuntimeException("Un produit avec ce nom existe déjà : " + produit.getNom());
        }

        // Assigner la catégorie
        Optional<Categorie> categorie = categorieRepository.findById(categorieId);
        if (categorie.isEmpty()) {
            throw new RuntimeException("Catégorie non trouvée avec l'ID : " + categorieId);
        }
        produit.setCategorie(categorie.get());

        produit.setDateCreation(LocalDateTime.now());
        produit.setDateModification(LocalDateTime.now());
        return produitRepository.save(produit);
    }

    /**
     * Mettre à jour un produit existant
     */
    public Produit mettreAJourProduit(Long idProduit, Produit produitMisAJour, Long categorieId) {
        Optional<Produit> produitExistant = produitRepository.findById(idProduit);
        if (produitExistant.isEmpty()) {
            throw new RuntimeException("Produit non trouvé avec l'ID : " + idProduit);
        }

        Produit produit = produitExistant.get();

        // Vérifier si le nom a changé et s'il n'existe pas déjà
        if (!produit.getNom().equalsIgnoreCase(produitMisAJour.getNom()) &&
                produitRepository.existsByNomIgnoreCase(produitMisAJour.getNom())) {
            throw new RuntimeException("Un autre produit utilise déjà ce nom : " + produitMisAJour.getNom());
        }

        // Assigner la nouvelle catégorie si fournie
        if (categorieId != null) {
            Optional<Categorie> categorie = categorieRepository.findById(categorieId);
            if (categorie.isEmpty()) {
                throw new RuntimeException("Catégorie non trouvée avec l'ID : " + categorieId);
            }
            produit.setCategorie(categorie.get());
        }

        // Mettre à jour les champs
        produit.setNom(produitMisAJour.getNom());
        produit.setDescription(produitMisAJour.getDescription());
        produit.setPrix(produitMisAJour.getPrix());
        produit.setQuantiteStock(produitMisAJour.getQuantiteStock());
        produit.setSeuilAlerte(produitMisAJour.getSeuilAlerte());
        produit.setDisponible(produitMisAJour.getDisponible());
        produit.setImagePath(produitMisAJour.getImagePath());
        produit.setDateModification(LocalDateTime.now());

        return produitRepository.save(produit);
    }

    /**
     * Obtenir un produit par son ID
     */
    @Transactional(readOnly = true)
    public Optional<Produit> obtenirProduitParId(Long idProduit) {
        return produitRepository.findById(idProduit);
    }

    /**
     * Obtenir tous les produits
     */
    @Transactional(readOnly = true)
    public List<Produit> obtenirTousLesProduits() {
        return produitRepository.findAllWithCategoriesOrderByCategorieAndNom();
    }

    /**
     * Obtenir tous les produits disponibles
     */
    @Transactional(readOnly = true)
    public List<Produit> obtenirProduitsDisponibles() {
        return produitRepository.findAllDisponiblesOrderByNom();
    }

    /**
     * Rechercher des produits
     */
    @Transactional(readOnly = true)
    public List<Produit> rechercherProduits(String termRecherche) {
        if (termRecherche == null || termRecherche.trim().isEmpty()) {
            return obtenirTousLesProduits();
        }
        return produitRepository.rechercherProduits(termRecherche.trim());
    }

    /**
     * Rechercher des produits avec stock disponible
     */
    @Transactional(readOnly = true)
    public List<Produit> rechercherProduitsAvecStock(String termRecherche) {
        if (termRecherche == null || termRecherche.trim().isEmpty()) {
            return produitRepository.findByCategorieIdWithStock(null);
        }
        return produitRepository.rechercherProduitsAvecStock(termRecherche.trim());
    }

    /**
     * Obtenir des produits par fourchette de prix
     */
    @Transactional(readOnly = true)
    public List<Produit> obtenirProduitsParFourchettePrix(BigDecimal prixMin, BigDecimal prixMax) {
        return produitRepository.findByPrixBetween(prixMin, prixMax);
    }

    /**
     * Obtenir des produits par catégorie
     */
    @Transactional(readOnly = true)
    public List<Produit> obtenirProduitsParCategorie(Long categorieId) {
        return produitRepository.findByCategorieId(categorieId);
    }

    /**
     * Obtenir des produits disponibles par catégorie
     */
    @Transactional(readOnly = true)
    public List<Produit> obtenirProduitsDisponiblesParCategorie(Long categorieId) {
        return produitRepository.findByCategorieIdAndDisponibleTrue(categorieId);
    }

    /**
     * Obtenir des produits avec stock faible
     */
    @Transactional(readOnly = true)
    public List<Produit> obtenirProduitsStockFaible() {
        return produitRepository.findProduitsStockFaible();
    }

    /**
     * Obtenir des produits en rupture de stock
     */
    @Transactional(readOnly = true)
    public List<Produit> obtenirProduitsRuptureStock() {
        return produitRepository.findByQuantiteStockLessThanEqual(0);
    }

    /**
     * Supprimer un produit
     */
    public void supprimerProduit(Long idProduit) {
        Optional<Produit> produit = produitRepository.findById(idProduit);
        if (produit.isEmpty()) {
            throw new RuntimeException("Produit non trouvé avec l'ID : " + idProduit);
        }

        // Vérifier si le produit est utilisé dans des commandes
        if (produit.get().getLignesCommande() != null && !produit.get().getLignesCommande().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer le produit car il est utilisé dans des commandes");
        }

        produitRepository.deleteById(idProduit);
    }

    /**
     * Marquer un produit comme non disponible
     */
    public Produit marquerIndisponible(Long idProduit) {
        Optional<Produit> produitOpt = produitRepository.findById(idProduit);
        if (produitOpt.isEmpty()) {
            throw new RuntimeException("Produit non trouvé avec l'ID : " + idProduit);
        }

        Produit produit = produitOpt.get();
        produit.setDisponible(false);
        produit.setDateModification(LocalDateTime.now());
        return produitRepository.save(produit);
    }

    /**
     * Marquer un produit comme disponible
     */
    public Produit marquerDisponible(Long idProduit) {
        Optional<Produit> produitOpt = produitRepository.findById(idProduit);
        if (produitOpt.isEmpty()) {
            throw new RuntimeException("Produit non trouvé avec l'ID : " + idProduit);
        }

        Produit produit = produitOpt.get();
        produit.setDisponible(true);
        produit.setDateModification(LocalDateTime.now());
        return produitRepository.save(produit);
    }

    /**
     * Ajuster le stock d'un produit
     */
    public Produit ajusterStock(Long idProduit, Integer nouvelleQuantite) {
        Optional<Produit> produitOpt = produitRepository.findById(idProduit);
        if (produitOpt.isEmpty()) {
            throw new RuntimeException("Produit non trouvé avec l'ID : " + idProduit);
        }

        if (nouvelleQuantite < 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être négative");
        }

        Produit produit = produitOpt.get();
        produit.setQuantiteStock(nouvelleQuantite);
        produit.setDateModification(LocalDateTime.now());
        return produitRepository.save(produit);
    }

    /**
     * Réduire le stock d'un produit (pour une vente)
     */
    public Produit reduireStock(Long idProduit, Integer quantiteVendue) {
        Optional<Produit> produitOpt = produitRepository.findById(idProduit);
        if (produitOpt.isEmpty()) {
            throw new RuntimeException("Produit non trouvé avec l'ID : " + idProduit);
        }

        Produit produit = produitOpt.get();

        if (produit.getQuantiteStock() < quantiteVendue) {
            throw new RuntimeException("Stock insuffisant. Stock actuel : " + produit.getQuantiteStock());
        }

        produit.setQuantiteStock(produit.getQuantiteStock() - quantiteVendue);
        produit.setDateModification(LocalDateTime.now());
        return produitRepository.save(produit);
    }

    /**
     * Augmenter le stock d'un produit (réapprovisionnement)
     */
    public Produit augmenterStock(Long idProduit, Integer quantiteAjoutee) {
        Optional<Produit> produitOpt = produitRepository.findById(idProduit);
        if (produitOpt.isEmpty()) {
            throw new RuntimeException("Produit non trouvé avec l'ID : " + idProduit);
        }

        if (quantiteAjoutee <= 0) {
            throw new IllegalArgumentException("La quantité à ajouter doit être positive");
        }

        Produit produit = produitOpt.get();
        produit.setQuantiteStock(produit.getQuantiteStock() + quantiteAjoutee);
        produit.setDateModification(LocalDateTime.now());
        return produitRepository.save(produit);
    }

    /**
     * Compter les produits disponibles
     */
    @Transactional(readOnly = true)
    public Long compterProduitsDisponibles() {
        return produitRepository.countProduitsDisponibles();
    }

    /**
     * Compter les produits avec stock faible
     */
    @Transactional(readOnly = true)
    public Long compterProduitsStockFaible() {
        return produitRepository.countProduitsStockFaible();
    }

    /**
     * Compter les produits en rupture de stock
     */
    @Transactional(readOnly = true)
    public Long compterProduitsRuptureStock() {
        return produitRepository.countProduitsRuptureStock();
    }

    /**
     * Obtenir le prix moyen des produits
     */
    @Transactional(readOnly = true)
    public BigDecimal obtenirPrixMoyen() {
        return produitRepository.getPrixMoyenProduits();
    }

    /**
     * Obtenir la valeur totale du stock
     */
    @Transactional(readOnly = true)
    public BigDecimal obtenirValeurTotaleStock() {
        return produitRepository.getValeurTotaleStock();
    }

    /**
     * Obtenir les produits récents
     */
    @Transactional(readOnly = true)
    public List<Produit> obtenirProduitsRecents(int nombreJours) {
        LocalDateTime dateDebut = LocalDateTime.now().minusDays(nombreJours);
        return produitRepository.findProduitsRecents(dateDebut);
    }

    /**
     * Créer un produit avec validation
     */
    public Produit creerProduit(String nom, String description, BigDecimal prix,
                                Integer quantiteStock, Integer seuilAlerte, Long categorieId) {
        Produit produit = new Produit();
        produit.setNom(nom);
        produit.setDescription(description);
        produit.setPrix(prix);
        produit.setQuantiteStock(quantiteStock != null ? quantiteStock : 0);
        produit.setSeuilAlerte(seuilAlerte != null ? seuilAlerte : 0);

        validerProduit(produit);
        return sauvegarderProduit(produit, categorieId);
    }

    /**
     * Valider les données d'un produit
     */
    private void validerProduit(Produit produit) {
        if (produit.getNom() == null || produit.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit est obligatoire");
        }
        if (produit.getPrix() == null) {
            throw new IllegalArgumentException("Le prix du produit est obligatoire");
        }
        if (produit.getPrix().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le prix du produit doit être supérieur à 0");
        }
        if (produit.getNom().length() > 100) {
            throw new IllegalArgumentException("Le nom du produit ne peut pas dépasser 100 caractères");
        }
        if (produit.getDescription() != null && produit.getDescription().length() > 500) {
            throw new IllegalArgumentException("La description ne peut pas dépasser 500 caractères");
        }
        if (produit.getQuantiteStock() != null && produit.getQuantiteStock() < 0) {
            throw new IllegalArgumentException("La quantité en stock ne peut pas être négative");
        }
        if (produit.getSeuilAlerte() != null && produit.getSeuilAlerte() < 0) {
            throw new IllegalArgumentException("Le seuil d'alerte ne peut pas être négatif");
        }
    }
}