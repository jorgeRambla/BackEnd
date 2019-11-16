package es.unizar.murcy.service;

import es.unizar.murcy.MurcyApplication;
import es.unizar.murcy.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.DisabledIf;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MurcyApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceTest {

    @Autowired
    UserService userService;

    private User user;
    private User confirmedUser;

    @Before
    public void before() {
        User newUser = new User("Test", "testpass", "test@test.com", "Test Test");
        newUser.addRol(User.Rol.USER);
        this.user = userService.create(newUser);

        User newConfirmedUser = new User("Confirmed", "testpass", "test2@test.com", "Test2 Test");
        newConfirmedUser.setConfirmed(true);
        this.confirmedUser = userService.create(newConfirmedUser);
    }

    @Test
    public void testFindAllUsers() {
        assertFalse(userService.findAllUsers().isEmpty());
        assertEquals(userService.findAllUsers().size(), 2);
    }

    @Test
    public void testFindUserById() {
        assertTrue(userService.findUserById(user.getId()).isPresent());
        assertTrue(userService.findUserById(confirmedUser.getId()).isPresent());
        assertFalse(userService.findUserById(-1L).isPresent());
    }

    @Test
    public void testFindUserByUserName() {
        assertTrue(userService.findUserByUserName(user.getUsername()).isPresent());
        assertTrue(userService.findUserByUserName(confirmedUser.getUsername()).isPresent());
        assertFalse(userService.findUserByUserName("NoExists").isPresent());
    }

    @Test
    public void testUpdate() {
        String prevUserName = user.getUsername();
        assertTrue(userService.findUserByUserName(user.getUsername()).isPresent());
        user.setUsername("newUserName");
        Date updateDate = new Date();
        user = userService.update(user);

        assertFalse(userService.findUserByUserName(prevUserName).isPresent());
        assertTrue(userService.findUserByUserName(user.getUsername()).isPresent());

        assertTrue(Math.abs(user.getModifiedDate().getTime() - updateDate.getTime()) < 50);
    }

    @Test
    public void testDelete() {
        assertTrue(userService.findUserByUserName(user.getUsername()).isPresent());
        userService.deleteUser(user);
        assertFalse(userService.findUserByUserName(user.getUsername()).isPresent());
        assertTrue(userService.findUserByUserName(confirmedUser.getUsername()).isPresent());
    }

    @Test
    public void testDeleteById() {
        assertTrue(userService.findUserByUserName(user.getUsername()).isPresent());
        userService.deleteUser(user.getId());
        assertFalse(userService.findUserByUserName(user.getUsername()).isPresent());
        assertTrue(userService.findUserByUserName(confirmedUser.getUsername()).isPresent());
    }

    @Test
    public void testExistsByUserName() {
        assertTrue(userService.existsByUsername(user.getUsername()));
        assertTrue(userService.existsByUsername(confirmedUser.getUsername()));
        assertFalse(userService.existsByUsername("NotExists"));

    }

    @Test
    public void testExistsByEmail() {
        assertTrue(userService.existsByEmail(user.getEmail()));
        assertFalse(userService.existsByEmail("NotExists@mail.com"));
    }

    @Test
    public void testConfirmUser() {
        assertFalse(user.getConfirmed());
        assertTrue(userService.confirmUser(user).getConfirmed());
    }

    @Test
    public void testConfirmUserById() {
        assertFalse(user.getConfirmed());
        assertTrue(userService.confirmUser(user.getId()).getConfirmed());
    }

    @Test
    public void testConfirmUserById_notFound() {
        assertNull(userService.confirmUser(-1));
    }
}
