package es.unizar.murcy.controllers;

import es.unizar.murcy.components.JsonWebTokenUtil;
import es.unizar.murcy.model.Token;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.dto.ErrorMessage;
import es.unizar.murcy.model.dto.JsonWebTokenRequest;
import es.unizar.murcy.model.dto.JwtResponse;
import es.unizar.murcy.model.dto.RegisterUserDto;
import es.unizar.murcy.service.JwtUserDetailsService;
import es.unizar.murcy.service.TokenService;
import es.unizar.murcy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JsonWebTokenUtil jsonWebTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @PostMapping("/api/user")
    public ResponseEntity create(@RequestBody RegisterUserDto registerUserDto) {
        if (userService.existsByUsername(registerUserDto.getUsername())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(new ErrorMessage(HttpStatus.BAD_REQUEST, "Ya existe un usuario con ese nombre"));
        }
        registerUserDto.setPassword(new BCryptPasswordEncoder().encode(registerUserDto.getPassword()));
        User user = userService.create(registerUserDto.toEntity());

        Token token = tokenService.create(new Token(user, UUID.randomUUID().toString(), new Date(System.currentTimeMillis() + Token.DEFAULT_TOKEN_EXPIRATION_TIME)));



        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(value = "/api/user/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JsonWebTokenRequest request) throws Exception {
        authenticate(request.getUsername(), request.getPassword());

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(request.getUsername());

        final String token = jsonWebTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

}