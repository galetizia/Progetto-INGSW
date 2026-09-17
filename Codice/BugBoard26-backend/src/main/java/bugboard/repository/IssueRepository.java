package bugboard.repository;

import bugboard.enums.StatoIssue;
import bugboard.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interfaccia per l'accesso ai dati delle Issue.
 * Gestisce il salvataggio, la ricerca e l'aggregazione statistica delle issue.
 */
@Repository
public interface IssueRepository extends JpaRepository<Issue, Integer> {

    List<Issue> findByStatoIn(List<StatoIssue> stati);

    /**
     * Raggruppa e conta le issue attive in base alla loro tipologia.
     * Utilizzato per alimentare i grafici della dashboard.
     *
     * @return Una lista di array, dove l'indice 0 è la tipologia (TipoIssue) e l'indice 1 è il conteggio (Number).
     */
    @Query("SELECT i.tipo, COUNT(i) FROM Issue i WHERE i.stato = TO_DO or i.stato= ASSEGNATO GROUP BY i.tipo")
    List<Object[]> findAllIssueTypes();

    /**
     * Raggruppa e conta tutte le issue presenti nel database in base al loro stato attuale.
     *
     * @return Una lista di array, dove l'indice 0 è lo stato (StatoIssue) e l'indice 1 è il conteggio (Number).
     */
    @Query("SELECT i.stato, COUNT(i) FROM Issue i GROUP BY i.stato")
    List<Object[]> findAllIssueStates();

}
