package es.unizar.murcy.service;

import es.unizar.murcy.model.Token;
import es.unizar.murcy.model.User;
import es.unizar.murcy.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TokenService {

    @Autowired
    TokenRepository tokenRepository;

    public List<Token> getAll() {
        return tokenRepository.findAll();
    }

    public Optional<Token> getToken(long idToken) {
        return tokenRepository.findTokenById(idToken);
    }

    public Optional<Token> getTokenByValue(String tokenValue) {
        return tokenRepository.findTokenByTokenValue(tokenValue);
    }

    public Token update(Token token) {
        return tokenRepository.save(token);
    }

    public void delete(Token token)  {
        delete(token.getId());
    }

    public void delete(long id) {
        tokenRepository.deleteById(id);
    }

    public Token create(Token token) {
        return tokenRepository.save(token);
    }

    public Optional<Token> getTokenByUser(User user) {
        return tokenRepository.findTokenByUser(user);
    }

    public List<Token> getExpiratedTokens(Date currentDate) {
        return tokenRepository.findTokenByExpirationDateBefore(currentDate);
    }
}
