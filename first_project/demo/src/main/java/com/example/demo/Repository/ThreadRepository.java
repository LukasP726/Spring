package com.example.demo.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.Thread;

@Repository
public class ThreadRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Definice RowMapperu pro Thread
    private final RowMapper<Thread> ROW_MAPPER = (rs, rowNum) -> {
        Thread thread = new Thread();
        thread.setId(rs.getInt("id"));
        thread.setName(rs.getString("name"));
        thread.setUserId(rs.getInt("idUser"));
        thread.setCreatedAt(rs.getTimestamp("createdAt"));
        return thread;
    };

    public List<Thread> findByNameContaining(String name) {
        String sql = "SELECT * FROM threads WHERE name LIKE ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, name);
    }

    public void createThread(Thread thread) {
        String sql = "INSERT INTO threads (name, idUser, createdAt) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, thread.getName(), thread.getId(), new Timestamp(System.currentTimeMillis()));
    }

    public List<Thread> getAllThreads() {
        String sql = "SELECT * FROM threads";
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

public Optional<Thread> getThreadById(Integer id) {
    String sql = "SELECT * FROM threads WHERE id = ?";
    try {
        Thread thread = jdbcTemplate.queryForObject(sql, ROW_MAPPER, id);
        return Optional.ofNullable(thread);
    } catch (EmptyResultDataAccessException e) {
        return Optional.empty();
    }
}
}