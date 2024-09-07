package com.example.demo.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.Session;

@Repository
public class SessionRepository {

    private final JdbcTemplate jdbcTemplate;

        public SessionRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
       
    }
/* 
    public void createSession(String sessionId, Long userId, String ipAddress, String userAgent) {
        String sql = "INSERT INTO sessions (session_id, user_id, ip_address, user_agent, created_at, last_accessed) VALUES (?, ?, ?, ?, NOW(), NOW())";
        jdbcTemplate.update(sql, sessionId, userId, ipAddress, userAgent);
    }
        */

    public void createSession(String sessionId, Long userId, String ipAddress, String userAgent, Date expiresAt) {
        // Implementace SQL dotazu pro vložení do tabulky sessions s expires_at
        String sql = "INSERT INTO sessions (session_id, user_id, ip_address, user_agent, expires_at) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, sessionId, userId, ipAddress, userAgent, expiresAt);
    }

    public boolean isSessionExpired(String sessionId) {
        String sql = "SELECT COUNT(*) FROM sessions WHERE session_id = ? AND expires_at > NOW()";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, sessionId);
        return count == 0;  // Pokud je výsledkem 0, session je vypršelá
    }
    
    public void updateSessionExpiry(String sessionId, Date newExpiresAt) {
        String sql = "UPDATE sessions SET expires_at = ?, last_accessed = NOW() WHERE session_id = ?";
        jdbcTemplate.update(sql, newExpiresAt, sessionId);
    }

    public void deleteSession(String sessionId) {
        String sql = "DELETE FROM sessions WHERE session_id = ?";
        jdbcTemplate.update(sql, sessionId);
    }




    public Session findBySessionId(String sessionId) {
    String sql = "SELECT * FROM sessions WHERE session_id = ?";
    return jdbcTemplate.queryForObject(sql, new Object[]{sessionId}, new RowMapper<Session>() {
        @Override
        public Session mapRow(ResultSet rs, int rowNum) throws SQLException {
            Session session = new Session();
            session.setSessionId(rs.getString("session_id"));
            session.setUserId(rs.getLong("user_id"));
            session.setCreatedAt(rs.getTimestamp("created_at"));
            session.setLastAccessed(rs.getTimestamp("last_accessed"));
            session.setExpiresAt(rs.getTimestamp("expires_at"));
            session.setIpAddress(rs.getString("ip_address"));
            session.setUserAgent(rs.getString("user_agent"));
            return session;
        }
    });
}

    public void save(Session session) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }
    

}
