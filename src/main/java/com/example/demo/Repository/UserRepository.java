package com.example.demo.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.example.demo.model.User;

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
//import com.example.demo.Config.Md5PasswordEncoder;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    //private final Md5PasswordEncoder passwordEncoder;
                                                //  Md5PasswordEncoder passwordEncoder
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
                rs.getLong("idRole"),
                rs.getBoolean("isBanned")
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
            rowsAffected = jdbcTemplate.update("UPDATE users SET firstName = ?, lastName = ?, login = ?, password = ?, email = ?, idRole = ?, isBanned = ? WHERE id = ?",
                    user.getFirstName(), user.getLastName(), user.getLogin(), hashedPassword, user.getEmail(), user.getIdRole(), user.getIsBanned(), user.getId());
            userId = user.getId();
        }
        

            

        return rowsAffected;
    }

    public int deleteUserById(Long id) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
        return rowsAffected;
    }

public List<User> findByNameContaining(String term) {
    // Upravený SQL dotaz s parametrizací
    //String sql = "SELECT * FROM users WHERE (firstName LIKE ? OR lastName LIKE ? OR login LIKE ?) AND isBanned = false";

    // Přidání zástupných znaků procent k termínu vyhledávání
    //String searchTerm = "%" + term + "%";
    String sql = "SELECT * FROM users WHERE (firstName LIKE'" + term + "' OR lastName LIKE '" + term + "' OR login LIKE '" + term + "') AND isBanned = false";


    // Použití parametrizovaného dotazu s třemi argumenty, aby se předešlo SQL injection
    //return jdbcTemplate.query(sql, new Object[]{searchTerm, searchTerm, searchTerm}, ROW_MAPPER);
    return jdbcTemplate.query(sql, ROW_MAPPER);
}

    

    public Optional<User> findByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";
        List<User> users = jdbcTemplate.query(sql, ROW_MAPPER, login);
        return users.stream().findFirst();
    }


    public List<User> getTopUsersByPostFrequency() {
        // Krok 1: Získání 5 nejčastějších idUser
        String sql = "SELECT id_user, COUNT(*) AS frequency FROM posts GROUP BY id_user ORDER BY frequency DESC LIMIT 5";
        List<Long> topUserIds = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("id_user"));

        // Krok 2: Získání uživatelů podle idUser
        if (topUserIds.isEmpty()) {
            return List.of(); // Pokud nejsou nalezeni žádní uživatelé, vrátí prázdný seznam
        }

        // Vytvoření SQL dotazu s IN klauzulí pro získání uživatelů
        String inSql = String.join(",", topUserIds.stream().map(String::valueOf).toArray(String[]::new));
        String usersSql = "SELECT id, firstName, lastName, login, password, email, idRole, isBanned FROM users WHERE id IN (" + inSql + ")";

        return jdbcTemplate.query(usersSql, ROW_MAPPER);
    }

    public Optional<User> findBySessionId(String sessionId) {
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

