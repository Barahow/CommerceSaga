package dev.barahow.authentication_microservice.Service;

import dev.barahow.core.dto.UserDTO;
import dev.barahow.core.types.Role;

import java.util.Set;

public interface UserAuthenticationService {

    String getLoggedInUser(String authorizationToken);
    UserDTO getUserByEmail(String email);

    UserDTO getUserISLocked(boolean locked);

    void incrementFailedLoginAttempt(String email);

    void resetUserLock(String email);
    String login(String email, String password);
    Set<Role> getUserRoles(String loggedInUser);
}
