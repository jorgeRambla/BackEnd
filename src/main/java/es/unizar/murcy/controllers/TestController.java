package es.unizar.murcy.controllers;

import es.unizar.murcy.model.HelloWorld;
import es.unizar.murcy.repository.HelloWorldRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {


    final private HelloWorldRepository helloWorldRepository;

    public TestController(HelloWorldRepository helloWorldRepository) {
        this.helloWorldRepository = helloWorldRepository;
    }


    @PostMapping("/api/user")
    public ResponseEntity create(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        if (helloWorldRepository.existsByUsername(username)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body("{\"code\": 400, \"message\": User already exists\"}");
        }

        String password = body.get("password");
        String encodedPassword = new BCryptPasswordEncoder().encode(password);
//        String hashedPassword = hashData.get_SHA_512_SecurePassword(password);
        String fullname = body.get("fullname");
        helloWorldRepository.save(new HelloWorld(username, encodedPassword, fullname));
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/api/test")
    public String test() {
        return "hola";
    }

}