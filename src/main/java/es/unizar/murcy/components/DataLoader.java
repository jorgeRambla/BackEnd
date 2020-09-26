package es.unizar.murcy.components;

import es.unizar.murcy.model.User;
import es.unizar.murcy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataLoader implements ApplicationRunner {

    private final UserRepository userRepository;

    @Value("${murcy.config.admin.username}")
    private String adminUsername;

    @Value("${murcy.config.admin.password}")
    private String adminPassword;

    @Value("${murcy.config.admin.email}")
    private String adminEmail;

    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void run(ApplicationArguments args) {
        if(!userRepository.existsByUsername(adminUsername)){
            Set<User.Rol> rolSet = new HashSet<>();
            rolSet.add(User.Rol.USER);
            rolSet.add(User.Rol.EDITOR);
            rolSet.add(User.Rol.REVIEWER);
            rolSet.add(User.Rol.ADMINISTRATOR);
            userRepository.save(new User(adminUsername, new BCryptPasswordEncoder().encode(adminPassword), adminUsername, adminEmail, null, true, new HashSet<>(), rolSet));
        }
    }
}
