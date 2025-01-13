package com.revature.project2.services;

import com.revature.project2.security.utils.ObjectUpdater;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.revature.project2.models.User;
import com.revature.project2.models.DTOs.OutgoingUserDTO;
import com.revature.project2.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServices {
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(UserServices.class);

    public UserServices(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        if(!isValidUser(user)) throw new IllegalArgumentException("Invalid user object");
        if(userRepository.findByUsername(user.getUsername()).isPresent())
            throw new IllegalArgumentException("Such user already exists");
        return userRepository.save(user);
    }

    public void update(User user) {
        var dbData = getUserByUsername(user.getUsername());
        ObjectUpdater.copyNonNullValues(dbData,user);
        user.setUserId(dbData.getUserId());
        if(!isValidUser(user))
            throw new IllegalArgumentException("Invalid user object");
        userRepository.save(user);
    }

    public boolean userExists(String username){
        return userRepository.findByUsername(username).isPresent();
    }

    public User getUserByUsername(String username){
        var dbData = userRepository.findByUsername(username);
        return dbData.orElseThrow(()->new UsernameNotFoundException("User Not Found"));
    }

    public void deleteByUsername(String username){
        var user = userRepository.findByUsername(username);
        if(user.isEmpty()) throw new UsernameNotFoundException("User Not Found");
        userRepository.delete(user.get());
    }

    private boolean isValidUser(User user){
        return
            (user.getFirstName() != null && !user.getFirstName().isBlank())
            && (user.getLastName() != null && !user.getLastName().isBlank())
            && (user.getUsername() != null && !user.getUsername().isBlank())
            && (user.getPassword() != null) // password length should be checked in UserManagementService. Here password is already encrypted
            && (user.getEmail() != null && !user.getEmail().isBlank());
    }

    public List<OutgoingUserDTO> getAllUsers() {
        logger.info("Retrieving all users");
        List<OutgoingUserDTO> outgoingUsers = new ArrayList<>();
        List<User> users = userRepository.findAll();
        for(User user : users){
            outgoingUsers.add(new OutgoingUserDTO(
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRole()
            ));
        }
        return outgoingUsers;
    }

}
