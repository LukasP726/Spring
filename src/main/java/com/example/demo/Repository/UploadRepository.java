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

    /**
     * Vyhledá všechny soubory, jejichž název obsahuje zadaný výraz (LIKE),
     * a uživatel, který soubor nahrál, není zabanován.
     * JOINuje na tabulku `users` kvůli kontrole sloupce `isBanned`.
     */
    public List<Upload> findByFilenameContaining(String filename) {
        String sql = "SELECT u.* FROM uploads u " +
                    "JOIN users usr ON u.id_user = usr.id " +
                    "WHERE u.filename LIKE ? AND usr.isBanned = false";

        String searchTerm = "%" + filename + "%";
        
        return jdbcTemplate.query(sql, new Object[]{searchTerm}, new BeanPropertyRowMapper<>(Upload.class));
    }

    /**
     * Vrátí všechny soubory nahrané konkrétním uživatelem podle jeho ID.
     */
    public List<Upload> findByUserId(Long userId) {
        String sql = "SELECT * FROM Uploads WHERE id_user = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, new BeanPropertyRowMapper<>(Upload.class));
    }

    /**
     * Vloží nový soubor do tabulky `uploads` s uvedením názvu souboru, ID uživatele,
     * ID příspěvku, a aktuálního času vytvoření.
     */
    public void createUpload(Upload upload) {
        String sql = "INSERT INTO Uploads (filename, id_user, id_post, created_at) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, upload.getFilename(), upload.getIdUser(), upload.getIdPost(), new Timestamp(System.currentTimeMillis()));
    }

    /**
     * Vyhledá všechny soubory, které jsou připojeny k danému příspěvku podle ID příspěvku.
     */
    public List<Upload> findByPostId(Long idPost) {
        String sql = "SELECT * FROM uploads WHERE id_post = ? ";
        return jdbcTemplate.query(sql, rowMapper, idPost);
    }

    /**
     * Vyhledá soubor podle jeho ID.
     * Vrací Optional, protože soubor nemusí existovat.
     */
    public Optional<Upload> findById(Long idUpload) {
        String sql = "SELECT * FROM uploads WHERE id = ?";
        return jdbcTemplate.query(sql, rowMapper, idUpload)
                .stream()
                .findFirst();
    }

    /**
     * Smaže všechny soubory, které byly nahrány daným uživatelem.
     * Vrací počet ovlivněných řádků.
     */
    public int deleteByIdUser(Long idUser) {
        return jdbcTemplate.update("DELETE FROM uploads WHERE id_user = ?", idUser);
    }

    /**
     * Smaže všechny soubory, které jsou připojeny k danému příspěvku podle jeho ID.
     * Vrací počet ovlivněných řádků.
     */
    public int deleteByIdPost(Long idPost){
        return jdbcTemplate.update("DELETE FROM uploads WHERE id_post = ?", idPost);
    }

    /**
     * Vrací název souboru připojeného k danému příspěvku podle jeho ID.
     * Pokud soubor neexistuje, vrátí null.
     */
    public String getFileNameByPostId(Long idPost) {
        try {
            String sql = "SELECT filename FROM uploads WHERE id_post = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{idPost}, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Vrací seznam tří nejnovějších obrázků (s příponami .jpg, .jpeg, .png, .gif),
     * seřazených podle data vytvoření v sestupném pořadí.
     */
    public List<Upload> findTop3ImagesOrderByCreatedAtDesc() {
        String sql = "SELECT * FROM uploads WHERE filename LIKE '%.jpg' OR filename LIKE '%.jpeg' OR filename LIKE '%.png' OR filename LIKE '%.gif' ORDER BY created_at DESC LIMIT 3";

        return jdbcTemplate.query(sql, rowMapper);
    }

}

