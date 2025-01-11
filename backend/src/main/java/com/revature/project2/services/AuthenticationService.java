package com.revature.project2.services;


import com.revature.project2.models.DTOs.TokenDto;
import com.revature.project2.security.UserRoles;
import com.revature.project2.security.authentication.CustomUDM;
import com.revature.project2.security.authentication.JWTAuthObj;
import com.revature.project2.security.utils.TokenProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final TokenProcessor tokenProcessor;
    private final CustomUDM userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public TokenDto login(String username, String password){
        var userDetails = userDetailsManager.loadUserByUsername(username);
        if(passwordEncoder.matches(password,userDetails.getPassword())){
            JWTAuthObj authObj = new JWTAuthObj(userDetails.getUsername(),true,userDetails.getAuthorities());
            return tokenProcessor.generateToken(authObj);
        }
       throw new UsernameNotFoundException("Incorrect credentials");
    }

    public Optional<Map.Entry<String, List<UserRoles>>> getAuthenticatedUser(){
       var authObj =  SecurityContextHolder.getContext().getAuthentication();
       if(authObj==null || !authObj.isAuthenticated() || authObj.getPrincipal().equals("anonymousUser"))
           return Optional.empty();
       String username = authObj.getName();
       List<UserRoles> roles = authObj.getAuthorities()
                   .stream()
                   .map(authority->UserRoles.valueOf(authority.getAuthority()))
                   .toList();
       return Optional.of(new AbstractMap.SimpleEntry<>(username,roles));
    }

    // putting this code here as it should be a function of authentification
    //
    private boolean validateCurrentUserRole(String authorizedLevelRole){
        // if no user in the context, just check for provided roles
        var currentUser = getAuthenticatedUser();
        if(currentUser.isEmpty()){
            return true;
        }
        List<UserRoles> currentRoles = currentUser.get().getValue();

        var authRole = UserRoles.valueOf(authorizedLevelRole.trim());
        boolean isAuthRoleHigherThanAnyPresent = currentRoles.stream().allMatch(curRole->authRole.ordinal()>curRole.ordinal());
        if(isAuthRoleHigherThanAnyPresent){return false;}
        return true;
    }
}
