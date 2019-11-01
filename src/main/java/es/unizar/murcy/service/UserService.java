package es.unizar.murcy.service;

import es.unizar.murcy.model.User;
import es.unizar.murcy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    UserRepository userRepository;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findUserByUserName(String userName) {
        return userRepository.findByUsername(userName);
    }

    public User createUser(User u) {
        return userRepository.save(u);
    }

    public User updateUser(User u) {
        return userRepository.save(u);
    }

    public void deleteUser(User user) {
        deleteUser(user.getId());
    }

    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
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
        return user;
    }
}