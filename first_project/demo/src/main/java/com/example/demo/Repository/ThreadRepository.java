package com.example.demo.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;
import com.example.demo.Model.Thread;

@Repository
public class ThreadRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Thread> findByNameContaining(String name) {
        String sql = "SELECT * FROM Threads WHERE name LIKE ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, "%" + name + "%");
        
        List<Thread> threads = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Thread thread = new Thread();
            thread.setId((Long) row.get("id"));
            thread.setName((String) row.get("name"));
            thread.setUserId((Long) row.get("userId"));
            thread.setCreatedAt((Date) row.get("createdAt"));
            threads.add(thread);
        }
        return threads;
    }

    public void createThread(Thread thread) {
        String sql = "INSERT INTO threads (name, idUser, createdAt) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, thread.getName(), thread.getId(), new Timestamp(System.currentTimeMillis()));
    }

    // Další metody pro CRUD operace
}

