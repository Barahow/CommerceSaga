package dev.barahow.authentication_microservice.filter;

import dev.barahow.authentication_microservice.Service.UserAuthenticationService;
import dev.barahow.authentication_microservice.Service.UserService;
import dev.barahow.core.dto.UserDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.security.Permission;
import java.util.UUID;

@Component
@Log4j2
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final UserService userService;

    public CustomPermissionEvaluator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

        if(authentication==null || targetDomainObject==null){
            return false;
        }

        // check if the user can access their own account
        if (permission.equals("UPDATE")||permission.equals("DELETE")|| permission.equals("VIEW")) {
            String targetUserEmail = (targetDomainObject instanceof UserDTO)
                    ? ((UserDTO) targetDomainObject).getEmail() : targetDomainObject.toString();

            String loggedInUserEmail = authentication.getName();

            // if the user is trying to modify their own data or if they are ADMIN

            return targetUserEmail.equals(loggedInUserEmail) || authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN") ||
                    grantedAuthority.getAuthority().equals("ADMIN"));


        }
        return false;
    }


    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {

        log.info("Checking permission for targetId: " + targetId);
        log.info("Logged-in user: " + authentication.getName());

     if(authentication==null ||targetId==null|| !targetType.equals("UserDTO")||
             !(permission instanceof String) ) {
         return false;
     }

     //fetch the user by Id
        UUID userId = (UUID)  targetId;

     UserDTO userDTO= userService.getUserById(userId);

     log.info("fetched user email {}",userDTO.getEmail());
     String loggedInUserEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
     log.info("Permission check result:{}",userDTO.getEmail().equals(loggedInUserEmail));
return userDTO.getEmail().equalsIgnoreCase(loggedInUserEmail) || isAdmin;

    }
}

