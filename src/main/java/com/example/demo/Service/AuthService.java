package com.example.demo.Service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.session.SessionConcurrencyDsl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Model.Session;
import com.example.demo.Model.User;
import com.example.demo.Repository.SessionRepository;
import com.example.demo.Repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
//import com.example.demo.Config.Md5PasswordEncoder;

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
    /* 
    @Autowired
    private Md5PasswordEncoder passwordEncoder;
*/ 


    public void login(String login, String password, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Optional<User> userOptional = userRepository.findByLogin(login);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Ověření hesla
            //System.out.println(user.getPassword());
            if (passwordEncoder.matches(password, user.getPassword())) {
                Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login, password)
                );
                
                if (authentication.isAuthenticated()) {
                    HttpSession session = request.getSession(true);
                    String sessionId = session.getId();
                    System.out.println("Session created with ID: " + sessionId);
    
                    // Nastavení maximální doby nečinnosti pro session (např. 30 minut)
                    int maxInactiveInterval = 30 * 60; // 30 minut v sekundách
                    session.setMaxInactiveInterval(maxInactiveInterval);
    
                    // Výpočet data a času vypršení
                    Date expiresAt = new Date(System.currentTimeMillis() + maxInactiveInterval * 1000L);
    
                    // Opakované pokusy o vytvoření session
                    boolean sessionCreated = false;
                    int retries = 3;  // Maximální počet pokusů
                    while (!sessionCreated && retries > 0) {
                        try {
                            // Pokus o uložení session do tabulky sessions
                            sessionRepository.createSession(sessionId, user.getId(), request.getRemoteAddr(), request.getHeader("User-Agent"), expiresAt);
                            sessionCreated = true;  // Pokud úspěšně vytvoříme session, nastavíme flag na true
                        } catch (DataIntegrityViolationException e) {
                            // Pokud dojde k chybě kvůli duplicitnímu session_id, vygenerujeme nové session_id
                            System.out.println("Duplicate session ID detected, generating a new one.");
                            session.invalidate();  // Zneplatnit starou session
                            session = request.getSession(true);  // Vytvořit novou session
                            sessionId = session.getId();
                            retries--;  // Snížit počet zbývajících pokusů
                        }
                    }
    
                    if (!sessionCreated) {
                        throw new Exception("Unable to create a unique session ID after multiple attempts.");
                    }
    
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





}

