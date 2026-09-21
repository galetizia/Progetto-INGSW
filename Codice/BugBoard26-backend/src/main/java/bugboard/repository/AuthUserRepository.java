package bugboard.repository;

import bugboard.model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaccia per l'accesso ai dati degli utenti (AuthUser).
 * Contiene i metodi standard di Spring Data e query personalizzate per l'estrazione delle statistiche aziendali.
 */
@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Integer> {

    Optional<AuthUser> findByEmail(String email);
    boolean existsByEmail(String email);

    /**
     * Calcola il numero totale di issue attualmente prese in carico (stato ASSEGNATO)
     * per ogni utente interno (INTERNAL_USER) che ha ancora l'account attivo.
     *
     * @return Una lista di array, dove l'indice 0 è l'email (String) e l'indice 1 è il conteggio (Number).
     */
    @Query("SELECT a.email, COUNT(i) FROM AuthUser a LEFT JOIN Issue i ON a = i.assignee and i.stato = ASSEGNATO WHERE a.statoAccount = true and a.ruolo = INTERNAL_USER GROUP BY a.email")
    List<Object[]> findAllIssuesPerUser();


    /**
     * Calcola il tempo medio di risoluzione delle issue (in ore) per ciascun utente.
     * Utilizza funzioni native di PostgreSQL (EXTRACT EPOCH) per calcolare la differenza esatta
     * tra la data di risoluzione e la data di assegnazione.
     *
     * @return Una lista di array, dove l'indice 0 è l'email (String) e l'indice 1 è la media in ore (Number).
     */
    @Query(value = "SELECT u.email, AVG(EXTRACT(EPOCH FROM i.data_risoluzione) - EXTRACT(EPOCH FROM i.data_assegnazione)) / 3600.0 " +
            "FROM issue i " +
            "JOIN auth_user u ON i.assignee_id = u.id " +
            "WHERE i.stato = 'RISOLTO' and u.stato_account = true " +
            "GROUP BY u.email", nativeQuery = true)
    List<Object[]> findAllTimePerUser();


    /**
     * Conta il numero totale di issue portate a termine (stato RISOLTO) da ciascun utente.
     *
     * @return Una lista di array, dove l'indice 0 è l'email (String) e l'indice 1 è il conteggio (Number).
     */
    @Query(value = "SELECT u.email, COUNT(i.id) AS issue_risolte FROM auth_user u LEFT JOIN issue i ON u.id = i.assignee_id AND i.stato = 'RISOLTO'" +
            "GROUP BY u.email" , nativeQuery = true)
    List<Object[]> findAllRisoltePerUser();

}
