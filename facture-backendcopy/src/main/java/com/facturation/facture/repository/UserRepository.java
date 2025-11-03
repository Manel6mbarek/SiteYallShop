// ============================================
// REPOSITORY UPDATED
// ============================================
package com.facturation.facture.repository;

import com.facturation.facture.model.User;
import com.facturation.facture.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Recherche par email
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmail(String email);

    // Recherche par rôle
    List<User> findByRole(Role role);
    List<User> findByRoleAndActifTrue(Role role);

    // Recherche par téléphone
    Optional<User> findByTelephone(String telephone);

    // Recherche des clients uniquement (rôle CLIENT)
    @Query("SELECT u FROM User u WHERE u.role = 'CLIENT' ORDER BY u.nom ASC, u.prenom ASC")
    List<User> findAllClients();

    // Recherche des admins uniquement (rôle ADMIN)
    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN' ORDER BY u.nom ASC, u.prenom ASC")
    List<User> findAllAdmins();

    // Recherche globale parmi les clients
    @Query("SELECT u FROM User u WHERE u.role = 'CLIENT' AND (" +
            "LOWER(u.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "u.telephone LIKE CONCAT('%', :searchTerm, '%'))")
    List<User> rechercherClients(@Param("searchTerm") String searchTerm);

    // Obtenir un client avec ses commandes
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.commandes WHERE u.id = :id AND u.role = 'CLIENT'")
    Optional<User> findClientByIdWithCommandes(@Param("id") Long id);

    // Compter le nombre total de clients
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'CLIENT'")
    Long countTotalClients();

    // Obtenir les clients les plus récents
    @Query("SELECT u FROM User u WHERE u.role = 'CLIENT' ORDER BY u.dateCreation DESC")
    List<User> findRecentClients();

    // Obtenir tous les utilisateurs triés
    @Query("SELECT u FROM User u ORDER BY u.nom ASC, u.prenom ASC")
    List<User> findAllOrderByNomAndPrenom();

    // Vérifier si un utilisateur est actif
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.actif = true")
    Optional<User> findActiveUserById(@Param("id") Long id);
}