package support.ticket_platform.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, CustomSuccessHandler customSuccessHandler) throws Exception {
    http.authorizeHttpRequests()
        .requestMatchers("/ticket/create", "/ticket/edit/**", "/delete/**", "/tickets").hasAuthority("ADMIN")
        .requestMatchers("/ticket/show/**", "/note/create").hasAnyAuthority("USER","ADMIN")
        .requestMatchers( "/user/**").hasAnyAuthority("USER")
        .requestMatchers("/api/**").permitAll()
        .requestMatchers("/**").authenticated()
        .and()
        .formLogin()
            .successHandler(customSuccessHandler) 
        .and()
        .logout();

    return http.build();
    }

    @Bean
    DatabaseUserDetailsService userDetailsService() {
        return new DatabaseUserDetailsService();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
        DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
        }
}
