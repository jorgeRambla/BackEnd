package es.unizar.murcy.controllers;

import es.unizar.murcy.components.JsonWebTokenUtil;
import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.exceptions.token.TokenNotFoundException;
import es.unizar.murcy.exceptions.user.*;
import es.unizar.murcy.exceptions.user.validation.UserBadRequestEmailAlreadyExistsException;
import es.unizar.murcy.exceptions.user.validation.UserBadRequestEmailNotValidException;
import es.unizar.murcy.exceptions.user.validation.UserBadRequestUsernameAlreadyExistsException;
import es.unizar.murcy.model.Token;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.dto.JsonWebTokenDto;
import es.unizar.murcy.model.dto.UserDto;
import es.unizar.murcy.model.request.JsonWebTokenRequest;
import es.unizar.murcy.model.request.RegisterUserRequest;
import es.unizar.murcy.model.request.UpdateUserRequest;
import es.unizar.murcy.service.JwtUserDetailsService;
import es.unizar.murcy.service.MailService;
import es.unizar.murcy.service.TokenService;
import es.unizar.murcy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
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

    private final UserService userService;
    private final TokenService tokenService;
    private final MailService mailService;
    private final JsonWebTokenUtil jsonWebTokenUtil;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final AuthUtilities authUtilities;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, TokenService tokenService, MailService mailService,
                          JsonWebTokenUtil jsonWebTokenUtil, JwtUserDetailsService jwtUserDetailsService,
                          AuthUtilities authUtilities) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.mailService = mailService;
        this.jsonWebTokenUtil = jsonWebTokenUtil;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.authUtilities = authUtilities;
    }

    @CrossOrigin
    @PostMapping("/api/user")
    public ResponseEntity create(@RequestBody RegisterUserRequest registerUserRequest,
                                 HttpServletRequest request) {
        final User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.UNLOGGED);

        if(Boolean.FALSE.equals(registerUserRequest.isComplete())) {
            throw new UserBadRequestException();
        }

        if (Boolean.FALSE.equals(isMailValid(registerUserRequest.getEmail()))) {
            throw new UserBadRequestEmailNotValidException();
        }

        if (userService.existsByUsername(registerUserRequest.getUsername())){
            throw new UserBadRequestUsernameAlreadyExistsException();
        }

        if (userService.existsByEmail(registerUserRequest.getEmail())){
            throw new UserBadRequestEmailAlreadyExistsException();
        }

        registerUserRequest.setPassword(new BCryptPasswordEncoder().encode(registerUserRequest.getPassword()));
        User user = userService.create(registerUserRequest.toEntity());

        if(requester != null && !registerUserRequest.isSendMail() &&
                authUtilities.hasPermission(requester, User.Rol.ADMINISTRATOR)) {
            user.setConfirmed(true);
            userService.update(user);
        } else {
            Token token = tokenService.create(new Token(user, UUID.randomUUID().toString(), new Date(System.currentTimeMillis() + Token.DEFAULT_TOKEN_EXPIRATION_TIME)));
            try {
                mailService.sendTokenConfirmationMail(token.getTokenValue(), user.getEmail());
            } catch (MailException me) {
                logger.error("Cannot mail to {}, with exception: {}", user.getUsername(), me.getMessage());

                user.setConfirmed(true);
                userService.update(user);
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @CrossOrigin
    @GetMapping("/api/user/info")
    public ResponseEntity<UserDto> getCurrentUser(HttpServletRequest request) {
        final User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.USER);

        return ResponseEntity.status(HttpStatus.OK).body(new UserDto(requester));
    }

    @CrossOrigin
    @GetMapping("/api/user/info/{id}")
    public ResponseEntity<UserDto> getUserById(HttpServletRequest request, @PathVariable long id) {
        final User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.USER);

        final User searchedUser = authUtilities.filterUserAuthorized(requester, id, User.Rol.REVIEWER);

        return ResponseEntity.status(HttpStatus.OK).body(new UserDto(searchedUser));

    }

    @CrossOrigin
    @PutMapping("/api/user/info")
    public ResponseEntity<UserDto> putCurrentUser(HttpServletRequest request,
                                          @RequestBody UpdateUserRequest updateUserRequest) {
        final User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.USER);

        return putUserById(request, requester.getId(), updateUserRequest);
    }

    @CrossOrigin
    @PutMapping("/api/user/info/{id}")
    public ResponseEntity<UserDto> putUserById(HttpServletRequest request,
                                               @PathVariable long id,
                                               @RequestBody UpdateUserRequest updateUserRequest) {
        final User requester = authUtilities.newUserMiddlewareCheck(request, User.Rol.USER);

        User searchedUser = authUtilities.filterUserAuthorized(requester, id, User.Rol.REVIEWER);

        if(Boolean.TRUE.equals(isUpdateValid(updateUserRequest.getEmail()))) {

            if(Boolean.FALSE.equals(isMailValid(updateUserRequest.getEmail()))) {
                throw new UserBadRequestEmailNotValidException();
            }

            if(!searchedUser.getEmail().equals(updateUserRequest.getEmail()) && userService.existsByEmail(updateUserRequest.getEmail())) {
                throw new UserBadRequestEmailAlreadyExistsException();
            } else {
                searchedUser.setEmail(updateUserRequest.getEmail());
            }
        }

        if(Boolean.TRUE.equals(isUpdateValid(updateUserRequest.getUsername()))) {
            if(!searchedUser.getUsername().equals(updateUserRequest.getUsername()) && userService.existsByUsername(updateUserRequest.getUsername())) {
                throw new UserBadRequestUsernameAlreadyExistsException();
            } else {
                searchedUser.setUsername(updateUserRequest.getUsername());
            }
        }

        User updatedUser = updateUserDataAuthorized(updateUserRequest, requester, searchedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(new UserDto(updatedUser));
    }

    @CrossOrigin
    @PostMapping(value = "/api/user/login")
    public ResponseEntity<JsonWebTokenDto> createAuthenticationToken(@RequestBody JsonWebTokenRequest jsonWebTokenRequest, HttpServletRequest request) {
        logger.info("Handle request POST /user/login, username: {{}}", jsonWebTokenRequest.getUsername());

        User user = userService.findUserByUserName(jsonWebTokenRequest.getUsername()).orElseThrow(UserForbiddenException::new);

        if(Boolean.FALSE.equals(user.getConfirmed())) {
            throw new UserNotConfirmedException();
        }

        authUtilities.authenticate(jsonWebTokenRequest.getUsername(), jsonWebTokenRequest.getPassword());

        final UserDetails userDetails = jwtUserDetailsService
                .loadUserByUsername(jsonWebTokenRequest.getUsername());

        final String token = jsonWebTokenUtil.generateToken(userDetails);

        user.setLastIp(request.getRemoteAddr());
        userService.update(user);

        return ResponseEntity.status(HttpStatus.OK).body(new JsonWebTokenDto(token, Token.DEFAULT_TOKEN_EXPIRATION_TIME));
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

        if(authUtilities.hasPermission(user, User.Rol.ADMINISTRATOR) && updateUserRequest.getRol() != null) {
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