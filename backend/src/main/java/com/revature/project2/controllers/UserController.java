package com.revature.project2.controllers;

import com.revature.project2.models.DTOs.TokenDto;
import com.revature.project2.services.AuthenticationService;
import com.revature.project2.services.UserManagementService;
import com.revature.project2.services.UserServices;
import lombok.RequiredArgsConstructor;
import com.revature.project2.models.DTOs.OutgoingUserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revature.project2.models.User;
import com.revature.project2.models.DTOs.IncomingLogin;


import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationService authService;
    private final UserManagementService userManagementService;
    private final UserServices userServices;

    @PostMapping
    public ResponseEntity<TokenDto> login(@RequestBody IncomingLogin user) {
        return ResponseEntity.ok(authService.login(user.username(), user.password()));
    }

    @PostMapping("/register")
    public ResponseEntity<TokenDto> register(@RequestBody User user) {
        userManagementService.createUser(user);
        return ResponseEntity.ok(authService.login(user.getUsername(),user.getPassword()));
    }

    @PatchMapping
    public ResponseEntity<Void> update(@RequestBody User user){
        userManagementService.updateUser(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<OutgoingUserDTO>> getAllUsers() {
        return ResponseEntity.ok(userServices.getAllUsers());
    }

    @GetMapping("/{username}")
    public ResponseEntity<OutgoingUserDTO> getUser(@PathVariable String username){
        User user = userServices.getUserByUsername(username);
        return  ResponseEntity.ok(new OutgoingUserDTO(user.getUserId(), user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole() ));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestBody String username){
        userManagementService.deleteUser(username);
        return ResponseEntity.ok().build();
    }

}
