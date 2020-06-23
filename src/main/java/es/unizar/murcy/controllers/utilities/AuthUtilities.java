package es.unizar.murcy.controllers.utilities;

import es.unizar.murcy.components.JsonWebTokenUtil;
import es.unizar.murcy.exceptions.user.UserNotFoundException;
import es.unizar.murcy.exceptions.user.UserUnauthorizedException;
import es.unizar.murcy.model.User;
import es.unizar.murcy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    public User getUserFromRequest(HttpServletRequest request) {
        final String authorization = request.getHeader("Authorization");

        final String username = jsonWebTokenUtil.getUserNameFromToken(authorization.substring(7));

        return userService.findUserByUserName(username).orElseThrow(UserUnauthorizedException::new);
    }

    public User getUserFromRequest(HttpServletRequest request, User.Rol rol, boolean canBeReviewer) {
        final String authorization = request.getHeader("Authorization");

        final String username = jsonWebTokenUtil.getUserNameFromToken(authorization.substring(7));

        Optional<User> user = userService.findUserByUserName(username);
        if (user.isPresent() && (user.get().getRoles().contains(rol) || (canBeReviewer && user.get().getRoles().contains(User.Rol.REVIEWER)))) {
            return user.get();
        }
        throw new UserUnauthorizedException();
    }

    public void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    public User filterUserAuthorized(User user, long id, User.Rol minRol) {
        if (user.getId() != id) {
            Optional<User.Rol> maxRol = getMaxRol(user);
            if (maxRol.isPresent() && maxRol.get().ordinal() >= minRol.ordinal()) {
                return userService.findUserById(id).orElseThrow(UserNotFoundException::new);
            }
            throw new UserUnauthorizedException();
        } else {
            return userService.findUserById(id).orElseThrow(UserNotFoundException::new);
        }
    }

    private Optional<User.Rol> getMaxRol(User user) {
        return user.getRoles().stream().max(Comparator.comparing(User.Rol::ordinal));
    }

}
