package com.revature.project2;

import com.revature.project2.models.DTOs.TokenDto;
import com.revature.project2.models.User;
import com.revature.project2.security.UserRoles;
import com.revature.project2.security.authentication.CustomUDM;
import com.revature.project2.security.authentication.CustomUserDetails;
import com.revature.project2.security.authentication.JWTAuthObj;
import com.revature.project2.security.utils.TokenProcessor;
import com.revature.project2.services.AuthenticationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class AuthentificationServiceTests {
    private TokenProcessor tokenProcessor;
    private CustomUDM customUDM;
    private PasswordEncoder passwordEncoder;
    private AuthenticationService authenticationService;

    @BeforeEach
    public void contextLoad(){
        tokenProcessor = Mockito.mock(TokenProcessor.class);
        customUDM = Mockito.mock(CustomUDM.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        authenticationService = new AuthenticationService(tokenProcessor, customUDM, passwordEncoder);
    }

    @Test
    public void test_login_valid(){
        User user = new User("User","passwordEncoded", "user@user.com","user_fn","user_ln","Employee");

        TokenDto tokenDto = new TokenDto("UserToken");
        ArgumentCaptor<Authentication> authenticationArgumentCaptor = ArgumentCaptor.forClass(Authentication.class);

        when(tokenProcessor.generateToken(authenticationArgumentCaptor.capture())).thenReturn(tokenDto);
        when(passwordEncoder.encode("passwordEncoded")).thenReturn("password");
        when(passwordEncoder.matches("password","password")).thenReturn(true);
        when(passwordEncoder.matches("password","passwordEncoded")).thenReturn(false);

        UserDetails userDetails = new CustomUserDetails(user, passwordEncoder);
        when(customUDM.loadUserByUsername("User")).thenReturn(userDetails);

        TokenDto returnToken = authenticationService.login("User", "password");
        Authentication authenticationUsed = authenticationArgumentCaptor.getValue();

        Assertions.assertTrue("UserToken".equals(returnToken.token()));
        Assertions.assertTrue(authenticationUsed.isAuthenticated());
        Assertions.assertTrue("User".equals(authenticationUsed.getPrincipal()));
    }

    @Test
    public void test_login_invalid(){
        User user = new User("User","passwordEncoded", "user@user.com","user_fn","user_ln","Employee");

        TokenDto tokenDto = new TokenDto("UserToken");
        ArgumentCaptor<Authentication> authenticationArgumentCaptor = ArgumentCaptor.forClass(Authentication.class);

        when(tokenProcessor.generateToken(authenticationArgumentCaptor.capture())).thenReturn(tokenDto);
        when(passwordEncoder.encode("passwordEncoded")).thenReturn("password");
        when(passwordEncoder.matches("password","password")).thenReturn(true);
        when(passwordEncoder.matches("password","passwordEncoded")).thenReturn(false);
        when(passwordEncoder.matches("notpassword","password")).thenReturn(false);
        when(passwordEncoder.matches("password","notpassword")).thenReturn(false);

        UserDetails userDetails = new CustomUserDetails(user, passwordEncoder);
        when(customUDM.loadUserByUsername("User")).thenReturn(userDetails);

        UsernameNotFoundException ex = Assertions.assertThrows(UsernameNotFoundException.class, ()->authenticationService.login("User", "notpassword"));
    }

    @Test
    public void test_getAuthenticatedUser_valid_employee(){
        User user = new User("User","passwordEncoded", "user@user.com","user_fn","user_ln","ROLE_EMPLOYEE");
        when(passwordEncoder.encode("passwordEncoded")).thenReturn("password");
        UserDetails userDetails = new CustomUserDetails(user, passwordEncoder);

        Authentication authentication = new JWTAuthObj("User", true, userDetails.getAuthorities() );

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        Optional<Map.Entry<String, List<UserRoles>>> returnObj = authenticationService.getAuthenticatedUser();
        Assertions.assertTrue(!returnObj.isEmpty());
        Map.Entry<String, List<UserRoles>> returnMapEntry = returnObj.get();
        Assertions.assertTrue("User".equals(returnMapEntry.getKey()));
        UserRoles returnUserRoles = returnMapEntry.getValue().get(0);
        Assertions.assertTrue(returnUserRoles.toString().contains("EMPLOYEE"));
    }

    @Test
    public void test_getAuthenticatedUser_valid_manager(){
        User user = new User("User","passwordEncoded", "user@user.com","user_fn","user_ln","ROLE_MANAGER");
        when(passwordEncoder.encode("passwordEncoded")).thenReturn("password");
        UserDetails userDetails = new CustomUserDetails(user, passwordEncoder);

        Authentication authentication = new JWTAuthObj("User", true, userDetails.getAuthorities() );

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        Optional<Map.Entry<String, List<UserRoles>>> returnObj = authenticationService.getAuthenticatedUser();
        Assertions.assertTrue(!returnObj.isEmpty());
        Map.Entry<String, List<UserRoles>> returnMapEntry = returnObj.get();
        Assertions.assertTrue("User".equals(returnMapEntry.getKey()));
        UserRoles returnUserRoles = returnMapEntry.getValue().get(0);
        Assertions.assertTrue(returnUserRoles.toString().contains("MANAGER"));
    }

    @Test
    public void test_getAuthenticatedUser_invalid_nullContext(){
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);
        Optional<Map.Entry<String, List<UserRoles>>> returnObj = authenticationService.getAuthenticatedUser();
        Assertions.assertTrue(returnObj.isEmpty());
    }

    @Test
    public void test_getAuthenticatedUser_invalid_notAuthenticated(){
        User user = new User("User","passwordEncoded", "user@user.com","user_fn","user_ln","ROLE_EMPLOYEE");
        when(passwordEncoder.encode("passwordEncoded")).thenReturn("password");
        UserDetails userDetails = new CustomUserDetails(user, passwordEncoder);

        Authentication authentication = new JWTAuthObj("User", false, userDetails.getAuthorities() );

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        Optional<Map.Entry<String, List<UserRoles>>> returnObj = authenticationService.getAuthenticatedUser();
        Assertions.assertTrue(returnObj.isEmpty());
    }

    @Test
    public void test_getAuthenticatedUser_invalid_anonymous(){
        User user = new User("User","passwordEncoded", "user@user.com","user_fn","user_ln","ROLE_EMPLOYEE");
        when(passwordEncoder.encode("passwordEncoded")).thenReturn("password");
        UserDetails userDetails = new CustomUserDetails(user, passwordEncoder);

        Authentication authentication = new JWTAuthObj("anonymousUser", true, userDetails.getAuthorities() );

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        Optional<Map.Entry<String, List<UserRoles>>> returnObj = authenticationService.getAuthenticatedUser();
        Assertions.assertTrue(returnObj.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({"ROLE_EMPLOYEE, ROLE_EMPLOYEE, true",
            "ROLE_EMPLOYEE, ROLE_MANAGER, false",
            "ROLE_MANAGER, ROLE_EMPLOYEE, true",
            "ROLE_MANAGER, ROLE_MANAGER, true"})
    public void test_validateCurrentUserRole(String userRole, String authRole, String resultString){
        boolean result = Boolean.valueOf(resultString);

        User user = new User("User","passwordEncoded", "user@user.com","user_fn","user_ln",userRole);
        when(passwordEncoder.encode("passwordEncoded")).thenReturn("password");
        UserDetails userDetails = new CustomUserDetails(user, passwordEncoder);

        Authentication authentication = new JWTAuthObj("User", true, userDetails.getAuthorities() );

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        Assertions.assertTrue(authenticationService.validateCurrentUserRole(authRole)==result);


    }

}
