package dev.barahow.authentication_microservice.filter;

import dev.barahow.authentication_microservice.Service.CustomUserDetailsService;
import dev.barahow.authentication_microservice.Service.UserAuthenticationService;
import dev.barahow.authentication_microservice.Service.UserService;
import dev.barahow.authentication_microservice.component.AuthUtil;
import dev.barahow.authentication_microservice.security.CustomUserDetails;
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


    private final CustomUserDetailsService customUserDetailsService;
    private final AuthUtil authUtil;

    public CustomPermissionEvaluator(CustomUserDetailsService customUserDetailsService, AuthUtil authUtil) {
        this.customUserDetailsService = customUserDetailsService;
        this.authUtil = authUtil;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

        if(authentication==null || targetDomainObject==null){
            return false;
        }

        // check if the user can access their own account
        if (permission.equals("UPDATE")||permission.equals("POST")||permission.equals("DELETE")|| permission.equals("VIEW")) {

            String targetUserId = (targetDomainObject instanceof UserDTO) ? ((UserDTO) targetDomainObject).getId().toString(): targetDomainObject.toString();

           String loggedInUserId = (( CustomUserDetails) authentication.getPrincipal()).getId().toString();


            // if the user is trying to modify their own data or if they are ADMIN

            return targetUserId.equals(loggedInUserId) || authentication.getAuthorities().stream()
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





        // Extract user ID from CustomUserDetails or email if principal is a String
        String loggedInUserId;
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            loggedInUserId = ((CustomUserDetails) authentication.getPrincipal()).getId().toString();
        } else if (authentication.getPrincipal() instanceof String) {
            String email = (String) authentication.getPrincipal();
            CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);
            loggedInUserId = customUserDetails.getId().toString();
        } else {
            return false; // Unknown principal type
        }



        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
     log.info("Permission check result:{}",targetId.equals(UUID.fromString(loggedInUserId)));
return targetId.equals(UUID.fromString(loggedInUserId)) || isAdmin;

    }
}

