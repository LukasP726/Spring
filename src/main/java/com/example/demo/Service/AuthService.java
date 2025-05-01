package com.example.demo.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    
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
                    // Vytvoření HTTP session
                    HttpSession session = request.getSession(true);
                    String sessionId = session.getId();
                    System.out.println("Session created with ID: " + sessionId);

                    // Nastavení délky session
                    int maxInactiveInterval = 30 * 60; // 30 minut
                    session.setMaxInactiveInterval(maxInactiveInterval);


                    //  Uložit uživatele do session (pokud to potřebuješ)
                    session.setAttribute("user", user);

                    //  Uložit SecurityContext (tohle je KLÍČOVÉ pro Spring Security)
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    securityContext.setAuthentication(authentication);
                    SecurityContextHolder.setContext(securityContext);
                    session.setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        securityContext
                    );

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
        session.invalidate(); // Zneplatnění session při odhlášení
    }

    SecurityContextHolder.clearContext();

    // Odstranění cookie s ID session
    Cookie cookie = new Cookie("JSESSIONID", null);
    cookie.setPath("/"); // Ujištění, že cesta je stejná jako při vytváření cookie
    cookie.setHttpOnly(true);
    cookie.setMaxAge(0); // Okamžité vypršení cookie
    response.addCookie(cookie);

    response.setStatus(HttpServletResponse.SC_OK);
}


    public Optional<User> findByLogin(String name) {
        return userRepository.findByLogin(name);
    }







}

