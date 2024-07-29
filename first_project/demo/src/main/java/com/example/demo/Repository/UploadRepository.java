package com.example.demo.Repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.Upload;

@Repository
public class UploadRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Upload> findByFilenameContaining(String filename) {
        String sql = "SELECT * FROM Uploads WHERE filename LIKE ?";
        return jdbcTemplate.query(sql, new Object[]{"%" + filename + "%"}, new BeanPropertyRowMapper<>(Upload.class));
    }

    public List<Upload> findByUserId(Long userId) {
        String sql = "SELECT * FROM Uploads WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, new BeanPropertyRowMapper<>(Upload.class));
    }

    public void createUpload(Upload upload) {
        String sql = "INSERT INTO Uploads (filename, user_id, post_id, created_at) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, upload.getFilename(), upload.getUserId(), upload.getPostId(), new Timestamp(System.currentTimeMillis()));
    }

    // Další metody pro CRUD operace
}

