package bugboard.repository;

import bugboard.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Integer> {

    Optional<Issue> findByTitolo(String titolo);
    boolean existsByTitolo(String titolo);

    Optional<Issue> findById(int id);
    boolean existsById(int id);
}
