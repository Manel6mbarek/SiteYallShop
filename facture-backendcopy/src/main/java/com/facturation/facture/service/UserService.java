// ============================================
// SERVICE UPDATED
// ============================================
        package com.facturation.facture.service;

import com.facturation.facture.model.User;
import com.facturation.facture.model.enums.Role;
import com.facturation.facture.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ====================== MÉTHODES CLIENTS ======================

    /**
     * Sauvegarder un nouveau client
     */
    public User sauvegarderClient(User client) {
        // Validation avant sauvegarde
        if (userRepository.existsByEmail(client.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà : " + client.getEmail());
        }

        // S'assurer que c'est un client
        client.setRole(Role.CLIENT);
        client.setActif(true);
        client.setDateCreation(LocalDateTime.now());
        client.setDateModification(LocalDateTime.now());

        validerUtilisateur(client);
        return userRepository.save(client);
    }

    public Optional<User> trouverParEmail(String email) {
        return userRepository.findByEmail(email);
    }
    /**
     * Mettre à jour un client existant
     */
    public User mettreAJourClient(Long idClient, User clientMisAJour) {
        Optional<User> clientExistant = userRepository.findById(idClient);

        if (clientExistant.isEmpty()) {
            throw new RuntimeException("Client non trouvé avec l'ID : " + idClient);
        }

        User client = clientExistant.get();

        // Vérifier que c'est bien un client
        if (client.getRole() != Role.CLIENT) {
            throw new RuntimeException("L'utilisateur avec l'ID " + idClient + " n'est pas un client");
        }

        // Vérifier si l'email a changé et s'il n'existe pas déjà
        if (!client.getEmail().equals(clientMisAJour.getEmail()) &&
                userRepository.existsByEmail(clientMisAJour.getEmail())) {
            throw new RuntimeException("Un autre utilisateur utilise déjà cet email : " + clientMisAJour.getEmail());
        }

        // Mettre à jour les champs (garder le rôle CLIENT)
        client.setNom(clientMisAJour.getNom());
        client.setPrenom(clientMisAJour.getPrenom());
        client.setEmail(clientMisAJour.getEmail());
        client.setTelephone(clientMisAJour.getTelephone());
        client.setAdresse(clientMisAJour.getAdresse());
        client.setDateModification(LocalDateTime.now());

        validerUtilisateur(client);
        return userRepository.save(client);
    }

    /**
     * Obtenir un client par son ID
     */
    @Transactional(readOnly = true)
    public Optional<User> obtenirClientParId(Long idClient) {
        return userRepository.findById(idClient)
                .filter(user -> user.getRole() == Role.CLIENT);
    }

    /**
     * Obtenir un client par son email
     */
    @Transactional(readOnly = true)
    public Optional<User> obtenirClientParEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .filter(user -> user.getRole() == Role.CLIENT);
    }

    /**
     * Obtenir tous les clients
     */
    @Transactional(readOnly = true)
    public List<User> obtenirTousLesClients() {
        return userRepository.findAllClients();
    }

    /**
     * Rechercher des clients
     */
    @Transactional(readOnly = true)
    public List<User> rechercherClients(String termRecherche) {
        if (termRecherche == null || termRecherche.trim().isEmpty()) {
            return obtenirTousLesClients();
        }
        return userRepository.rechercherClients(termRecherche.trim());
    }

    /**
     * Obtenir un client avec ses commandes
     */
    @Transactional(readOnly = true)
    public Optional<User> obtenirClientAvecCommandes(Long idClient) {
        return userRepository.findClientByIdWithCommandes(idClient);
    }

    /**
     * Supprimer un client
     */
    public void supprimerClient(Long idClient) {
        Optional<User> client = obtenirClientParId(idClient);

        if (client.isEmpty()) {
            throw new RuntimeException("Client non trouvé avec l'ID : " + idClient);
        }

        // Vérifier si le client a des commandes
        if (client.get().getCommandes() != null && !client.get().getCommandes().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer le client car il a des commandes associées");
        }

        userRepository.deleteById(idClient);
    }

    /**
     * Vérifier si un email existe déjà
     */
    @Transactional(readOnly = true)
    public boolean emailExiste(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Compter le nombre total de clients
     */
    @Transactional(readOnly = true)
    public Long compterClients() {
        return userRepository.countTotalClients();
    }

    /**
     * Obtenir les clients les plus récents
     */
    @Transactional(readOnly = true)
    public List<User> obtenirClientsRecents() {
        return userRepository.findRecentClients();
    }

    // ====================== MÉTHODES GÉNÉRIQUES UTILISATEURS ======================

    /**
     * Créer un utilisateur (admin ou client)
     */
    public User creerUtilisateur(String nom, String prenom, String email, String telephone,
                                 String adresse, Role role, String motDePasse) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà : " + email);
        }

        User user = new User();
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setTelephone(telephone);
        user.setAdresse(adresse);
        user.setRole(role);
        user.setMotDePasse(motDePasse); // À encoder en production
        user.setActif(true);
        user.setDateCreation(LocalDateTime.now());
        user.setDateModification(LocalDateTime.now());

        validerUtilisateur(user);
        return userRepository.save(user);
    }

    /**
     * Obtenir tous les admins
     */
    @Transactional(readOnly = true)
    public List<User> obtenirTousLesAdmins() {
        return userRepository.findAllAdmins();
    }

    /**
     * Activer/désactiver un utilisateur
     */
    public User changerStatutUtilisateur(Long userId, boolean actif) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID : " + userId);
        }

        User user = userOpt.get();
        user.setActif(actif);
        user.setDateModification(LocalDateTime.now());

        return userRepository.save(user);
    }

    // ====================== MÉTHODES PRIVÉES ======================

    /**
     * Valider les données d'un utilisateur
     */
    private void validerUtilisateur(User user) {
        if (user.getNom() == null || user.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }

        if (user.getPrenom() == null || user.getPrenom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom est obligatoire");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'email est obligatoire");
        }

        if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            throw new IllegalArgumentException("Format d'email invalide");
        }

        if (user.getRole() == null) {
            throw new IllegalArgumentException("Le rôle est obligatoire");
        }
    }

    /**
     * Créer un client avec validation
     */
    public User creerClient(String nom, String prenom, String email, String telephone, String adresse) {
        return creerUtilisateur(nom, prenom, email, telephone, adresse, Role.CLIENT, null);
    }

}