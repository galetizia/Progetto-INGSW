package bugboard.repository;

import bugboard.model.Bug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BugRepository extends JpaRepository<Bug, Integer> {

    boolean existsByTitolo(String titolo);

    Optional<Bug> findByTitolo(String titolo);

}
