package com.example.demo.Repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.Hero;
import com.example.demo.Model.User;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final RowMapper<User> USER_ROW_MAPPER = new RowMapper<>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                rs.getLong("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("login"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getLong("id_role")
            );
        }
    };

    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users", USER_ROW_MAPPER);
    }

    public Optional<User> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, id)
                .stream()
                .findFirst();
    }

    public int save(User user) {
        if (user.getId() == null) {
            return jdbcTemplate.update("INSERT INTO users (first_name, last_name, login, password, email, id_role) VALUES (?, ?, ?, ?, ?, ?)",
                    user.getFirstName(), user.getLastName(), user.getLogin(), user.getPassword(), user.getEmail(), user.getRoleId());
        } else {
            return jdbcTemplate.update("UPDATE users SET first_name = ?, last_name = ?, login = ?, password = ?, email = ?, id_role = ? WHERE id = ?",
                    user.getFirstName(), user.getLastName(), user.getLogin(), user.getPassword(), user.getEmail(), user.getRoleId(), user.getId());
        }
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }

    public List<User> findByNameContaining(String term) {
        String sql = "SELECT * FROM users WHERE first_name LIKE ? OR last_name LIKE ?";
        RowMapper<User> rowMapper = (rs, rowNum) -> new User(
            rs.getLong("id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("login"),
            rs.getString("password"), 
            rs.getString("email"),      
            rs.getLong("id_role")
        );
        return jdbcTemplate.query(sql, rowMapper, "%" + term + "%", "%" + term + "%");
    }



}

