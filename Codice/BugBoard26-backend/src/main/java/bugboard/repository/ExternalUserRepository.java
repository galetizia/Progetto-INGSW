package bugboard.repository;

import bugboard.model.ExternalUser;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface ExternalUserRepository extends JpaRepository<ExternalUser, Integer>  {

    Optional<ExternalUser> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsById(int id);

    Optional<ExternalUser> findById(int id);
}
