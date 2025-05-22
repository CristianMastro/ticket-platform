package support.ticket_platform.service;

import java.util.List;
import java.util.Optional;

import support.ticket_platform.model.User;

public interface UserService {

    List<User> findAll();
    User findById(Long id);
    Optional<User> findByEmail(String email);
    User save(User user);
    void deleteById(Long id);
    List<User> findByDisponibile(boolean disponibile); // Operatori disponibili
    Optional<User> findByUsername(String username);
}
