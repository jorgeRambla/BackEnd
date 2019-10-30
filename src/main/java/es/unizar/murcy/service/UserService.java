package es.unizar.murcy.service;

import es.unizar.murcy.model.Usuario;
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

    public List<Usuario> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<Usuario> findUserById(long id) {
        return userRepository.findById(id);
    }

    public Usuario createUser(Usuario u) {
        return userRepository.save(u);
    }

    public Usuario updateUser(Usuario u) {
        return userRepository.save(u);
    }

    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }
}