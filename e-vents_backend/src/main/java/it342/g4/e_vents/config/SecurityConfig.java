package it342.g4.e_vents.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/signup", "/change-password", "/api/users/**", "/css/**", "/js/**", "/events/**", "/api/events/**").permitAll()
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(customAuthenticationSuccessHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
            );

        return http.build();
    }

    @Bean
    public org.springframework.security.web.authentication.AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            org.springframework.security.core.userdetails.User userDetails =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            String email = userDetails.getUsername();
            it342.g4.e_vents.model.User user = ((it342.g4.e_vents.repository.UserRepository)
                org.springframework.web.context.support.WebApplicationContextUtils
                    .getRequiredWebApplicationContext(request.getServletContext())
                    .getBean(it342.g4.e_vents.repository.UserRepository.class))
                .findByEmail(email).orElse(null);
            if (user != null && user.getRole() != null) {
                Long role = user.getRole().getRoleId();
                if (role == 1L) {
                    response.sendRedirect("/home");
                } else if (role == 2L) {
                    response.sendRedirect("/events/dashboard");
                } else {
                    response.sendRedirect("/events/dashboard");
                }
            } else {
                response.sendRedirect("/login");
            }
        };
    }
}