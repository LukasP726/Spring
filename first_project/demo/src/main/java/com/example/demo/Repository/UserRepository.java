package com.example.demo.Repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.User;

import jakarta.transaction.Transactional;

import javax.sql.DataSource;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public UserRepository(DataSource dataSource, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.passwordEncoder = passwordEncoder;
    }

    private static final RowMapper<User> ROW_MAPPER = new RowMapper<>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                rs.getLong("id"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("login"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getLong("idRole")
            );
        }
    };

    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users", ROW_MAPPER);
    }

    public Optional<User> getUserById(Long id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id = ?", ROW_MAPPER, id)
                .stream()
                .findFirst();
    }

    @Transactional
    public int saveUser(User user) {
        String rawPassword = user.getPassword();
        if(rawPassword == null) return -1;
        //String hashedPassword = passwordEncoder.encode(rawPassword);
        String hashedPassword = hashPassword(rawPassword, "MD5");

        int rowsAffected;
        Long userId;

        if (user.getId() == null) {
            rowsAffected = jdbcTemplate.update("INSERT INTO users (firstName, lastName, login, password, email, idRole) VALUES (?, ?, ?, ?, ?, ?)",
                    user.getFirstName(), user.getLastName(), user.getLogin(), hashedPassword, user.getEmail(), user.getIdRole());

            if (rowsAffected > 0) {
                // poslední vložené ID
                userId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
            } else {
                throw new RuntimeException("Failed to insert new user");
            }
        } else {
            rowsAffected = jdbcTemplate.update("UPDATE users SET firstName = ?, lastName = ?, login = ?, password = ?, email = ?, idRole = ? WHERE id = ?",
                    user.getFirstName(), user.getLastName(), user.getLogin(), hashedPassword, user.getEmail(), user.getIdRole(), user.getId());
            userId = user.getId();
        }
        

            

        return rowsAffected;
    }

    public int deleteUserById(Long id) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
        rowsAffected += jdbcTemplate.update("DELETE FROM hashed_passwords WHERE idUser = ?", id);
        return rowsAffected;
    }
/* 
    public List<User> findByNameContaining(String term) {
        String sql = "SELECT * FROM users WHERE firstName LIKE ? OR lastName LIKE ? OR login LIKE ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, "%" + term + "%", "%" + term + "%","%" + term + "%");
    }
*/
    public List<User> findByNameContaining(String term) {
        // Přímo vkládáme uživatelský vstup do SQL dotazu
        String sql = "SELECT * FROM users WHERE firstName LIKE '%" + term + "%' OR lastName LIKE '%" + term + "%' OR login LIKE '%" + term + "%'";
        
        // Vykonání dotazu bez použití parametrizace
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }
    

    public Optional<User> findByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";
        List<User> users = jdbcTemplate.query(sql, ROW_MAPPER, login);
        return users.stream().findFirst();
    }
 /* 
    private void saveHashedPasswords(Long userId, String password) throws NoSuchAlgorithmException, SQLException {
        String md5Hash = hashPassword(password, "MD5");
        String sha1Hash = hashPassword(password, "SHA-1");
        String sha256Hash = hashPassword(password, "SHA-256");

        // Uložení hashů do tabulky hashed_passwords
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            String insertSQL = "INSERT INTO hashed_passwords (idUser, password_md5, password_sha1, password_sha256) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                preparedStatement.setLong(1, userId);
                preparedStatement.setString(2, md5Hash);
                preparedStatement.setString(3, sha1Hash);
                preparedStatement.setString(4, sha256Hash);
                preparedStatement.executeUpdate();
                System.out.println("Hashed passwords saved for user ID: " + userId);
            }
        } catch (SQLException e) {
            System.err.println("Error saving hashed passwords: " + e.getMessage());
            throw e;
        }
    }
*/


    private String hashPassword(String password, String algorithm){
        try {
            

        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        byte[] hashedBytes = messageDigest.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }


    public List<User> getTopUsersByPostFrequency() {
        // Krok 1: Získání 5 nejčastějších idUser
        String sql = "SELECT idUser, COUNT(*) AS frequency FROM posts GROUP BY idUser ORDER BY frequency DESC LIMIT 5";
        List<Long> topUserIds = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("idUser"));

        // Krok 2: Získání uživatelů podle idUser
        if (topUserIds.isEmpty()) {
            return List.of(); // Pokud nejsou nalezeni žádní uživatelé, vrátí prázdný seznam
        }

        // Vytvoření SQL dotazu s IN klauzulí pro získání uživatelů
        String inSql = String.join(",", topUserIds.stream().map(String::valueOf).toArray(String[]::new));
        String usersSql = "SELECT id, firstName, lastName, login, password, email, idRole FROM users WHERE id IN (" + inSql + ")";

        return jdbcTemplate.query(usersSql, ROW_MAPPER);
    }

    public Optional<User> findBySessionId(String sessionId) {
        //String sql = "SELECT * FROM users WHERE session_id = ?";  // Předpokládáme, že uživatelé mají sloupec 'session_id'
        String sql = "SELECT u.* FROM users u JOIN sessions s ON u.id = s.user_id WHERE s.session_id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new Object[]{sessionId},ROW_MAPPER);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();  // Pokud uživatel není nalezen nebo nastane jiná chyba
        }
    }

    public String getLoginByIdUser(int id){
        String sql = "SELECT login FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id},String.class);
    }




}

