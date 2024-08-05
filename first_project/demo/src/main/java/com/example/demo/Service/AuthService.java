package com.example.demo.Service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Config.JwtUtil;
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
    private JwtUtil jwtUtil;

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
                    // Generate a JWT token using JwtUtil
                    return jwtUtil.generateToken((UserDetails) authentication.getPrincipal());
                }
            }
        }
        throw new Exception("Invalid login or password");
    }

    public Optional<User> findByLogin(String name) {
        return userRepository.findByLogin(name);
    }
}

