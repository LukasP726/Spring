package com.example.demo.Service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService customUserDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    public String login(String login, String password) throws Exception {
        Optional<User> userOptional = userRepository.findByLogin(login);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Authentication process
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(login, password)
                );

                if (authentication.isAuthenticated()) {
                    // Generate a JWT token
                    return generateToken(login);
                }
            }
        }
        throw new Exception("Invalid login or password");
    }
    
    private String generateToken(String username) {
        return Jwts.builder()
            .setSubject(username) // Set the subject of the token, typically the user
            .setIssuedAt(new Date()) // Set the issue date of the token
            .setExpiration(new Date(System.currentTimeMillis() + 864_000_000)) // Set the expiration date (e.g., 10 days)
            .signWith(SignatureAlgorithm.HS256, secretKey.getBytes()) // Sign the token with the secret key
            .compact(); // Create the JWT token
    }

    public Optional<User> findByLogin(String name) {
        return userRepository.findByLogin(name);
    }
}
