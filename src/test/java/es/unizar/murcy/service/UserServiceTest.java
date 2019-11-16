package es.unizar.murcy.service;

import es.unizar.murcy.model.User;
import es.unizar.murcy.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;


public class UserServiceTest {
    private User user;
    private User newUser1;
    private User newConfirmedUser;

    @Autowired
    private UserRepository userRepository;
    private User confirmedUser;
    private UserService userService;

    @Before
    public void before() {
        user = new User("Test", "testpass", "test@test.com", "Test Test");
        user.addRol(User.Rol.USER);
        this.user = userRepository.save(user);

        newUser1 = new User("Test1", "testpass1", "test1@test.com", "Test1 Test1");
        newUser1.addRol(User.Rol.USER);
        this.user = userRepository.save(newUser1);

        newConfirmedUser = new User("Confirmed", "testpass", "test2@test.com", "Test2 Test");
        newConfirmedUser.setConfirmed(true);
        this.confirmedUser = userRepository.save(newConfirmedUser);
    }

    @After
    public void after() {
        userRepository.deleteAll();
    }

    @Test
    public void testFindAllUsers() {

    }

    @Test
    public void testFindUserById() {
        assertTrue(userService.findUserById(user.getId()).isPresent());
        assertFalse(userService.findUserById(12).isPresent());
    }

    @Test
    public void testFindUserByUserName() {
        assertTrue(userService.findUserByUserName(user.getUsername()).isPresent());
        assertFalse(userService.findUserByUserName("NoExists").isPresent());
    }

    @Test
    public void testUpdate(){

    }

    @Test
    public void testDeleteUser() {
        userService.deleteUser(user);
        assertTrue(userService.findUserByUserName(user.getUsername()).isPresent()==false);
    }

    @Test
    public void testDeleteUserById() {
        userService.deleteUser(user.getId());
        assertTrue(userService.findUserByUserName(user.getUsername()).isPresent()==false);
    }

    @Test
    public void testExistsByUsername() {
        assertTrue(userService.existsByUsername(user.getUsername()));
        assertFalse(userService.existsByUsername("Noexists"));
    }

    @Test
    public void testExistsByEmail() {
        assertTrue(userService.existsByEmail(user.getEmail()));
        assertFalse(userService.existsByEmail("Noexists"));
    }

    @Test
    public void testCreate() {
        User createdUser = new User("Test3", "testpass3", "test3@test.com", "Test3 Test3");
        createdUser.addRol(User.Rol.USER);
        userService.create(createdUser);
        assertTrue(userService.existsByUsername(createdUser.getUsername()));
    }

    @Test
    public void testConfirmUserById() {
        User u=userService.confirmUser(user.getId());
        assertTrue(u.equals(userRepository.findById(user.getId())));
        assertFalse(u.equals(userRepository.findByUsername("Noexists")));
    }

    @Test
    public void testConfirmUser() {
        User u=userService.confirmUser(user);
        assertTrue(u.equals(userRepository.findById(user.getId())));
        assertFalse(u.equals(userRepository.findByUsername("Noexists")));
    }
    
}
