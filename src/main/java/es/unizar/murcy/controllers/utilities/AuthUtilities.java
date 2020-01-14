package es.unizar.murcy.controllers.utilities;

import es.unizar.murcy.components.JsonWebTokenUtil;
import es.unizar.murcy.exceptions.user.UserUnauthorizedException;
import es.unizar.murcy.model.User;
import es.unizar.murcy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
public class AuthUtilities {

    @Autowired
    JsonWebTokenUtil jsonWebTokenUtil;

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    public Optional<User> getUserFromRequest(HttpServletRequest request) {
        final String authorization = request.getHeader("Authorization");

        final String username = jsonWebTokenUtil.getUserNameFromToken(authorization.substring(7));

        return userService.findUserByUserName(username);
    }

    public User getUserFromRequest(HttpServletRequest request, User.Rol rol, boolean canBeReviewer) {
        final String authorization = request.getHeader("Authorization");

        final String username = jsonWebTokenUtil.getUserNameFromToken(authorization.substring(7));

        Optional<User> user = userService.findUserByUserName(username);
        if(user.isPresent() && (user.get().getRoles().contains(rol) || (canBeReviewer && user.get().getRoles().contains(User.Rol.REVIEWER)))) {
                return user.get();
        }
        throw new UserUnauthorizedException();
    }

    public void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

}
