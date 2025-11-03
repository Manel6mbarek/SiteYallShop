package com.facturation.facture.service;

import com.facturation.facture.dto.LigneCommandeDTO;
import com.facturation.facture.config.ResourceNotFoundException;
import com.facturation.facture.model.Commande;
import com.facturation.facture.model.LigneCommande;
import com.facturation.facture.model.Produit;
import com.facturation.facture.repository.CommandeRepository;
import com.facturation.facture.repository.LigneCommandeRepository;
import com.facturation.facture.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LigneCommandeService {

    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private ProduitRepository produitRepository;

    /**
     * Vérifie si la commande appartient bien au client
     */
    public boolean commandeAppartientAuClient(Long commandeId, Long clientId) {
        return commandeRepository.findById(commandeId)
                .map(c -> c.getClient().getId().equals(clientId))
                .orElse(false);
    }

    /**
     * Récupère l’ID de la commande d’une ligne
     */
    public Long getCommandeIdByLigne(Long ligneId) {
        return ligneCommandeRepository.findById(ligneId)
                .map(l -> l.getCommande().getId())
                .orElseThrow(() -> new RuntimeException("Ligne de commande non trouvée : " + ligneId));
    }

    /**
     * Créer une nouvelle ligne de commande
     */
    public LigneCommandeDTO creerLigneCommande(LigneCommandeDTO ligneCommandeDTO) {
        // Vérifier que la commande existe
        Commande commande = commandeRepository.findById(ligneCommandeDTO.getCommandeId())
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'ID : " + ligneCommandeDTO.getCommandeId()));

        // Vérifier que le produit existe
        Produit produit = produitRepository.findById(ligneCommandeDTO.getProduit().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID : " + ligneCommandeDTO.getProduit().getId()));

        // Créer la ligne de commande
        LigneCommande ligneCommande = new LigneCommande(
                ligneCommandeDTO.getQuantite(),
                ligneCommandeDTO.getPrixUnitaire(),
                commande,
                produit
        );

        LigneCommande savedLigne = ligneCommandeRepository.save(ligneCommande);
        return LigneCommandeDTO.fromEntity(savedLigne);
    }

    /**
     * Obtenir toutes les lignes de commande
     */
    @Transactional(readOnly = true)
    public List<LigneCommandeDTO> obtenirToutesLesLignesCommande() {
        return ligneCommandeRepository.findAll().stream()
                .map(LigneCommandeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir une ligne de commande par ID
     */
    @Transactional(readOnly = true)
    public LigneCommandeDTO obtenirLigneCommandeParId(Long id) {
        LigneCommande ligneCommande = ligneCommandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ligne de commande non trouvée avec l'ID : " + id));
        return LigneCommandeDTO.fromEntity(ligneCommande);
    }

    /**
     * Obtenir les lignes de commande par ID de commande
     */
    @Transactional(readOnly = true)
    public List<LigneCommandeDTO> obtenirLignesCommandeParCommandeId(Long commandeId) {
        return ligneCommandeRepository.findByCommandeIdWithProduit(commandeId).stream()
                .map(LigneCommandeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les lignes de commande par ID de produit
     */
    @Transactional(readOnly = true)
    public List<LigneCommandeDTO> obtenirLignesCommandeParProduitId(Long produitId) {
        return ligneCommandeRepository.findByProduitId(produitId).stream()
                .map(LigneCommandeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Mettre à jour une ligne de commande
     */
    public LigneCommandeDTO mettreAJourLigneCommande(Long id, LigneCommandeDTO ligneCommandeDTO) {
        LigneCommande ligneExistante = ligneCommandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ligne de commande non trouvée avec l'ID : " + id));

        // Mettre à jour les champs modifiables
        ligneExistante.setQuantite(ligneCommandeDTO.getQuantite());
        ligneExistante.setPrixUnitaire(ligneCommandeDTO.getPrixUnitaire());

        // Recalculer le sous-total
        ligneExistante.setSousTotal(
                ligneCommandeDTO.getPrixUnitaire().multiply(BigDecimal.valueOf(ligneCommandeDTO.getQuantite()))
        );

        // Si le produit a changé
        if (ligneCommandeDTO.getProduit() != null &&
                !ligneExistante.getProduit().getId().equals(ligneCommandeDTO.getProduit().getId())) {
            Produit nouveauProduit = produitRepository.findById(ligneCommandeDTO.getProduit().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID : " + ligneCommandeDTO.getProduit().getId()));
            ligneExistante.setProduit(nouveauProduit);
            ligneExistante.setNomProduit(nouveauProduit.getNom());
        }

        LigneCommande ligneUpdated = ligneCommandeRepository.save(ligneExistante);
        return LigneCommandeDTO.fromEntity(ligneUpdated);
    }

    /**
     * Supprimer une ligne de commande
     */
    public void supprimerLigneCommande(Long id) {
        if (!ligneCommandeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ligne de commande non trouvée avec l'ID : " + id);
        }
        ligneCommandeRepository.deleteById(id);
    }

    /**
     * Supprimer toutes les lignes d'une commande
     */
    public void supprimerLignesCommandeParCommandeId(Long commandeId) {
        ligneCommandeRepository.deleteByCommandeId(commandeId);
    }

    /**
     * Calculer le total d'une commande
     */
    @Transactional(readOnly = true)
    public BigDecimal calculerTotalCommande(Long commandeId) {
        BigDecimal total = ligneCommandeRepository.calculateTotalCommande(commandeId);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Compter le nombre d'articles dans une commande
     */
    @Transactional(readOnly = true)
    public Long compterArticlesInCommande(Long commandeId) {
        return ligneCommandeRepository.countArticlesInCommande(commandeId);
    }

    /**
     * Calculer la quantité totale d'articles dans une commande
     */
    @Transactional(readOnly = true)
    public Long calculerQuantiteTotaleInCommande(Long commandeId) {
        return ligneCommandeRepository.sumQuantiteInCommande(commandeId);
    }

    /**
     * Obtenir les statistiques de vente d'un produit
     */
    @Transactional(readOnly = true)
    public Object[] obtenirStatistiquesProduit(Long produitId) {
        return ligneCommandeRepository.getStatistiquesProduit(produitId);
    }

    /**
     * Obtenir le nombre de commandes contenant un produit
     */
    @Transactional(readOnly = true)
    public Long compterCommandesAvecProduit(Long produitId) {
        return ligneCommandeRepository.countCommandesWithProduit(produitId);
    }

    /**
     * Calculer le chiffre d'affaires d'un produit
     */
    @Transactional(readOnly = true)
    public BigDecimal calculerChiffreAffairesProduit(Long produitId) {
        BigDecimal ca = ligneCommandeRepository.calculateChiffreAffairesProduit(produitId);
        return ca != null ? ca : BigDecimal.ZERO;
    }

    /**
     * Rechercher des lignes de commande par nom de produit
     */
    @Transactional(readOnly = true)
    public List<LigneCommandeDTO> rechercherParNomProduit(String nomProduit) {
        return ligneCommandeRepository.findByNomProduitContainingIgnoreCase(nomProduit).stream()
                .map(LigneCommandeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les lignes de commande par tranche de prix
     */
    @Transactional(readOnly = true)
    public List<LigneCommandeDTO> obtenirLignesParTranchePrix(BigDecimal prixMin, BigDecimal prixMax) {
        return ligneCommandeRepository.findByPrixUnitaireBetween(prixMin, prixMax).stream()
                .map(LigneCommandeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les produits les plus vendus
     */
    @Transactional(readOnly = true)
    public List<Object[]> obtenirProduitsLesPlusVendus() {
        return ligneCommandeRepository.findProduitsLesPlusVendus();
    }

    /**
     * Obtenir les produits par chiffre d'affaires
     */
    @Transactional(readOnly = true)
    public List<Object[]> obtenirProduitsParChiffreAffaires() {
        return ligneCommandeRepository.findProduitsParChiffreAffaires();
    }
}