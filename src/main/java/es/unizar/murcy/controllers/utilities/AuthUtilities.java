package es.unizar.murcy.controllers.utilities;

import es.unizar.murcy.components.JsonWebTokenUtil;
import es.unizar.murcy.exceptions.user.UserNotFoundException;
import es.unizar.murcy.exceptions.user.UserUnauthorizedException;
import es.unizar.murcy.model.User;
import es.unizar.murcy.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.Optional;

@Component
public class AuthUtilities {

    JsonWebTokenUtil jsonWebTokenUtil;
    UserService userService;
    AuthenticationManager authenticationManager;

    public AuthUtilities(JsonWebTokenUtil jsonWebTokenUtil, UserService userService,
                         AuthenticationManager authenticationManager) {
        this.jsonWebTokenUtil = jsonWebTokenUtil;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    public User newUserMiddlewareCheck(HttpServletRequest request, User.Rol minRol) {
        final String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            return null;
        }
        final String username = jsonWebTokenUtil.getUserNameFromToken(authorization.substring(7));
        Optional<User> user = userService.findUserByUserName(username);

        if(minRol == User.Rol.UNLOGGED) {
            return user.orElseGet(() -> null);
        } else {
            if(user.isPresent()) {
                Optional<User.Rol> maxRol = getMaxRol(user.get());
                if (maxRol.isPresent() && maxRol.get().ordinal() >= minRol.ordinal()) {
                    return user.get();
                }
            }
            throw new UserUnauthorizedException();
        }
    }

    public boolean hasPermission(User requester, User.Rol minRol) {
        Optional<User.Rol> maxRol = getMaxRol(requester);
        return maxRol.filter(rol -> hasPermission(rol, minRol)).isPresent();
    }

    public boolean hasPermission(User.Rol requesterRol, User.Rol minRol) {
        return requesterRol.ordinal() >= minRol.ordinal();
    }

    public void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    public User filterUserAuthorized(User requester, long id, User.Rol minRol) {
        if (requester.getId() != id) {
            Optional<User.Rol> maxRol = getMaxRol(requester);
            if (maxRol.isPresent() && maxRol.get().ordinal() >= minRol.ordinal()) {
                return userService.findUserById(id).orElseThrow(UserNotFoundException::new);
            }
            throw new UserUnauthorizedException();
        } else {
            return userService.findUserById(id).orElseThrow(UserNotFoundException::new);
        }
    }

    public User filterUserAuthorized(User requester, User user, User.Rol minRol) {
        if (requester.getId() != user.getId()) {
            Optional<User.Rol> maxRol = getMaxRol(requester);
            if (maxRol.isPresent() && maxRol.get().ordinal() >= minRol.ordinal()) {
                return user;
            }
            throw new UserUnauthorizedException();
        } else {
            return user;
        }
    }

    private Optional<User.Rol> getMaxRol(User user) {
        return user.getRoles().stream().max(Comparator.comparing(User.Rol::ordinal));
    }

}
