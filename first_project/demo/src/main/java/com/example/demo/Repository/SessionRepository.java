package com.example.demo.Repository;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SessionRepository {

    private final JdbcTemplate jdbcTemplate;

        public SessionRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
       
    }

    public void createSession(String sessionId, Long userId, String ipAddress, String userAgent) {
        String sql = "INSERT INTO sessions (session_id, user_id, ip_address, user_agent, created_at, last_accessed) VALUES (?, ?, ?, ?, NOW(), NOW())";
        jdbcTemplate.update(sql, sessionId, userId, ipAddress, userAgent);
    }

    public void deleteSession(String sessionId) {
        String sql = "DELETE FROM sessions WHERE session_id = ?";
        jdbcTemplate.update(sql, sessionId);
    }
    

}
