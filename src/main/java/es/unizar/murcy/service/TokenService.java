package es.unizar.murcy.service;

import es.unizar.murcy.model.Token;
import es.unizar.murcy.model.User;
import es.unizar.murcy.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
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

    public Token update(Token token) {
        return tokenRepository.save(token);
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
}
