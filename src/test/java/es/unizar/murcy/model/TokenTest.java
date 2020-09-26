package es.unizar.murcy.model;

import es.unizar.murcy.MurcyApplication;
import es.unizar.murcy.repository.TokenRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MurcyApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TokenTest {

    private Token token;
    private TokenRepository tokenRepository;
    private User newUser;

    @Before
    public void before() {
        newUser = new User("Test", "testpass", "test@test.com", "Test Test");
        newUser.addRol(User.Rol.USER);
        Date date=new Date();
        token=new Token(newUser,"Token",date);
    }

    @Test
    public void testGetId() {
        token.setId(1);
        assertEquals(1, token.getId());
    }

    @Test
    public void testSetId() {
        token.setId(1);
        assertEquals(token.getId(),1);
    }

    @Test
    public void testSetUser() {
        token.setUser(newUser);
        String userName=newUser.getUsername();
        assertEquals(token.getUser().getUsername(), userName);
    }

    @Test
    public void testGetUser() {
        token.setUser(newUser);
        String userName=newUser.getUsername();
        assertEquals(token.getUser().getUsername(), userName);
    }

    @Test
    public void testSetExpirationDate() {
        Date date=new Date();
        token.setExpirationDate(date);
        Date testDate=token.getExpirationDate();
        assertEquals(testDate, date);
    }

    @Test
    public void testGetCreateDateUser() {
        Date date=new Date();
        token.setExpirationDate(date);
        Date testDate=token.getExpirationDate();
        assertEquals(testDate, date);
    }

    @Test
    public void testSetTokenValue() {
        String tokenValue="Token";
        token.setTokenValue(tokenValue);
        String value=token.getTokenValue();
        assertEquals(value, tokenValue);
    }

    @Test
    public void testGetTokenValue() {
        String tokenValue="Token";
        token.setTokenValue(tokenValue);
        assertEquals(token.getTokenValue(), tokenValue);
    }

    @Test
    public void testTokenConstructor() {
        Date date=new Date();
        Token token1=new Token(newUser,"Token", date);
        assertEquals(token1.getTokenValue(), "Token");
    }

}
