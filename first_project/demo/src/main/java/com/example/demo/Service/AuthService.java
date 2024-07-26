package com.example.demo.Service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
      @Autowired
    private PasswordEncoder passwordEncoder;

    public String login(String login, String password) throws Exception {
        Optional<User> userOptional = userRepository.findByLogin(login);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Generate a token (this is just a placeholder, you should use a real token generation method)
                return "dummy-token";
            }
        }
        throw new Exception("Invalid login or password");
    }

    public Optional<User> findByLogin(String name) {
        return userRepository.findByLogin(name);
    }
}
