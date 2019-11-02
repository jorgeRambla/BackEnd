package es.unizar.murcy.controllers;

import es.unizar.murcy.components.JsonWebTokenUtil;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private AuthenticationManager authenticationManager;

    @Autowired
    private JsonWebTokenUtil jsonWebTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @PostMapping("/api/user")
    public ResponseEntity create(@RequestBody RegisterUserDto registerUserDto) {
        if(Boolean.FALSE.equals(registerUserDto.isComplete())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(new ErrorMessage(HttpStatus.BAD_REQUEST, "Faltan campos"));
        }

        if (userService.existsByUsername(registerUserDto.getUsername())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(new ErrorMessage(HttpStatus.BAD_REQUEST, "Ya existe un usuario con ese nombre"));
        }
        registerUserDto.setPassword(new BCryptPasswordEncoder().encode(registerUserDto.getPassword()));
        User user = userService.create(registerUserDto.toEntity());

        Token token = tokenService.create(new Token(user, UUID.randomUUID().toString(), new Date(System.currentTimeMillis() + Token.DEFAULT_TOKEN_EXPIRATION_TIME)));

        mailService.sendTokenConfirmationMail(token.getTokenValue(), user.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/api/user/info")
    public ResponseEntity getCurrentUser(HttpServletRequest request) {
        final String authorization = request.getHeader("Authorization");

        final String username = jsonWebTokenUtil.getUserNameFromToken(authorization.substring(7));

        Optional<User> user = userService.findUserByUserName(username);
        if(user.isPresent()) {
            return ResponseEntity.ok().body(new UserDto(user.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(HttpStatus.NOT_FOUND, "User not found"));
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

        authenticate(jsonWebTokenRequest.getUsername(), jsonWebTokenRequest.getPassword());

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(jsonWebTokenRequest.getUsername());

        final String token = jsonWebTokenUtil.generateToken(userDetails);

        User toUpdateUser = user.get();
        toUpdateUser.setLastIp(request.getRemoteAddr());
        userService.update(toUpdateUser);

        return ResponseEntity.ok(new JwtResponse(token));
    }

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

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

}