package com.revature.project2.security;

import com.revature.project2.security.authentication.JWTAuthFilter;
import com.revature.project2.security.authentication.JWTAuthProvider;
import com.revature.project2.security.exceptionhandlers.AccessDeniedHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig {
    private final JWTAuthFilter jwtAuthFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final JWTAuthProvider jwtAuthProvider;
    private final AccessDeniedHandler accessDeniedHandler;

    // using @Lazy injection to avoid circular dependency
    public SecurityConfig(@Lazy JWTAuthFilter jwtAuthFilter, AuthenticationEntryPoint authenticationEntryPoint, JWTAuthProvider jwtAuthProvider, AccessDeniedHandlerImpl accessDeniedHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jwtAuthProvider = jwtAuthProvider;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsManager udManager) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(config -> {
                    // Allow unauthenticated access to the /users endpoint
                    config.requestMatchers(HttpMethod.POST, "/users","/users/register").permitAll();

                    // All other requests must be authenticated
                    config.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e->{
                    e.accessDeniedHandler(accessDeniedHandler);
                    e.authenticationEntryPoint(authenticationEntryPoint);
                })
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless session management
                .httpBasic(Customizer.withDefaults())  // Basic HTTP authentication, if needed
                .csrf(csrf -> csrf.disable());  // Disabling CSRF for stateless applications

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(jwtAuthProvider)
                .build();
    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/**")
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                        .allowedOriginPatterns("*")
                        .allowCredentials(true)
                        .allowedHeaders("*");
            }
        };
    }
}
