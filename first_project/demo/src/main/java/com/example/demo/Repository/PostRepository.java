package com.example.demo.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.Post;
import com.example.demo.Model.Upload;
import com.example.demo.Model.User;

import java.sql.Timestamp;
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
        post.setIdUser(rs.getInt("idUser"));
        post.setIdThread(rs.getInt("idThread"));
        post.setCreatedAt(rs.getTimestamp("createdAt"));
        return post;
    };

    public List<Post> findByContentContaining(String content) {
        String sql = "SELECT * FROM Posts WHERE content LIKE ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, "%" + content + "%");
    }

    public List<Post> findByUserId(Long idUser) {
        String sql = "SELECT * FROM posts WHERE idUser = ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, idUser);
    }

    public Integer getIdUserById(Integer id) {
        String sql = "SELECT idUser FROM posts WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, id);
    }
//new Timestamp(System.currentTimeMillis())
    public void createPost(Post post) {
        String sql = "INSERT INTO posts (content, idUser, idThread, createdAt) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, post.getContent(), post.getIdUser(), post.getIdThread(), post.getCreatedAt());
    }

    public List<Post> findByThreadId(Integer idThread){
        String sql = "SELECT * FROM posts WHERE idThread = ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, idThread);
        
    }

    public Integer getLastInsertId() {
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
    }



    public Optional<Post> getPostById(Integer id) {
    String sql = "SELECT * FROM posts WHERE id = ?";
    try {
        Post post = jdbcTemplate.queryForObject(sql, ROW_MAPPER, id);
        return Optional.ofNullable(post);
    } catch (EmptyResultDataAccessException e) {
        return Optional.empty();
    }
}

    public int deleteByIdUser(Long idUser) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM posts WHERE idUser = ?", idUser);
        return rowsAffected;
    }

    public int deletePost(Long id){
        int rowsAffected = jdbcTemplate.update("DELETE FROM posts WHERE id = ?", id);
        return rowsAffected;
    }

    public int updatePost(int postId, String content) {
        /* 
        doplnit updateAt
        String sql = "UPDATE Posts SET content = ?, updatedAt = ? WHERE id = ?";
        return jdbcTemplate.update(sql, content, new Timestamp(System.currentTimeMillis()), postId);
        */
        String sql = "UPDATE Posts SET content = ? WHERE id = ?";
        return jdbcTemplate.update(sql, content, postId);
    }






}
