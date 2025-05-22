package support.ticket_platform.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import support.ticket_platform.model.User;
import support.ticket_platform.repository.UserRepository;
import support.ticket_platform.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new RuntimeException("User non trovato"));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return userRepo.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepo.deleteById(id);
    }

    @Override
    public List<User> findByDisponibile(boolean disponibile) {
        return userRepo.findByDisponibile(disponibile);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }
}