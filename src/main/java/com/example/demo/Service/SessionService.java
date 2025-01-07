package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.model.Role;
import com.example.demo.model.Session;
import com.example.demo.repository.SessionRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class SessionService {

    private static final double SESSION_EXTENSION_INTERVAL =  20 * 60 * 1000; // Interval 20 minut


     private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }



    public Session findBySessionId(String sessionId) {
        return sessionRepository.findBySessionId(sessionId);
    }


    public void extendSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String sessionId = session.getId();
            
            // Prodloužení session o dalších 30 minut
            Date newExpiresAt = new Date(System.currentTimeMillis() + 30 * 60 * 1000);
            sessionRepository.updateSessionExpiry(sessionId, newExpiresAt);
        }
    }


    
    public void checkAndExtendSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String sessionId = session.getId();
            
            // Získáme session z databáze
            Session dbSession = sessionRepository.findBySessionId(sessionId);
            if (dbSession != null) {
                long currentTime = System.currentTimeMillis();
                long lastAccessedTime = dbSession.getLastAccessed().getTime();

                // Pokud uplynulo více než 5 minut od posledního přístupu, aktualizujeme
                if (currentTime - lastAccessedTime > SESSION_EXTENSION_INTERVAL) {
                    Date newExpiresAt = new Date(currentTime + 60 * 60 * 1000); // Prodloužíme o 60 minut
                    sessionRepository.updateSessionExpiry(sessionId, newExpiresAt);
                    session.setAttribute("lastAccessed", new Date(currentTime)); // Aktualizujeme i v HttpSession
                }
            }
        }
    }

}
