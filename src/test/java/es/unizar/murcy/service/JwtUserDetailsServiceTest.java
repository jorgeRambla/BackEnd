package es.unizar.murcy.service;

import es.unizar.murcy.MurcyApplication;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.request.RegisterUserRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MurcyApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class JwtUserDetailsServiceTest {

    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    UserService userService;

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadUserByUsername_exception() {
        jwtUserDetailsService.loadUserByUsername("NotExists");
    }

    @Test
    public void testLoadUserByUsername() {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUsername("test");
        registerUserRequest.setEmail("test@test.com");
        registerUserRequest.setFullName("Test test");
        registerUserRequest.setPassword("test");

        userService.create(registerUserRequest.toEntity());

        assertEquals(org.springframework.security.core.userdetails.User.class,
                jwtUserDetailsService.loadUserByUsername("test").getClass());
    }
}
