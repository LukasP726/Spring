package com.example.demo.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.Post;
import com.example.demo.Model.Upload;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class PostRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper pro mapování výsledků na objekty Post
    private final RowMapper<Post> rowMapper = (rs, rowNum) -> {
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
        return jdbcTemplate.query(sql, rowMapper, "%" + content + "%");
    }

    public List<Post> findByUserId(Long idUser) {
        String sql = "SELECT * FROM posts WHERE idUser = ?";
        return jdbcTemplate.query(sql, rowMapper, idUser);
    }

    public Integer getIdUserById(Integer id) {
        String sql = "SELECT idUser FROM posts WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, id);
    }

    public void createPost(Post post) {
        String sql = "INSERT INTO posts (content, idUser, idThread, createdAt) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, post.getContent(), post.getIdUser(), post.getIdThread(), new Timestamp(System.currentTimeMillis()));
    }

    public List<Post> findByThreadId(Integer idThread){
        String sql = "SELECT * FROM posts WHERE idThread = ?";
        return jdbcTemplate.query(sql, rowMapper, idThread);
        
    }

    public Integer getLastInsertId() {
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
    }



}
