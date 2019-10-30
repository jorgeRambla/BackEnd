package es.unizar.murcy.repository;

import es.unizar.murcy.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findTokenById(long idToken);
}
