package bugboard.repository;

import bugboard.model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Integer> {

    // Serve per evitare i NullPointerException costringendo chi chiama questo metodo
    // a gestire obbligatoriamente il caso in cui l'utente non esista nel DB
    Optional<AuthUser> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<AuthUser> findById(int id);

    boolean existsById(int id);

    @Query("SELECT a.email, COUNT(i) FROM AuthUser a LEFT JOIN Issue i ON a = i.assignee and i.stato = 'ASSEGNATO' WHERE a.statoAccount = true and a.ruolo = INTERNAL_USER GROUP BY a.email")
    List<Object[]> findAllIssuesPerUser();

    @Query(value = "SELECT u.email, AVG(EXTRACT(EPOCH FROM i.data_risoluzione) - EXTRACT(EPOCH FROM i.data_assegnazione)) / 3600.0 " +
            "FROM issue i " +
            "JOIN auth_user u ON i.assignee_id = u.id " +
            "WHERE i.stato = 'RISOLTO'" +
            "GROUP BY u.email", nativeQuery = true)
    List<Object[]> findAllTimePerUser();

    @Query(value = "SELECT u.email, COUNT(i.id) AS issue_risolte FROM auth_user u LEFT JOIN issue i ON u.id = i.assignee_id AND i.stato = 'RISOLTO'" +
            "GROUP BY u.email" , nativeQuery = true)
    List<Object[]> findAllRisoltePerUser();

}
