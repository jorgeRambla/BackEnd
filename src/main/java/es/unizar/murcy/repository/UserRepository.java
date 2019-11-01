package es.unizar.murcy.repository;

import es.unizar.murcy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndConfirmedIsTrue(String username);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndConfirmedIsTrue(String username);
}