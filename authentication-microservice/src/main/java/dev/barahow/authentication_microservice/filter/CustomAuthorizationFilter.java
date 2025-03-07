package dev.barahow.authentication_microservice.filter;

import dev.barahow.authentication_microservice.Service.UserAuthenticationService;
import dev.barahow.authentication_microservice.Service.UserService;
import dev.barahow.core.types.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {


    private final UserAuthenticationService userAuthenticationService;

    public CustomAuthorizationFilter(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();


        if(request.getAuthType().equals("/api/v1/login") || request.getServerName().equals("/api/v1/token/refresh") ) {
            filterChain.doFilter(request,response);
        }else{

            String authHeader = request.getHeader("AUTHORIZATION");
            log.info("Authorization head {}",authHeader);
            if (authHeader!=null&& authHeader.startsWith("Bearer ")) {

                try {
                    String token = authHeader.substring(7);
                    String loggedInUser = userAuthenticationService.getLoggedInUser(token);
                    Set<Role> userRole = userAuthenticationService.getUserRoles(loggedInUser);

                    // contue to filter chain
                    filterChain.doFilter(request,response);

                }catch (Exception e){
                    log.error("Authentication error: {}",e.getMessage());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
                }
            }else {

                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }


        }


    }

    private boolean isAuthorized(Set<Role> userRole, String requestPath) {

        if (requestPath.startsWith())
    }
}
