package es.unizar.murcy.service;

import es.unizar.murcy.model.Token;
import es.unizar.murcy.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TokenService {

    @Autowired
    TokenRepository tokenRepository;

    public List<Token> findAllToken() {
        return tokenRepository.findAll();
    }

    public Token findTokenById(long idToken) {
        return tokenRepository.findTokenById(idToken);
    }

    public Token createToken(Token t) {
        return tokenRepository.save(t);
    }

    public Token updateToken(Token t) {
        return tokenRepository.save(t);
    }

    public void deleteToken(long idToken) {
        tokenRepository.deleteById(idToken);
    }
}
