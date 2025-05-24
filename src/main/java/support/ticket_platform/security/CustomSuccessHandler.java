package support.ticket_platform.security;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import support.ticket_platform.model.User;
import support.ticket_platform.service.UserService;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    public CustomSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String username = authentication.getName();
        Optional<User> optionalUser = userService.findByUsername(username);
        if (optionalUser.isEmpty()) {
            // Se non trovi l'utente, reindirizza a una pagina di errore o logout
            response.sendRedirect("/login?error");
            return;
        }

        User user = optionalUser.get();
        Long userId = user.getId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ADMIN")) {
                response.sendRedirect("/tickets");
                return;
            } else if (authority.getAuthority().equals("USER")) {
                response.sendRedirect("/user/" + userId);
                return;
            }
        }

        response.sendRedirect("/");
    }
}
