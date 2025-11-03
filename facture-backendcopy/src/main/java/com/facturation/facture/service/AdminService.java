package com.facturation.facture.service;


import com.facturation.facture.dto.CommandeDTO;
import com.facturation.facture.dto.ProduitDTO;
import com.facturation.facture.model.Commande;
import com.facturation.facture.model.Produit;
import com.facturation.facture.repository.CommandeRepository;
import com.facturation.facture.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final CommandeRepository commandeRepository;
    private final ProduitRepository produitRepository;

    @Autowired
    public AdminService(CommandeRepository commandeRepository, ProduitRepository produitRepository) {
        this.commandeRepository = commandeRepository;
        this.produitRepository = produitRepository;
    }

    // Obtenir toutes les commandes
    public List<CommandeDTO> obtenirToutesLesCommandes() {
        return commandeRepository.findAll()
                .stream()
                .map(CommandeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtenir toutes les produits
    public List<ProduitDTO> obtenirTousLesProduits() {
        return produitRepository.findAll()
                .stream()
                .map(ProduitDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Obtenir les commandes par client
    public List<CommandeDTO> obtenirCommandesParClient(Long clientId) {
        return commandeRepository.findByClientId(clientId)
                .stream()
                .map(CommandeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Vérifier si une facture appartient à un client
    public boolean factureAppartientAuClient(Long factureId, Long clientId) {
        return commandeRepository.findById(factureId)
                .map(c -> c.getClient().getId().equals(clientId))
                .orElse(false);
    }

    // Vérifier si une commande appartient à un client
    public boolean commandeAppartientAuClient(Long commandeId, Long clientId) {
        return commandeRepository.findById(commandeId)
                .map(c -> c.getClient().getId().equals(clientId))
                .orElse(false);
    }
}
