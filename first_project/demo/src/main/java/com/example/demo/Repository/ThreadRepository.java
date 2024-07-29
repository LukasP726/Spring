package com.example.demo.Repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ThreadRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Thread> findByNameContaining(String name) {
        String sql = "SELECT * FROM Threads WHERE name LIKE ?";
        return jdbcTemplate.query(sql, new Object[]{"%" + name + "%"}, new BeanPropertyRowMapper<>(Thread.class));
    }

    public void createThread(Thread thread) {
        String sql = "INSERT INTO Threads (name, user_id, created_at) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, thread.getName(), thread.getId(), new Timestamp(System.currentTimeMillis()));
    }

    // Další metody pro CRUD operace
}

