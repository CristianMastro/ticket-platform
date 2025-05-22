package support.ticket_platform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import support.ticket_platform.model.User;


public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    // TROVA SOLO OPERATORI DISPONIBILI //
    List<User> findByDisponibile(boolean disponibile);
}
