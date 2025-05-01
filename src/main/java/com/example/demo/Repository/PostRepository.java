package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.demo.model.Post;
import com.example.demo.model.PostDTO;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper pro mapování výsledků na objekty Post
    private final RowMapper<Post> ROW_MAPPER = (rs, rowNum) -> {
        Post post = new Post();
        post.setId(rs.getInt("id"));
        post.setContent(rs.getString("content"));
        post.setIdUser(rs.getInt("id_user"));
        post.setIdThread(rs.getInt("id_thread"));
        post.setCreatedAt(rs.getTimestamp("created_at"));
        return post;
    };


    /**
     * Vyhledává příspěvky obsahující zadaný řetězec v obsahu (content),
     * pouze od nezabanovaných uživatelů.
     * POZOR: metoda je zranitelná vůči SQL injection kvůli přímému vložení řetězce do SQL.
     */
    public List<Post> findByContentContaining(String content) {
        // SQL dotaz s JOIN na tabulku Users a kontrolou isBanned      
        String sql = "SELECT p.* FROM Posts p " +
                    "JOIN Users u ON p.id_user = u.id " +
                    "WHERE p.content LIKE '"+ content +"' AND u.isBanned = false"; 
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    /**
     * Vrací seznam příspěvků od uživatele se zadaným ID.
     */
    public List<Post> findByUserId(Long idUser) {
        String sql = "SELECT * FROM posts WHERE id_user = ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, idUser);
    }

    /**
     * Vrací ID uživatele, který vytvořil příspěvek se zadaným ID příspěvku.
     */
    public Integer getIdUserById(Integer id) {
        String sql = "SELECT idUser FROM posts WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, id);
    }

    /**
     * Vytváří nový příspěvek v databázi se zadanými údaji.
     */
    public void createPost(Post post) {
        String sql = "INSERT INTO posts (content, id_user, id_thread, created_at) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, post.getContent(), post.getIdUser(), post.getIdThread(), post.getCreatedAt());
    }

    /**
     * Vrací seznam příspěvků v daném vláknu podle ID vlákna.
     */
    public List<Post> findByThreadId(Integer idThread){
        String sql = "SELECT * FROM posts WHERE id_thread = ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, idThread);
    }

    /**
     * Vrací DTO příspěvků ve vláknu, včetně loginu autora,
     * seřazené podle data vytvoření sestupně.
     */
    public List<PostDTO> findPostDTOsByThreadId(Integer idThread) {
        String sql = "SELECT p.id, p.content, p.created_at, u.login AS owner " +
                    "FROM posts p " +
                    "JOIN users u ON p.id_user = u.id " +
                    "WHERE p.id_thread = ? " +
                    "ORDER BY created_at DESC ";
        return jdbcTemplate.query(sql, new Object[]{idThread}, new BeanPropertyRowMapper<>(PostDTO.class));
    }



    /**
     * Vrací ID posledního vloženého záznamu v rámci aktuálního spojení (session).
     * Funguje pouze po INSERT operaci a závisí na databázi (MySQL).
     */
    public Integer getLastInsertId() {
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
    }

    /**
     * Vyhledá příspěvek podle jeho ID.
     * Pokud není nalezen, vrací prázdný Optional místo vyhození výjimky.
     */
    public Optional<Post> getPostById(Integer id) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        try {
            Post post = jdbcTemplate.queryForObject(sql, ROW_MAPPER, id);
            return Optional.ofNullable(post);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Maže všechny příspěvky uživatele se zadaným ID uživatele.
     * Vrací počet ovlivněných řádků.
     */
    public int deleteByIdUser(Long idUser) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM posts WHERE id_user = ?", idUser);
        return rowsAffected;
    }

    /**
     * Maže příspěvek podle jeho ID.
     * Vrací počet ovlivněných řádků.
     */
    public int deletePost(Long id){
        int rowsAffected = jdbcTemplate.update("DELETE FROM posts WHERE id = ?", id);
        return rowsAffected;
    }

    /**
     * Aktualizuje obsah příspěvku se zadaným ID.
     * Vrací počet ovlivněných řádků.
     */
    public int updatePost(int postId, String content) {
        String sql = "UPDATE Posts SET content = ? WHERE id = ?";
        return jdbcTemplate.update(sql, content, postId);
    }










}
