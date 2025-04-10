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
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Optional<UserEntity> userEntity = userRepository.findById(UUID.fromString(id));
        if (userEntity.isEmpty()) {
            throw new UsernameNotFoundException("user not found with that email");
        }


        // conver userEntity toa  userDetails instance
        return new CustomUserDetails(userEntity.get().getEmail(),
                userEntity.get().getPassword(),
                userEntity.get().getId(),
                userEntity.get().getRole());


    }
}
