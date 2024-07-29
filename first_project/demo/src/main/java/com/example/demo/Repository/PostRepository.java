package com.example.demo.Repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.Post;

@Repository
public class PostRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Post> findByContentContaining(String content) {
        String sql = "SELECT * FROM Posts WHERE content LIKE ?";
        return jdbcTemplate.query(sql, new Object[]{"%" + content + "%"}, new BeanPropertyRowMapper<>(Post.class));
    }

    public List<Post> findByUserId(Long userId) {
        String sql = "SELECT * FROM Posts WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, new BeanPropertyRowMapper<>(Post.class));
    }

    public void createPost(Post post) {
        String sql = "INSERT INTO Posts (content, user_id, thread_id, created_at) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, post.getContent(), post.getUserId(), post.getThreadId(), new Timestamp(System.currentTimeMillis()));
    }

    // Další metody pro CRUD operace
}
