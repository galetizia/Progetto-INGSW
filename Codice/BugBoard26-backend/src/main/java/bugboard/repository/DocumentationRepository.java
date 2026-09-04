package bugboard.repository;

import bugboard.model.Documentation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentationRepository extends JpaRepository<Documentation, Integer> {

    boolean existsByTitolo(String titolo);

    Optional<Documentation> findByTitolo(String titolo);

}