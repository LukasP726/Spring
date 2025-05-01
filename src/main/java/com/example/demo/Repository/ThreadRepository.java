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


    
    
    /**
     * Vyhledá všechna vlákna, jejichž název obsahuje zadaný výraz (LIKE),
     * a jejichž autor není zabanován.
     * JOINuje na tabulku users kvůli kontrole sloupce isBanned.
     */
    public List<Thread> findByNameContaining(String name) {
        String sql = "SELECT t.* FROM threads t " +
                    "JOIN users u ON t.id_user = u.id " +
                    "WHERE t.name LIKE ? AND u.isBanned = false";

        String searchTerm = "%" + name + "%";

        return jdbcTemplate.query(sql, new Object[]{searchTerm}, ROW_MAPPER);
    }

    /**
     * Vytvoří nové vlákno v tabulce `threads`.
     * Datum a čas vytvoření nastaví na aktuální systémový čas.
     */
    public void createThread(Thread thread) {
        String sql = "INSERT INTO threads (name, id_user, created_at) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, thread.getName(), thread.getIdUser(), new Timestamp(System.currentTimeMillis()));
    }

    /**
     * Vrací seznam všech vláken z tabulky `threads`.
     */
    public List<Thread> getAllThreads() {
        String sql = "SELECT * FROM threads";
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    /**
     * Vyhledá vlákno podle jeho ID.
     * Pokud se nenajde, vrací Optional.empty().
     */
    public Optional<Thread> getThreadById(Integer id) {
        String sql = "SELECT * FROM threads WHERE id = ?";
        try {
            Thread thread = jdbcTemplate.queryForObject(sql, ROW_MAPPER, id);
            return Optional.ofNullable(thread);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Vrací všechna vlákna založená daným uživatelem.
     */
    public List<Thread> findByUserId(Integer idUser) {
        String sql = "SELECT * FROM threads WHERE id_user = ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, idUser);
    }

    /**
     * Smaže vlákno podle jeho ID.
     * Vrací počet ovlivněných řádků.
     */
    public int deleteById(Integer id) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM threads WHERE id = ?", id);
        return rowsAffected;
    }

    /**
     * Smaže všechna vlákna vytvořená daným uživatelem.
     * Vrací počet ovlivněných řádků.
     */
    public int deleteByIdUser(Long idUser) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM threads WHERE id_user = ?", idUser);
        return rowsAffected;
    }

    /**
     * Vrací ID uživatele (vlastníka) vlákna na základě ID vlákna.
     */
    public Integer findOwnerByThreadId(int idThread) {
        String sql = "SELECT id_user FROM threads WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{idThread}, Integer.class);
    }


}
