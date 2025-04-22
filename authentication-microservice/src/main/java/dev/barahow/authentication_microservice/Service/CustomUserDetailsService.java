package dev.barahow.authentication_microservice.Service;

import dev.barahow.authentication_microservice.dao.UserEntity;
import dev.barahow.authentication_microservice.repository.UserRepository;
import dev.barahow.authentication_microservice.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       UserEntity userEntity = userRepository.findByEmailIgnoreCase(email);
        if (userEntity==null) {
            throw new UsernameNotFoundException("user not found with that email");
        }


        // conver userEntity toa  userDetails instance
        return new CustomUserDetails(userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getId(),
                userEntity.getRole());


    }
}
