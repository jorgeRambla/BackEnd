package es.unizar.murcy.components;

import es.unizar.murcy.model.User;
import es.unizar.murcy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Value("${murcy.administrator:test}")
    private String adminUsername;

    @Value("${murcy.administrator.password:supersecretpassword}")
    private String adminPassword;


    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void run(ApplicationArguments args) {
        if(!userRepository.existsByUsername(adminUsername)){
            Set<User.Rol> rolSet = new HashSet<>();
            rolSet.add(User.Rol.USER);
            rolSet.add(User.Rol.EDITOR);
            rolSet.add(User.Rol.REVIEWER);
            userRepository.save(new User(adminUsername, new BCryptPasswordEncoder().encode(adminPassword), adminUsername, adminUsername + "@murcy.com", null, true, rolSet));
        }
    }
}
