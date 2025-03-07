package dev.barahow.authentication_microservice.Service;

import dev.barahow.authentication_microservice.dao.UserEntity;
import dev.barahow.authentication_microservice.mapper.UserMapper;
import dev.barahow.authentication_microservice.repository.UserRepository;
import dev.barahow.core.dto.LockInfo;
import dev.barahow.core.dto.UserDTO;
import dev.barahow.core.exceptions.UserNotFoundException;
import dev.barahow.core.types.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        if(userDTO== null){
            throw new IllegalArgumentException("UserDTO cannot be null");
        }

        log.info("Saving new user {} to the database",userDTO.getEmail());;

        userDTO.setPasswordHash(passwordEncoder.encode(userDTO.getPasswordHash()));

        LockInfo lockInfo = new LockInfo(false,null);
        userDTO.setLocked(lockInfo);

        // map DTO to Entity
      UserEntity userEntity = userMapper.toEntity(userDTO);


        // save the entity in the Database
     UserEntity saveUserToDB =   userRepository.save(userEntity);

     log.info("saved user to database {}",saveUserToDB.toString());
     return userMapper.toDTO(saveUserToDB);

    }



    @Override
    public void addRoleToUser(String email) {
        if (email == null ) {
            throw new IllegalArgumentException("Email  cannot be null");
        }

        // find the user by email
        UserEntity userEntity= userRepository.findByEmailIgnoreCase(email);

        if(userEntity== null){
            throw new EntityNotFoundException("user with email " + email + "not found");
        }


             Set<Role> roles= new HashSet<>();
        roles.add(Role.CUSTOMER);
        userEntity.setRole(roles);


        userRepository.save(userEntity);




    }

    @Override
    public UserDTO createUser(UserDTO user) {

        hashPassword(user);

        UserEntity newUser  = userRepository.findByEmailIgnoreCase(user.getEmail());
        if(newUser!= null){
            throw new IllegalStateException("email already exist");
        }


        Set<Role> setRole = new HashSet<>();
        setRole.add(Role.CUSTOMER);
            user.setRole(setRole);
        LockInfo lockInfo= new LockInfo(false,null);
            user.setLocked(lockInfo);


            UserEntity userEntity= userMapper.toEntity(user);






log.info("Created a new user {}",userEntity);

    UserEntity savedNewUser = userRepository.save(userEntity);



    return userMapper.toDTO(savedNewUser);
    }

    @Override
    public UserDTO getUser(String email) {
        UserEntity userEntity = userRepository.findByEmailIgnoreCase(email);

        if(userEntity== null){
            throw new UserNotFoundException("no User found with that email " +  email);
        }

        return userMapper.toDTO(userEntity);

    }

    @Override
    public Page<UserDTO> getUsers(Pageable pageable) {

        Page<UserEntity> userEntities = userRepository.findAll(pageable);


        Page<UserDTO> userDTOs = userEntities.map(userMapper::toDTO);

        return userDTOs;
    }

    @Override
    public void updateUser(UUID id, UserDTO userDTO) {

    }


    @Override
    public UserDTO getUserById(UUID id) {
        Optional<UserEntity> userEntity= userRepository.findById(id);
        if (userEntity.isEmpty()){
            throw new UserNotFoundException("User not found with that id");
        }

        return userMapper.toDTO(userEntity.get());

    }

    @Override
    public void deleteUser(UUID id) {
        Optional<UserEntity> delete= userRepository.findById(id);

        if (delete.isEmpty()){
            throw new UserNotFoundException("user to delete does not exist");
        }

        userRepository.delete(delete.get());
    }


    private void hashPassword(UserDTO user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

    }
}
