package bugboard.repository;

import bugboard.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    boolean existsByTitolo(String titolo);

    Optional<Question> findByTitolo(String titolo);

}