package bugboard.repository;

import bugboard.enums.StatoIssue;
import bugboard.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Integer> {

    List<Issue> findByStatoIn(List<StatoIssue> stati);

    @Query("SELECT i.tipo, COUNT(i) FROM Issue i WHERE i.stato = TO_DO or i.stato= ASSEGNATO GROUP BY i.tipo")
    List<Object[]> findAllIssueTypes();

    @Query("SELECT i.stato, COUNT(i) FROM Issue i GROUP BY i.stato")
    List<Object[]> findAllIssueStates();

}
