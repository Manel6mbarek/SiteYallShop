package com.facturation.facture.service;

import com.facturation.facture.dto.CommandeDTO;
import com.facturation.facture.dto.LigneCommandeDTO;
import com.facturation.facture.model.*;
import com.facturation.facture.model.enums.StatutCommande;
import com.facturation.facture.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Ajoutez ces imports en haut du fichier
import com.facturation.facture.model.Facture;
import com.facturation.facture.model.enums.ModePaiement;
import com.facturation.facture.model.enums.StatutFacture;
import com.facturation.facture.repository.FactureRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@Transactional
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final UserRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final FactureRepository factureRepository;

    @Autowired
    public CommandeService(CommandeRepository commandeRepository,
                           UserRepository clientRepository,
                           ProduitRepository produitRepository,
                           LigneCommandeRepository ligneCommandeRepository,
                           FactureRepository factureRepository) {
        this.commandeRepository = commandeRepository;
        this.clientRepository = clientRepository;
        this.produitRepository = produitRepository;
        this.ligneCommandeRepository = ligneCommandeRepository;
        this.factureRepository = factureRepository;
    }

    /**
     * Créer une nouvelle commande avec produits et facture automatique
     */
    public Commande creerCommandeAvecProduits(CommandeDTO commandeDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            System.out.println("❌ Aucun utilisateur authentifié !");
        } else {
            System.out.println("✅ Utilisateur authentifié = " + authentication.getName());
        }

        // Récupérer l'utilisateur connecté depuis Spring Security
        String email = authentication.getName(); // ou getUsername()
        User client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + email));

        // Créer la commande pour ce client
        Commande commande = new Commande(client);
        commande.setNumeroCommande(genererNumeroCommande());
        commande.setStatut(StatutCommande.EN_ATTENTE);// Statut initial

        // Ajouter les produits envoyés dans le DTO
        if (commandeDTO.getLignesCommande() != null && !commandeDTO.getLignesCommande().isEmpty()) {
            for (LigneCommandeDTO ligneDTO : commandeDTO.getLignesCommande()) {
                if (ligneDTO.getProduit() == null || ligneDTO.getProduit().getId() == null) {
                    throw new RuntimeException("Produit manquant dans une ligne de commande");
                }

                Produit produit = produitRepository.findById(ligneDTO.getProduit().getId())
                        .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

                if (!produit.getDisponible()) {
                    throw new RuntimeException("Le produit " + produit.getNom() + " n'est pas disponible");
                }

                if (ligneDTO.getQuantite() == null || ligneDTO.getQuantite() <= 0) {
                    throw new RuntimeException("Quantité invalide pour le produit " + produit.getNom());
                }

                LigneCommande ligne = new LigneCommande();
                ligne.setCommande(commande);
                ligne.setProduit(produit);
                ligne.setQuantite(ligneDTO.getQuantite());
                ligne.setPrixUnitaire(produit.getPrix());
                ligne.calculerSousTotal();

                commande.ajouterLigneCommande(ligne);
            }
        } else {
            throw new RuntimeException("Une commande doit contenir au moins un produit");
        }

        // Calcul des totaux
        commande.calculerTotaux();

        // Sauvegarder la commande d'abord
        Commande commandeSauvegardee = commandeRepository.save(commande);

        // Créer automatiquement la facture avec tous les détails
        creerFactureComplete(commandeSauvegardee);

        return commandeSauvegardee;
    }

    /**
     * Créer une facture complète automatiquement pour une commande
     */
    private void creerFactureComplete(Commande commande) {
        Facture facture = new Facture();
        facture.setCommande(commande);
        facture.setModePaiement(ModePaiement.ESPECES); // Mode par défaut

        // Définir les dates
        LocalDateTime maintenant = LocalDateTime.now();
        facture.setDateCreation(maintenant);
        facture.setDateFacture(maintenant);
        facture.setDateModification(maintenant);

        // Générer le numéro de facture
        facture.setNumeroFacture(genererNumeroFacture());

        // Informations client
        if (commande.getClient() != null) {
            facture.setNomClient(commande.getClient().getNom());
            // Vous pouvez ajouter d'autres infos client si nécessaire
            // facture.setAdresseClient(commande.getClient().getAdresse());
            // facture.setTelephoneClient(commande.getClient().getTelephone());
        }

        // Statut de la commande
        facture.setStatutCommande(commande.getStatut() != null ? commande.getStatut().name() : "EN_ATTENTE");

        // Détails des produits commandés (description complète)
        StringBuilder descriptionProduits = new StringBuilder();
        if (commande.getLignesCommande() != null && !commande.getLignesCommande().isEmpty()) {
            descriptionProduits.append("Produits commandés:\n");
            for (LigneCommande ligne : commande.getLignesCommande()) {
                descriptionProduits.append("- ")
                        .append(ligne.getProduit().getNom())
                        .append(" x")
                        .append(ligne.getQuantite())
                        .append(" = ")
                        .append(ligne.getSousTotal())
                        .append(" DT\n");
            }
        }
        // Vous pouvez stocker cela dans un champ description si il existe
        // facture.setDescriptionProduits(descriptionProduits.toString());

        // Copier les montants depuis la commande
        facture.setMontantHT(commande.getTotalHT() != null ? commande.getTotalHT() : BigDecimal.ZERO);
        facture.setMontantTVA(commande.getMontantTVA() != null ? commande.getMontantTVA() : BigDecimal.ZERO);
        facture.setMontantTTC(commande.getTotalTTC() != null ? commande.getTotalTTC() : BigDecimal.ZERO);

        // Définir le statut de la facture selon le statut de la commande
        if (commande.getStatut() == StatutCommande.PAYEE) {
            facture.setStatut(StatutFacture.PAYEE);
            facture.setDatePaiement(maintenant);
        } else {
            facture.setStatut(StatutFacture.EN_ATTENTE);
        }

        // Sauvegarder la facture
        Facture factureSauvegardee = factureRepository.save(facture);

        // Lier la facture à la commande
        commande.setFacture(factureSauvegardee);
        commandeRepository.save(commande);
    }

    /**
     * Valider une commande et mettre à jour la facture
     */
    public Commande validerCommande(Long idCommande) {
        Optional<Commande> commandeOpt = commandeRepository.findByIdWithLignesCommande(idCommande);
        if (commandeOpt.isEmpty()) {
            throw new RuntimeException("Commande non trouvée avec l'ID : " + idCommande);
        }

        Commande commande = commandeOpt.get();

        // Vérifications avant validation
        if (commande.getLignesCommande() == null || commande.getLignesCommande().isEmpty()) {
            throw new RuntimeException("Impossible de valider une commande vide");
        }

        if (commande.getStatut() != StatutCommande.EN_ATTENTE) {
            throw new RuntimeException("Seules les commandes en attente peuvent être validées");
        }

        // Vérifier la disponibilité des produits
        for (LigneCommande ligne : commande.getLignesCommande()) {
            if (!ligne.getProduit().getDisponible()) {
                throw new RuntimeException("Le produit '" + ligne.getProduit().getNom() + "' n'est plus disponible");
            }
        }

        // Changer le statut vers VALIDEE (ou TRAITEE selon votre enum)
        commande.setStatut(StatutCommande.PAYEE); // Ou TRAITEE si vous avez ce statut
        commande.setDateModification(LocalDateTime.now());
        calculerTotauxCommande(commande);

        // Mettre à jour automatiquement la facture associée
        mettreAJourFactureAutomatiquement(commande);

        return commandeRepository.save(commande);
    }

    /**
     * Changer le statut d'une commande (pour l'admin)
     */
    public Commande changerStatutCommande(Long idCommande, StatutCommande nouveauStatut) {
        Optional<Commande> commandeOpt = commandeRepository.findById(idCommande);
        if (commandeOpt.isEmpty()) {
            throw new RuntimeException("Commande non trouvée avec l'ID : " + idCommande);
        }

        Commande commande = commandeOpt.get();
        StatutCommande ancienStatut = commande.getStatut();

        // Validation des transitions de statut
        if (!peutChangerStatut(ancienStatut, nouveauStatut)) {
            throw new RuntimeException("Transition de statut invalide : " + ancienStatut + " -> " + nouveauStatut);
        }

        commande.setStatut(nouveauStatut);
        commande.setDateModification(LocalDateTime.now());

        // Mettre à jour automatiquement la facture
        mettreAJourFactureAutomatiquement(commande);

        return commandeRepository.save(commande);
    }

    /**
     * Mettre à jour le mode de paiement d'une facture
     */
    public Commande changerModePaiement(Long idCommande, ModePaiement modePaiement) {
        Optional<Commande> commandeOpt = commandeRepository.findById(idCommande);
        if (commandeOpt.isEmpty()) {
            throw new RuntimeException("Commande non trouvée avec l'ID : " + idCommande);
        }

        Commande commande = commandeOpt.get();
        if (commande.getFacture() != null) {
            Facture facture = commande.getFacture();
            facture.setModePaiement(modePaiement);
            facture.setDateModification(LocalDateTime.now());
            factureRepository.save(facture);
        }

        return commande;
    }

    /**
     * Mettre à jour la facture automatiquement quand la commande change
     */
    private void mettreAJourFactureAutomatiquement(Commande commande) {
        if (commande.getFacture() != null) {
            Facture facture = commande.getFacture();

            // Mettre à jour les montants
            facture.setMontantHT(commande.getTotalHT());
            facture.setMontantTVA(commande.getMontantTVA());
            facture.setMontantTTC(commande.getTotalTTC());
            facture.setDateModification(LocalDateTime.now());

            // Mettre à jour le statut de la commande dans la facture
            facture.setStatutCommande(commande.getStatut().name());

            // Synchroniser les statuts
            switch (commande.getStatut()) {
                case PAYEE:
                    facture.setStatut(StatutFacture.PAYEE);
                    if (facture.getDatePaiement() == null) {
                        facture.setDatePaiement(LocalDateTime.now());
                    }
                    break;
                case ANNULEE:
                    facture.setStatut(StatutFacture.ANNULEE);
                    break;
                case LIVREE:
                    // La facture reste payée si elle l'était déjà
                    if (facture.getStatut() != StatutFacture.PAYEE) {
                        facture.setStatut(StatutFacture.EN_ATTENTE);
                    }
                    break;
                default:
                    if (facture.getStatut() != StatutFacture.PAYEE) {
                        facture.setStatut(StatutFacture.EN_ATTENTE);
                    }
                    break;
            }

            factureRepository.save(facture);
        }
    }

    /**
     * Vérifier si une transition de statut est valide
     */
    private boolean peutChangerStatut(StatutCommande ancienStatut, StatutCommande nouveauStatut) {
        if (ancienStatut == nouveauStatut) {
            return true;
        }

        switch (ancienStatut) {
            case EN_ATTENTE:
                return nouveauStatut == StatutCommande.PAYEE || nouveauStatut == StatutCommande.ANNULEE;
            case PAYEE:
                return nouveauStatut == StatutCommande.LIVREE || nouveauStatut == StatutCommande.ANNULEE;
            case LIVREE:
                return nouveauStatut == StatutCommande.ANNULEE; // Selon vos règles métier
            case ANNULEE:
                return false; // Une commande annulée ne peut plus changer de statut
            default:
                return false;
        }
    }

    /**
     * Générer un numéro de facture unique
     */
    private String genererNumeroFacture() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long countFactures = factureRepository.count();
        return String.format("FAC-%s-%04d", dateStr, countFactures + 1);
    }

    /**
     * Générer un numéro de commande unique
     */
    private String genererNumeroCommande() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Optional<String> lastNumero = commandeRepository.findLastNumeroCommande();

        int sequence = 1;
        if (lastNumero.isPresent()) {
            String last = lastNumero.get();
            if (last.startsWith("CMD-" + dateStr)) {
                String sequenceStr = last.substring(last.lastIndexOf("-") + 1);
                sequence = Integer.parseInt(sequenceStr) + 1;
            }
        }

        return String.format("CMD-%s-%04d", dateStr, sequence);
    }

    /**
     * Créer une nouvelle commande simple (garde pour compatibilité)
     */
    public Commande creerCommande(Long idClient) {
        Optional<User> clientOpt = clientRepository.findById(idClient);
        if (clientOpt.isEmpty()) {
            throw new RuntimeException("Client non trouvé avec l'ID : " + idClient);
        }

        Commande commande = new Commande();
        commande.setClient(clientOpt.get());
        commande.setNumeroCommande(genererNumeroCommande());
        commande.setDateCommande(LocalDateTime.now());
        commande.setDateModification(LocalDateTime.now());
        commande.setStatut(StatutCommande.EN_ATTENTE);
        commande.setSousTotal(BigDecimal.ZERO);
        commande.setTauxTVA(new BigDecimal("20.00"));
        commande.setMontantTVA(BigDecimal.ZERO);
        commande.setTotalHT(BigDecimal.ZERO);
        commande.setTotalTTC(BigDecimal.ZERO);

        Commande commandeSauvegardee = commandeRepository.save(commande);
        creerFactureComplete(commandeSauvegardee);

        return commandeSauvegardee;
    }

    // ... (garder toutes les autres méthodes existantes inchangées)

    /**
     * Ajouter un produit à une commande
     */
    public Commande ajouterProduitACommande(Long idCommande, Long idProduit, Integer quantite) {
        Optional<Commande> commandeOpt = commandeRepository.findById(idCommande);
        if (commandeOpt.isEmpty()) {
            throw new RuntimeException("Commande non trouvée avec l'ID : " + idCommande);
        }

        Optional<Produit> produitOpt = produitRepository.findById(idProduit);
        if (produitOpt.isEmpty()) {
            throw new RuntimeException("Produit non trouvé avec l'ID : " + idProduit);
        }

        Commande commande = commandeOpt.get();
        Produit produit = produitOpt.get();

        if (!peutEtreModifiee(commande)) {
            throw new RuntimeException("Cette commande ne peut plus être modifiée car son statut est : " + commande.getStatut());
        }

        if (!produit.getDisponible()) {
            throw new RuntimeException("Le produit '" + produit.getNom() + "' n'est plus disponible");
        }

        Optional<LigneCommande> ligneExistante = ligneCommandeRepository.findByCommandeAndProduit(commande, produit);

        if (ligneExistante.isPresent()) {
            LigneCommande ligne = ligneExistante.get();
            ligne.setQuantite(ligne.getQuantite() + quantite);
            ligneCommandeRepository.save(ligne);
        } else {
            LigneCommande nouvelleLigne = new LigneCommande();
            nouvelleLigne.setCommande(commande);
            nouvelleLigne.setProduit(produit);
            nouvelleLigne.setQuantite(quantite);
            nouvelleLigne.setPrixUnitaire(produit.getPrix());
            nouvelleLigne.calculerSousTotal();
            ligneCommandeRepository.save(nouvelleLigne);
        }

        calculerTotauxCommande(commande);
        Commande commandeModifiee = commandeRepository.save(commande);

        // Mettre à jour la facture automatiquement
        mettreAJourFactureAutomatiquement(commandeModifiee);

        return commandeModifiee;
    }

    private void calculerTotauxCommande(Commande commande) {
        BigDecimal sousTotal = BigDecimal.ZERO;

        if (commande.getLignesCommande() != null) {
            for (LigneCommande ligne : commande.getLignesCommande()) {
                sousTotal = sousTotal.add(ligne.getSousTotal());
            }
        }

        commande.setSousTotal(sousTotal);
        commande.setTotalHT(sousTotal);

        BigDecimal montantTVA = sousTotal.multiply(commande.getTauxTVA()).divide(new BigDecimal("100"));
        commande.setMontantTVA(montantTVA);
        commande.setTotalTTC(sousTotal.add(montantTVA));
        commande.setDateModification(LocalDateTime.now());
    }

    private boolean peutEtreModifiee(Commande commande) {
        return commande.getStatut() == StatutCommande.EN_ATTENTE;
    }

    // ... (toutes les autres méthodes existantes restent identiques)
    @Transactional(readOnly = true)
    public Optional<Commande> obtenirCommandeAvecDetails(Long idCommande) {
        return commandeRepository.findByIdWithDetails(idCommande);
    }

    @Transactional(readOnly = true)
    public List<Commande> obtenirCommandesClient(Long idClient) {
        return commandeRepository.findByClientIdOrderByDateCommandeDesc(idClient);
    }

    @Transactional(readOnly = true)
    public List<Commande> obtenirCommandesParStatut(StatutCommande statut) {
        return commandeRepository.findByStatut(statut);
    }

    @Transactional(readOnly = true)
    public List<Commande> obtenirCommandesAujourdhui() {
        return commandeRepository.findCommandesAujourdhui();
    }

    @Transactional(readOnly = true)
    public BigDecimal calculerChiffreAffaires() {
        return commandeRepository.calculateChiffreAffaires(StatutCommande.PAYEE);
    }

    /**
     * Annuler une commande
     */
    public Commande annulerCommande(Long idCommande) {
        Optional<Commande> commandeOpt = commandeRepository.findById(idCommande);
        if (commandeOpt.isEmpty()) {
            throw new RuntimeException("Commande non trouvée avec l'ID : " + idCommande);
        }

        Commande commande = commandeOpt.get();

        if (commande.getStatut() == StatutCommande.ANNULEE) {
            throw new RuntimeException("Cette commande est déjà annulée");
        }

        if (commande.getStatut() == StatutCommande.LIVREE) {
            throw new RuntimeException("Impossible d'annuler une commande déjà livrée");
        }

        commande.setStatut(StatutCommande.ANNULEE);
        commande.setDateModification(LocalDateTime.now());

        // Mettre à jour automatiquement la facture associée
        mettreAJourFactureAutomatiquement(commande);

        return commandeRepository.save(commande);
    }

    /**
     * Supprimer un produit d'une commande
     */
    public Commande supprimerProduitDeCommande(Long idCommande, Long idProduit) {
        Optional<Commande> commandeOpt = commandeRepository.findByIdWithLignesCommande(idCommande);
        if (commandeOpt.isEmpty()) {
            throw new RuntimeException("Commande non trouvée avec l'ID : " + idCommande);
        }

        Commande commande = commandeOpt.get();

        if (!peutEtreModifiee(commande)) {
            throw new RuntimeException("Cette commande ne peut plus être modifiée");
        }

        LigneCommande ligneASupprimer = null;
        for (LigneCommande ligne : commande.getLignesCommande()) {
            if (ligne.getProduit().getId().equals(idProduit)) {
                ligneASupprimer = ligne;
                break;
            }
        }

        if (ligneASupprimer == null) {
            throw new RuntimeException("Produit non trouvé dans cette commande");
        }

        commande.getLignesCommande().remove(ligneASupprimer);
        ligneCommandeRepository.delete(ligneASupprimer);

        calculerTotauxCommande(commande);
        Commande commandeModifiee = commandeRepository.save(commande);

        // Mettre à jour la facture automatiquement
        mettreAJourFactureAutomatiquement(commandeModifiee);

        return commandeModifiee;
    }

    /**
     * Modifier la quantité d'un produit dans une commande
     */
    public Commande modifierQuantiteProduit(Long idCommande, Long idProduit, Integer nouvelleQuantite) {
        if (nouvelleQuantite <= 0) {
            return supprimerProduitDeCommande(idCommande, idProduit);
        }

        Optional<Commande> commandeOpt = commandeRepository.findByIdWithLignesCommande(idCommande);
        if (commandeOpt.isEmpty()) {
            throw new RuntimeException("Commande non trouvée avec l'ID : " + idCommande);
        }

        Commande commande = commandeOpt.get();

        if (!peutEtreModifiee(commande)) {
            throw new RuntimeException("Cette commande ne peut plus être modifiée");
        }

        LigneCommande ligneAModifier = null;
        for (LigneCommande ligne : commande.getLignesCommande()) {
            if (ligne.getProduit().getId().equals(idProduit)) {
                ligneAModifier = ligne;
                break;
            }
        }

        if (ligneAModifier == null) {
            throw new RuntimeException("Produit non trouvé dans cette commande");
        }

        ligneAModifier.setQuantite(nouvelleQuantite);
        ligneAModifier.calculerSousTotal();
        ligneCommandeRepository.save(ligneAModifier);

        calculerTotauxCommande(commande);
        Commande commandeModifiee = commandeRepository.save(commande);

        // Mettre à jour la facture automatiquement
        mettreAJourFactureAutomatiquement(commandeModifiee);

        return commandeModifiee;
    }

    /**
     * Mettre à jour le commentaire d'une commande
     */
    public Commande mettreAJourCommentaire(Long idCommande, String commentaire) {
        Optional<Commande> commandeOpt = commandeRepository.findById(idCommande);
        if (commandeOpt.isEmpty()) {
            throw new RuntimeException("Commande non trouvée avec l'ID : " + idCommande);
        }

        Commande commande = commandeOpt.get();
        commande.setCommentaire(commentaire);
        commande.setDateModification(LocalDateTime.now());
        return commandeRepository.save(commande);
    }
    public List<Commande> obtenirToutesLesCommandes() {
        return commandeRepository.findAll();
    }
    public List<Commande> obtenirCommandesParClient(Long clientId) {
        return commandeRepository.findByClientId(clientId);
    }
    public boolean commandeAppartientAuClient(Long commandeId, Long clientId) {
        return commandeRepository.findById(commandeId)
                .map(c -> c.getClient().getId().equals(clientId))
                .orElse(false);
    }
    public List<Commande> getCommandesParClient(Long clientId) {
        User client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'ID: " + clientId));

        return commandeRepository.findByClientId(clientId);
    }

    public Commande getCommandeById(Long id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + id));
    }
    /**
     * Mettre à jour le taux TVA d'une commande
     */
    public Commande mettreAJourTauxTVA(Long idCommande, BigDecimal nouveauTaux) {
        Optional<Commande> commandeOpt = commandeRepository.findByIdWithLignesCommande(idCommande);
        if (commandeOpt.isEmpty()) {
            throw new RuntimeException("Commande non trouvée avec l'ID : " + idCommande);
        }

        Commande commande = commandeOpt.get();

        if (!peutEtreModifiee(commande)) {
            throw new RuntimeException("Cette commande ne peut plus être modifiée");
        }

        commande.setTauxTVA(nouveauTaux);
        calculerTotauxCommande(commande);
        Commande commandeModifiee = commandeRepository.save(commande);

        // Mettre à jour la facture automatiquement
        mettreAJourFactureAutomatiquement(commandeModifiee);

        return commandeModifiee;
    }
}