package com.example.demo.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.Post;
import com.example.demo.Model.Upload;

@Repository
public class UploadRepository {

    // RowMapper pro mapování výsledků na objekty Post
    private final RowMapper<Upload> rowMapper = (rs, rowNum) -> {
        Upload upload = new Upload();
        upload.setId(rs.getInt("id"));
        upload.setFilename(rs.getString("filename"));
        upload.setIdUser(rs.getInt("idUser"));
        upload.setIdPost(rs.getInt("idPost"));
        upload.setCreatedAt(rs.getTimestamp("createdAt"));
        return upload;
    };


    @Autowired
    private JdbcTemplate jdbcTemplate;

    /* 

    public List<Upload> findByFilenameContaining(String filename) {
        String sql = "SELECT * FROM uploads WHERE filename LIKE ?";
        return jdbcTemplate.query(sql, new Object[]{"%" + filename + "%"}, new BeanPropertyRowMapper<>(Upload.class));
    }
    */
    
    public List<Upload> findByFilenameContaining(String filename) {
        // Přímo vkládáme uživatelský vstup do SQL dotazu
        String sql = "SELECT * FROM uploads WHERE filename LIKE '%" + filename + "%'";
        
        // Vykonání dotazu bez použití parametrizace
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Upload.class));
    }
    



    public List<Upload> findByUserId(Long userId) {
        String sql = "SELECT * FROM Uploads WHERE idUser = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, new BeanPropertyRowMapper<>(Upload.class));
    }
    




    public void createUpload(Upload upload) {
        String sql = "INSERT INTO Uploads (filename, idUser, idPost, createdAt) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, upload.getFilename(), upload.getIdUser(), upload.getIdPost(), new Timestamp(System.currentTimeMillis()));
    }

    public List<Upload> findByPostId(Long idPost) {
        String sql = "SELECT * FROM uploads WHERE idPost = ? ";
        return jdbcTemplate.query(sql, rowMapper, idPost);
    }

    public Optional<Upload> findById(Long idUpload) {
        String sql = "SELECT * FROM uploads WHERE id = ?";
        return jdbcTemplate.query(sql, rowMapper, idUpload)
        .stream()
        .findFirst();

    }

    public int deleteByIdUser(Long idUser) {
        return jdbcTemplate.update("DELETE FROM uploads WHERE idUser = ?", idUser);
    }

    public int deleteByIdPost(Long idPost){
        return jdbcTemplate.update("DELETE FROM uploads WHERE idPost = ?", idPost);
    }

    public String getFileNameByPostId(Long idPost) {
        try {
            String sql = "SELECT filename FROM uploads WHERE idPost = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{idPost}, String.class);
        } catch (EmptyResultDataAccessException e) {
            // Záznam nebyl nalezen,
            return null;
        }
    }

    public List<Upload> findTop3ImagesOrderByCreatedAtDesc() {
        String sql = "SELECT * FROM uploads WHERE filename LIKE '%.jpg' OR filename LIKE '%.jpeg' OR filename LIKE '%.png' OR filename LIKE '%.gif' ORDER BY createdAt DESC LIMIT 3";

        return jdbcTemplate.query(sql, rowMapper);
    }

    // Další metody pro CRUD operace
}

