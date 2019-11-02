package es.unizar.murcy.controllers;

import es.unizar.murcy.components.JsonWebTokenUtil;
import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.model.Token;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.dto.*;
import es.unizar.murcy.service.JwtUserDetailsService;
import es.unizar.murcy.service.MailService;
import es.unizar.murcy.service.TokenService;
import es.unizar.murcy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private MailService mailService;

    @Autowired
    private JsonWebTokenUtil jsonWebTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private AuthUtilities authUtilities;

    @CrossOrigin
    @PostMapping("/api/user")
    public ResponseEntity create(@RequestBody RegisterUserDto registerUserDto) {
        if(Boolean.FALSE.equals(registerUserDto.isComplete())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(new ErrorMessage(HttpStatus.BAD_REQUEST, "Faltan campos"));
        }

        if (userService.existsByUsername(registerUserDto.getUsername())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(new ErrorMessage(HttpStatus.BAD_REQUEST, "Ya existe un usuario con ese nombre"));
        }

        if (userService.existsByEmail(registerUserDto.getEmail())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(new ErrorMessage(HttpStatus.BAD_REQUEST, "Ya existe un usuario con ese email"));
        }

        registerUserDto.setPassword(new BCryptPasswordEncoder().encode(registerUserDto.getPassword()));
        User user = userService.create(registerUserDto.toEntity());

        Token token = tokenService.create(new Token(user, UUID.randomUUID().toString(), new Date(System.currentTimeMillis() + Token.DEFAULT_TOKEN_EXPIRATION_TIME)));

        mailService.sendTokenConfirmationMail(token.getTokenValue(), user.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @CrossOrigin
    @GetMapping("/api/user/info")
    public ResponseEntity getCurrentUser(HttpServletRequest request) {
        Optional<User> user = authUtilities.getUserFromRequest(request);

        if(user.isPresent()) {
            return ResponseEntity.ok().body(new UserDto(user.get()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessage(HttpStatus.UNAUTHORIZED));
    }

    @CrossOrigin
    @GetMapping("/api/user/info/{id}")
    public ResponseEntity getUserById(HttpServletRequest request, @PathVariable long id) {
        Optional<User> user = authUtilities.getUserFromRequest(request);

        if(!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessage(HttpStatus.UNAUTHORIZED));
        }

        if(id == user.get().getId() || user.get().getRoles().contains(User.Rol.REVIEWER)) {
            Optional<User> fetchedUser = userService.findUserById(id);
            if(fetchedUser.isPresent()) {
                return ResponseEntity.ok().body(new UserDto(fetchedUser.get()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(HttpStatus.NOT_FOUND, "User not found"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessage(HttpStatus.UNAUTHORIZED));
    }

    @CrossOrigin
    @PutMapping("/api/user/info")
    public ResponseEntity putCurrentUser(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @CrossOrigin
    @PutMapping("/api/user/info/{id}")
    public ResponseEntity putUserById(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping(value = "/api/user/login")
    public ResponseEntity createAuthenticationToken(@RequestBody JsonWebTokenRequest jsonWebTokenRequest, HttpServletRequest request) {
        Optional<User> user = userService.findUserByUserName(jsonWebTokenRequest.getUsername());

        if(!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if(Boolean.FALSE.equals(user.get().getConfirmed())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMessage(HttpStatus.FORBIDDEN, "User not confirmed"));
        }

        authUtilities.authenticate(jsonWebTokenRequest.getUsername(), jsonWebTokenRequest.getPassword());

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(jsonWebTokenRequest.getUsername());

        final String token = jsonWebTokenUtil.generateToken(userDetails);

        User toUpdateUser = user.get();
        toUpdateUser.setLastIp(request.getRemoteAddr());
        userService.update(toUpdateUser);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @CrossOrigin
    @PostMapping(value = "/api/user/confirm/{tokenValue}")
    public ResponseEntity confirmToken(@PathVariable String tokenValue) {
        Optional<Token> token = tokenService.getTokenByValue(tokenValue);

        if(!token.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(new ErrorMessage(HttpStatus.NOT_FOUND, "Token not found"));
        }

        User user = token.get().getUser();

        userService.confirmUser(user);

        tokenService.delete(token.get());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}