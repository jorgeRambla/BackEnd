package es.unizar.murcy.repository;

import es.unizar.murcy.model.Token;
import es.unizar.murcy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findTokenById(long idToken);

    Optional<Token> findTokenByTokenValue(String tokenValue);

    Optional<Token> findTokenByUser(User user);

    List<Token> findTokenByExpirationDateAfter(Date currentDate);
}
