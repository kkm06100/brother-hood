package brother.hood.auth.persistence.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import brother.hood.auth.persistence.User;

public interface UserJpaRepository extends CrudRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
