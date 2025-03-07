package dev.barahow.authentication_microservice.filter;

import dev.barahow.authentication_microservice.Service.UserAuthenticationService;
import dev.barahow.core.dto.UserDTO;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.security.Permission;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {



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
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));


    }
return false;
    }


    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
