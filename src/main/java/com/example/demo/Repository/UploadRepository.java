package com.example.demo.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Post;
import com.example.demo.model.Upload;

@Repository
public class UploadRepository {

    // RowMapper pro mapování výsledků na objekty Post
    private final RowMapper<Upload> rowMapper = (rs, rowNum) -> {
        Upload upload = new Upload();
        upload.setId(rs.getInt("id"));
        upload.setFilename(rs.getString("filename"));
        upload.setIdUser(rs.getInt("id_user"));
        upload.setIdPost(rs.getInt("id_post"));
        upload.setCreatedAt(rs.getTimestamp("created_at"));
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
        // SQL dotaz s JOIN na tabulku users a kontrolou isBanned
        String sql = "SELECT u.* FROM uploads u " +
                     "JOIN users usr ON u.id_user = usr.id " +
                     "WHERE u.filename LIKE ? AND usr.isBanned = false";
    
        // Přidání zástupných znaků procent k termínu vyhledávání
        String searchTerm = "%" + filename + "%";
    
        // Použití parametrizovaného dotazu s argumentem searchTerm
        return jdbcTemplate.query(sql, new Object[]{searchTerm}, new BeanPropertyRowMapper<>(Upload.class));
    }
    
    



    public List<Upload> findByUserId(Long userId) {
        String sql = "SELECT * FROM Uploads WHERE id_user = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, new BeanPropertyRowMapper<>(Upload.class));
    }
    




    public void createUpload(Upload upload) {
        String sql = "INSERT INTO Uploads (filename, id_user, id_post, created_at) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, upload.getFilename(), upload.getIdUser(), upload.getIdPost(), new Timestamp(System.currentTimeMillis()));
    }

    public List<Upload> findByPostId(Long idPost) {
        String sql = "SELECT * FROM uploads WHERE id_post = ? ";
        return jdbcTemplate.query(sql, rowMapper, idPost);
    }

    public Optional<Upload> findById(Long idUpload) {
        String sql = "SELECT * FROM uploads WHERE id = ?";
        return jdbcTemplate.query(sql, rowMapper, idUpload)
        .stream()
        .findFirst();

    }

    public int deleteByIdUser(Long idUser) {
        return jdbcTemplate.update("DELETE FROM uploads WHERE id_user = ?", idUser);
    }

    public int deleteByIdPost(Long idPost){
        return jdbcTemplate.update("DELETE FROM uploads WHERE id_post = ?", idPost);
    }

    public String getFileNameByPostId(Long idPost) {
        try {
            String sql = "SELECT filename FROM uploads WHERE id_post = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{idPost}, String.class);
        } catch (EmptyResultDataAccessException e) {
            // Záznam nebyl nalezen,
            return null;
        }
    }

    public List<Upload> findTop3ImagesOrderByCreatedAtDesc() {
        String sql = "SELECT * FROM uploads WHERE filename LIKE '%.jpg' OR filename LIKE '%.jpeg' OR filename LIKE '%.png' OR filename LIKE '%.gif' ORDER BY created_at DESC LIMIT 3";

        return jdbcTemplate.query(sql, rowMapper);
    }

    // Další metody pro CRUD operace
}

