package es.unizar.murcy.repository;

import es.unizar.murcy.MurcyApplication;
import es.unizar.murcy.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MurcyApplication.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;
    private User confirmedUser;

    @Before
    public void before() {
        User newUser = new User("Test", "testpass", "test@test.com", "Test Test");
        newUser.addRol(User.Rol.USER);
        this.user = userRepository.save(newUser);

        User newConfirmedUser = new User("Confirmed", "testpass", "test2@test.com", "Test2 Test");
        newConfirmedUser.setConfirmed(true);
        this.confirmedUser = userRepository.save(newConfirmedUser);
    }

    @After
    public void after() {
        userRepository.deleteAll();
    }

    @Test
    public void testExistsByUserName() {
        assertTrue(userRepository.existsByUsername(user.getUsername()));
        assertFalse(userRepository.existsByUsername("NoExists"));
    }

    @Test
    public void testExistsByUserEmail() {
        assertTrue(userRepository.existsByEmail(user.getEmail()));
        assertFalse(userRepository.existsByEmail("NoExists"));
    }

    @Test
    public void testExistsByUserNameAndConfirmed() {
        assertTrue(userRepository.existsByUsernameAndConfirmedIsTrue(confirmedUser.getUsername()));
        assertFalse(userRepository.existsByUsernameAndConfirmedIsTrue(user.getUsername()));
        assertFalse(userRepository.existsByUsernameAndConfirmedIsTrue("NoExists"));
    }

    @Test
    public void testFindByUsernameAndConfirmedIsTrue() {
        assertTrue(userRepository.findByUsernameAndConfirmedIsTrue(confirmedUser.getUsername()).isPresent());
        assertFalse(userRepository.findByUsernameAndConfirmedIsTrue(user.getUsername()).isPresent());
        assertFalse(userRepository.findByUsernameAndConfirmedIsTrue("NoExists").isPresent());
    }

    @Test
    public void testFindByUsername() {
        assertTrue(userRepository.findByUsername(confirmedUser.getUsername()).isPresent());
        assertTrue(userRepository.findByUsername(user.getUsername()).isPresent());
        assertFalse(userRepository.findByUsername("NoExists").isPresent());
    }

}
