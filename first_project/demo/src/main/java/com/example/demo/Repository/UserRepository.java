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
        String hashedPassword = passwordEncoder.encode(rawPassword);

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
        
        if(userId != null){
            if (userId > 0) {
                try {
                    //saveHashedPasswords(userId, rawPassword);
                    saveOrUpdateHashedPasswords(userId, hashedPassword);
                } catch (NoSuchAlgorithmException | SQLException e) {
                    e.printStackTrace();
                    // Pokud dojde k chybě při ukládání hashů, zahoďme transakci
                    throw new DataAccessException("Failed to save hashed passwords", e) {};
                }
            }
        }
            

        return rowsAffected;
    }

    public int deleteUserById(Long id) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
        rowsAffected += jdbcTemplate.update("DELETE FROM hashed_passwords WHERE idUser = ?", id);
        return rowsAffected;
    }

    public List<User> findByNameContaining(String term) {
        String sql = "SELECT * FROM users WHERE firstName LIKE ? OR lastName LIKE ? OR login LIKE ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, "%" + term + "%", "%" + term + "%","%" + term + "%");
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

private void saveOrUpdateHashedPasswords(Long userId, String password) throws NoSuchAlgorithmException, SQLException {
    String md5Hash = hashPassword(password, "MD5");
    String sha1Hash = hashPassword(password, "SHA-1");
    String sha256Hash = hashPassword(password, "SHA-256");

    // Kontrola, zda už existuje záznam pro daný userId
    String checkSQL = "SELECT COUNT(*) FROM hashed_passwords WHERE idUser = ?";
    String insertSQL = "INSERT INTO hashed_passwords (idUser, password_md5, password_sha1, password_sha256) VALUES (?, ?, ?, ?)";
    String updateSQL = "UPDATE hashed_passwords SET password_md5 = ?, password_sha1 = ?, password_sha256 = ? WHERE idUser = ?";

    try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSQL)) {
            checkStmt.setLong(1, userId);
            try (ResultSet resultSet = checkStmt.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // Záznam už existuje, provedeme update
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSQL)) {
                        updateStmt.setString(1, md5Hash);
                        updateStmt.setString(2, sha1Hash);
                        updateStmt.setString(3, sha256Hash);
                        updateStmt.setLong(4, userId);
                        updateStmt.executeUpdate();
                        System.out.println("Hashed passwords updated for user ID: " + userId);
                    }
                } else {
                    // Záznam neexistuje, provedeme insert
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertSQL)) {
                        insertStmt.setLong(1, userId);
                        insertStmt.setString(2, md5Hash);
                        insertStmt.setString(3, sha1Hash);
                        insertStmt.setString(4, sha256Hash);
                        insertStmt.executeUpdate();
                        System.out.println("Hashed passwords saved for user ID: " + userId);
                    }
                }
            }
        }
    } catch (SQLException e) {
        System.err.println("Error saving or updating hashed passwords: " + e.getMessage());
        throw e;
    }
}

    private String hashPassword(String password, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        byte[] hashedBytes = messageDigest.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
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




}

