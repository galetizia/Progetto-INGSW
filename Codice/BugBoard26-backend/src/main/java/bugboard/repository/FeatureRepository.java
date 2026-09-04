package bugboard.repository;

import bugboard.model.Bug;
import bugboard.model.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Integer> {

    boolean existsByTitolo(String titolo);

    Optional<Bug> findByTitolo(String titolo);

}
