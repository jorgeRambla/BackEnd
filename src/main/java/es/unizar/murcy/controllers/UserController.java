package es.unizar.murcy.controllers;

import es.unizar.murcy.components.JsonWebTokenUtil;
import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.model.Token;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.dto.*;
import es.unizar.murcy.model.request.JsonWebTokenRequest;
import es.unizar.murcy.model.request.RegisterUserRequest;
import es.unizar.murcy.model.request.UpdateUserRequest;
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

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin
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
    public ResponseEntity create(@RequestBody RegisterUserRequest registerUserRequest) {
        if(Boolean.FALSE.equals(registerUserRequest.isComplete())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST, "Faltan campos"));
        }

        if (Boolean.FALSE.equals(isMailValid(registerUserRequest.getEmail()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST, "Email is not valid"));
        }

        if (userService.existsByUsername(registerUserRequest.getUsername())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST, "Ya existe un usuario con ese nombre"));
        }

        if (userService.existsByEmail(registerUserRequest.getEmail())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST, "Ya existe un usuario con ese email"));
        }

        registerUserRequest.setPassword(new BCryptPasswordEncoder().encode(registerUserRequest.getPassword()));
        User user = userService.create(registerUserRequest.toEntity());

        Token token = tokenService.create(new Token(user, UUID.randomUUID().toString(), new Date(System.currentTimeMillis() + Token.DEFAULT_TOKEN_EXPIRATION_TIME)));

        mailService.sendTokenConfirmationMail(token.getTokenValue(), user.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @CrossOrigin
    @GetMapping("/api/user/info")
    public ResponseEntity getCurrentUser(HttpServletRequest request) {
        Optional<User> user = authUtilities.getUserFromRequest(request);

        if(user.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(new UserDto(user.get()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }

    @CrossOrigin
    @GetMapping("/api/user/info/{id}")
    public ResponseEntity getUserById(HttpServletRequest request, @PathVariable long id) {
        Optional<User> user = authUtilities.getUserFromRequest(request);

        if(!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        Optional<User> optionalUser = userService.findUserById(id);

        if(!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
        }

        if(optionalUser.get().getId() == user.get().getId() || user.get().getRoles().contains(User.Rol.REVIEWER)) {
            return ResponseEntity.status(HttpStatus.OK).body(new UserDto(optionalUser.get()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }

    @CrossOrigin
    @PutMapping("/api/user/info")
    public ResponseEntity putCurrentUser(HttpServletRequest request,
                                          @RequestBody UpdateUserRequest updateUserRequest) {
        Optional<User> user = authUtilities.getUserFromRequest(request);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        return putUserById(request, user.get().getId(), updateUserRequest);
    }

    @CrossOrigin
    @PutMapping("/api/user/info/{id}")
    public ResponseEntity putUserById(HttpServletRequest request, @PathVariable long id, @RequestBody UpdateUserRequest updateUserRequest) {
        Optional<User> user = authUtilities.getUserFromRequest(request);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
        }

        Optional<User> finalUser = userService.findUserById(id);

        if(!finalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND, "User not found"));
        }

        if(user.get().getId() == id || user.get().getRoles().contains(User.Rol.REVIEWER)) {

            if(Boolean.TRUE.equals(isUpdateValid(updateUserRequest.getEmail()))) {

                if(Boolean.FALSE.equals(isMailValid(updateUserRequest.getEmail()))) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST, "Email is not valid"));
                }

                if(!finalUser.get().getEmail().equals(updateUserRequest.getEmail()) && userService.existsByEmail(updateUserRequest.getEmail())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST, "Ya existe un usuario con ese email"));
                } else {
                    finalUser.get().setEmail(updateUserRequest.getEmail());
                }
            }

            if(Boolean.TRUE.equals(isUpdateValid(updateUserRequest.getUsername()))) {

                if(!finalUser.get().getUsername().equals(updateUserRequest.getUsername()) && userService.existsByUsername(updateUserRequest.getUsername())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST, "Ya existe un usuario con ese nombre"));
                } else {
                    finalUser.get().setUsername(updateUserRequest.getUsername());
                }
            }

            User updatedUser = updateUserDataAuthorized(updateUserRequest, user.get(), finalUser.get());

            return ResponseEntity.status(HttpStatus.CREATED).body(new UserDto(updatedUser));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDto(HttpStatus.UNAUTHORIZED));
    }

    @CrossOrigin
    @PostMapping(value = "/api/user/login")
    public ResponseEntity createAuthenticationToken(@RequestBody JsonWebTokenRequest jsonWebTokenRequest, HttpServletRequest request) {
        Optional<User> user = userService.findUserByUserName(jsonWebTokenRequest.getUsername());

        if(!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if(Boolean.FALSE.equals(user.get().getConfirmed())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMessageDto(HttpStatus.FORBIDDEN, "User not confirmed"));
        }

        authUtilities.authenticate(jsonWebTokenRequest.getUsername(), jsonWebTokenRequest.getPassword());

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(jsonWebTokenRequest.getUsername());

        final String token = jsonWebTokenUtil.generateToken(userDetails);

        User toUpdateUser = user.get();
        toUpdateUser.setLastIp(request.getRemoteAddr());
        userService.update(toUpdateUser);

        return ResponseEntity.status(HttpStatus.OK).body(new JsonWebTokenDto(token));
    }

    @CrossOrigin
    @PostMapping(value = "/api/user/confirm/{tokenValue}")
    public ResponseEntity confirmToken(@PathVariable String tokenValue) {
        Optional<Token> token = tokenService.getTokenByValue(tokenValue);

        if(!token.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(new ErrorMessageDto(HttpStatus.NOT_FOUND, "Token not found"));
        }

        User user = token.get().getUser();

        userService.confirmUser(user);

        tokenService.delete(token.get());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private User updateUserDataAuthorized(UpdateUserRequest updateUserRequest, User user, User finalUser) {
        if(updateUserRequest.getFullName() != null && !updateUserRequest.getFullName().equals("")) {
            finalUser.setFullName(updateUserRequest.getFullName());
        }

        if(updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().equals("")) {
            finalUser.setPassword(new BCryptPasswordEncoder().encode(updateUserRequest.getPassword()));
        }

        if(user.getRoles().contains(User.Rol.REVIEWER) && updateUserRequest.getRol() != null) {
            if(updateUserRequest.getRol().length == 0){
                updateUserRequest.setRol(new String[]{User.Rol.USER.name()});
            }
            finalUser.setRoles(updateUserRequest.getRolSet());
        }

        return userService.update(finalUser);
    }

    private Boolean isMailValid(String mail) {
        boolean isValid = true;
        try {
            InternetAddress emailAddr = new InternetAddress(mail);
            emailAddr.validate();
        } catch (AddressException ex) {
            isValid = false;
        }
        return isValid;
    }

    private Boolean isUpdateValid(String value) {
        return value != null && !value.equals("");
    }
}