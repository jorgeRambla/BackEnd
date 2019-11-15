package es.unizar.murcy.repository;

import es.unizar.murcy.MurcyApplication;
import es.unizar.murcy.model.User;
import es.unizar.murcy.service.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MurcyApplication.class)
public class EditorRequestRepositoryTest {

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

    @After
    public void after() {
        userService.deleteUser(user);
        userService.deleteUser(confirmedUser);
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

/*
    public void deleteUser(User user) {
        deleteUser(user.getId());
    }

    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public User confirmUser(long userId) {
        Optional<User> user = findUserById(userId);
        if(user.isPresent()) {
            return confirmUser(user.get());
        }
        return null;
    }

    public User confirmUser(User user) {
        user.setConfirmed(true);
        return update(user);
    }*/
}
