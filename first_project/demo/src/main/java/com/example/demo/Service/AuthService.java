package com.example.demo.Service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.session.SessionConcurrencyDsl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Model.User;
import com.example.demo.Repository.SessionRepository;
import com.example.demo.Repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;



    public void login(String login, String password, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Optional<User> userOptional = userRepository.findByLogin(login);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
    
            // Ověření hesla
            if (passwordEncoder.matches(password, user.getPassword())) {
                Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login, password)
                );
    
                if (authentication.isAuthenticated()) {
                    HttpSession session = request.getSession(true);
                    String sessionId = session.getId();
                    System.out.println("Session created with ID: " + sessionId);
    
                    // Uložení session do tabulky sessions
                    sessionRepository.createSession(sessionId, user.getId(), request.getRemoteAddr(), request.getHeader("User-Agent"));
                    
                    session.setAttribute("user", user);
    
                    response.setStatus(HttpServletResponse.SC_OK);
                    return;
                }
            }
        }
        throw new Exception("Invalid login or password");
    }
    

public void logout(HttpServletRequest request, HttpServletResponse response) {
    HttpSession session = request.getSession(false); // Získá aktuální session, pokud existuje
    if (session != null) {
        String sessionId = session.getId();
        sessionRepository.deleteSession(sessionId);  
        session.invalidate(); // Zneplatnění session při odhlášení
    }

    // Odstranění cookie s ID session
    Cookie cookie = new Cookie("JSESSIONID", null);
    cookie.setPath("/"); // Ujisti se, že cesta je stejná jako při vytváření cookie
    cookie.setHttpOnly(true);
    cookie.setSecure(true); // Pouze pokud používáš HTTPS
    cookie.setMaxAge(0); // Okamžité vypršení cookie
    response.addCookie(cookie);

    response.setStatus(HttpServletResponse.SC_OK);
}


    public Optional<User> findByLogin(String name) {
        return userRepository.findByLogin(name);
    }


    public boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean loggedIn = session != null && session.getAttribute("user") != null;
        System.out.println("Session: " + session);
        System.out.println("LoggedIn: " + loggedIn);
        return loggedIn;
    }
}

