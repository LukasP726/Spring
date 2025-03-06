package com.example.demo.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Post;
import com.example.demo.model.Thread;

@Repository
public class ThreadRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Definice RowMapperu pro Thread
    private final RowMapper<Thread> ROW_MAPPER = (rs, rowNum) -> {
        Thread thread = new Thread();
        thread.setId(rs.getInt("id"));
        thread.setName(rs.getString("name"));
        thread.setIdUser(rs.getInt("id_user"));
        thread.setCreatedAt(rs.getTimestamp("created_at"));
        return thread;
    };
/* 
    public List<Thread> findByNameContaining(String name) {
        String sql = "SELECT * FROM threads WHERE name LIKE ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, "%" +name + "%" );
    }
    */

    
    
    public List<Thread> findByNameContaining(String name) {
        // SQL dotaz s JOIN na tabulku Users a kontrolou isBanned
        /* 
        String sql = "SELECT t.* FROM threads t " +
                     "JOIN users u ON t.id_user = u.id " +
                     "WHERE t.name LIKE ? AND u.isBanned = false";
                     */
                    String sql = "SELECT t.* FROM threads t " +
                    "JOIN users u ON t.id_user = u.id " +
                    "WHERE t.name LIKE '"+ name +"' AND u.isBanned = false";
    
        // Přidání zástupných znaků procent k termínu vyhledávání
        //String searchTerm = "%" + name + "%";
    
        // Použití parametrizovaného dotazu s argumentem searchTerm
        //return jdbcTemplate.query(sql, new Object[]{searchTerm}, ROW_MAPPER);
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }
    


    
    public void createThread(Thread thread) {
        String sql = "INSERT INTO threads (name, id_user, created_at) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, thread.getName(), thread.getIdUser(), new Timestamp(System.currentTimeMillis()));
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



        public List<Thread> findByUserId(Integer idUser) {
        String sql = "SELECT * FROM threads WHERE id_user = ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, idUser);
    }


    public int deleteById(Integer id) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM threads WHERE id = ?", id);
        return rowsAffected;
    }

    public int deleteByIdUser(Long idUser) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM threads WHERE id_user = ?", idUser);
        return rowsAffected;
    }

    public Integer findOwnerByThreadId(int idThread) {
        String sql = "SELECT id_user FROM threads WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{idThread}, Integer.class);
    }

}
