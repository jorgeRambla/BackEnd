package es.unizar.murcy.controllers;

import es.unizar.murcy.components.JsonWebTokenUtil;
import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.exceptions.token.TokenNotFoundException;
import es.unizar.murcy.exceptions.user.UserForbiddenException;
import es.unizar.murcy.exceptions.user.UserNotConfirmedException;
import es.unizar.murcy.exceptions.user.UserNotFoundException;
import es.unizar.murcy.exceptions.user.UserUnauthorizedException;
import es.unizar.murcy.exceptions.user.validation.UserBadRequestEmailAlreadyExistsException;
import es.unizar.murcy.exceptions.user.validation.UserBadRequestEmailNotValidException;
import es.unizar.murcy.exceptions.user.validation.UserBadRequestUsernameAlreadyExistsException;
import es.unizar.murcy.model.Token;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.dto.ErrorMessageDto;
import es.unizar.murcy.model.dto.JsonWebTokenDto;
import es.unizar.murcy.model.dto.UserDto;
import es.unizar.murcy.model.request.JsonWebTokenRequest;
import es.unizar.murcy.model.request.RegisterUserRequest;
import es.unizar.murcy.model.request.UpdateUserRequest;
import es.unizar.murcy.service.JwtUserDetailsService;
import es.unizar.murcy.service.MailService;
import es.unizar.murcy.service.TokenService;
import es.unizar.murcy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
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
    public ResponseEntity<UserDto> getCurrentUser(HttpServletRequest request) {
        User user = authUtilities.getUserFromRequest(request);

        return ResponseEntity.status(HttpStatus.OK).body(new UserDto(user));
    }

    @CrossOrigin
    @GetMapping("/api/user/info/{id}")
    public ResponseEntity<UserDto> getUserById(HttpServletRequest request, @PathVariable long id) {
        User user = authUtilities.getUserFromRequest(request);

        User fetchedUser = userService.findUserById(id).orElseThrow(UserNotFoundException::new);


        if(fetchedUser.getId() == user.getId() || user.getRoles().contains(User.Rol.REVIEWER)) {
            return ResponseEntity.status(HttpStatus.OK).body(new UserDto(fetchedUser));
        }
        throw new UserUnauthorizedException();
    }

    @CrossOrigin
    @PutMapping("/api/user/info")
    public ResponseEntity<UserDto> putCurrentUser(HttpServletRequest request,
                                          @RequestBody UpdateUserRequest updateUserRequest) {
        User user = authUtilities.getUserFromRequest(request);

        return putUserById(request, user.getId(), updateUserRequest);
    }

    @CrossOrigin
    @PutMapping("/api/user/info/{id}")
    public ResponseEntity<UserDto> putUserById(HttpServletRequest request, @PathVariable long id, @RequestBody UpdateUserRequest updateUserRequest) {
        User user = authUtilities.getUserFromRequest(request);

        User fetchedUser = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        if(user.getId() == id || user.getRoles().contains(User.Rol.REVIEWER)) {

            if(Boolean.TRUE.equals(isUpdateValid(updateUserRequest.getEmail()))) {

                if(Boolean.FALSE.equals(isMailValid(updateUserRequest.getEmail()))) {
                    throw new UserBadRequestEmailNotValidException();
                }

                if(!fetchedUser.getEmail().equals(updateUserRequest.getEmail()) && userService.existsByEmail(updateUserRequest.getEmail())) {
                    throw new UserBadRequestEmailAlreadyExistsException();
                } else {
                    fetchedUser.setEmail(updateUserRequest.getEmail());
                }
            }

            if(Boolean.TRUE.equals(isUpdateValid(updateUserRequest.getUsername()))) {

                if(!fetchedUser.getUsername().equals(updateUserRequest.getUsername()) && userService.existsByUsername(updateUserRequest.getUsername())) {
                    throw new UserBadRequestUsernameAlreadyExistsException();
                } else {
                    fetchedUser.setUsername(updateUserRequest.getUsername());
                }
            }

            User updatedUser = updateUserDataAuthorized(updateUserRequest, user, fetchedUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(new UserDto(updatedUser));
        }
        throw new UserUnauthorizedException();
    }

    @CrossOrigin
    @PostMapping(value = "/api/user/login")
    public ResponseEntity<JsonWebTokenDto> createAuthenticationToken(@RequestBody JsonWebTokenRequest jsonWebTokenRequest, HttpServletRequest request) {
        User user = userService.findUserByUserName(jsonWebTokenRequest.getUsername()).orElseThrow(UserForbiddenException::new);

        if(Boolean.FALSE.equals(user.getConfirmed())) {
            throw new UserNotConfirmedException();
        }

        authUtilities.authenticate(jsonWebTokenRequest.getUsername(), jsonWebTokenRequest.getPassword());

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(jsonWebTokenRequest.getUsername());

        final String token = jsonWebTokenUtil.generateToken(userDetails);

        user.setLastIp(request.getRemoteAddr());
        userService.update(user);

        return ResponseEntity.status(HttpStatus.OK).body(new JsonWebTokenDto(token));
    }

    @CrossOrigin
    @PostMapping(value = "/api/user/confirm/{tokenValue}")
    public ResponseEntity confirmToken(@PathVariable String tokenValue) {
        Token token = tokenService.getTokenByValue(tokenValue).orElseThrow(TokenNotFoundException::new);

        User user = token.getUser();

        userService.confirmUser(user);

        tokenService.delete(token);

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