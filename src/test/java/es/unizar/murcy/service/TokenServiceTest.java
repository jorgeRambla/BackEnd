package es.unizar.murcy.service;

import es.unizar.murcy.MurcyApplication;
import es.unizar.murcy.model.Token;
import es.unizar.murcy.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MurcyApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TokenServiceTest {

    @Autowired
    TokenService tokenService;

    @Autowired
    UserService userService;

    private User user;
    private User user2;

    private Token token;
    private Token expiredToken;

    @Before
    public void before() {
        User newUser = new User("Test", "testpass", "test@test.com", "Test Test");
        this.user = userService.create(newUser);

        newUser = new User("Test2", "testpass", "test2@test.com", "Test2 Test2");
        this.user2 = userService.create(newUser);
        Token token = new Token(this.user, "token1", new Date(System.currentTimeMillis() + Token.DEFAULT_TOKEN_EXPIRATION_TIME));
        this.token = tokenService.create(token);

        token = new Token(this.user2, "token2", new Date(System.currentTimeMillis() - Token.DEFAULT_TOKEN_EXPIRATION_TIME));
        this.expiredToken = tokenService.create(token);
    }

    @Test
    public void testGetAll() {
        assertEquals(2, tokenService.getAll().size());
    }

    @Test
    public void testGetTokenById() {
        assertFalse(tokenService.getToken(-1).isPresent());

        assertTrue(tokenService.getToken(token.getId()).isPresent());
        assertTrue(tokenService.getToken(expiredToken.getId()).isPresent());
    }

    @Test
    public void testGetTokenByValue() {
        assertFalse(tokenService.getTokenByValue("notExists").isPresent());

        assertTrue(tokenService.getTokenByValue(token.getTokenValue()).isPresent());
        assertTrue(tokenService.getTokenByValue(expiredToken.getTokenValue()).isPresent());
    }

    @Test
    public void testUpdate() {
        String prevTokenValue = token.getTokenValue();
        this.token.setTokenValue("new");

        this.token = tokenService.update(this.token);

        assertNotEquals(prevTokenValue, this.token.getTokenValue());

        assertEquals("new", this.token.getTokenValue());
    }

    @Test
    public void testDelete()  {
        assertTrue(tokenService.getToken(token.getId()).isPresent());
        assertTrue(tokenService.getToken(expiredToken.getId()).isPresent());

        tokenService.delete(token);

        assertFalse(tokenService.getToken(token.getId()).isPresent());
        assertTrue(tokenService.getToken(expiredToken.getId()).isPresent());
    }

    @Test
    public void testDeleteById() {
        assertTrue(tokenService.getToken(token.getId()).isPresent());
        assertTrue(tokenService.getToken(expiredToken.getId()).isPresent());

        tokenService.delete(token.getId());

        assertFalse(tokenService.getToken(token.getId()).isPresent());
        assertTrue(tokenService.getToken(expiredToken.getId()).isPresent());
    }

    @Test
    public void testGetTokenByUser() {
        assertTrue(tokenService.getTokenByUser(user).isPresent());
    }

    @Test
    public void testGetExpiredTokens() {
        List<Token> expiredTokens = tokenService.getExpiredTokens(new Date());
        assertEquals(1, expiredTokens.size());
        assertTrue(expiredTokens.contains(expiredToken));
    }
}
