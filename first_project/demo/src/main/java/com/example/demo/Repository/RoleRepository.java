package com.example.demo.Repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.Role;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class RoleRepository {

    private final JdbcTemplate jdbcTemplate;

    public RoleRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final RowMapper<Role> ROLE_ROW_MAPPER = new RowMapper<>() {
        @Override
        public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Role(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getInt("weight")
            );
        }
    };

    public List<Role> findAll() {
        return jdbcTemplate.query("SELECT * FROM role", ROLE_ROW_MAPPER);
    }

    public Optional<Role> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM role WHERE id = ?", ROLE_ROW_MAPPER, id)
                .stream()
                .findFirst();
    }

    public int save(Role role) {
        if (role.getId() == null) {
            return jdbcTemplate.update("INSERT INTO role (name, weight) VALUES (?, ?)",
                    role.getName(), role.getWeight());
        } else {
            return jdbcTemplate.update("UPDATE role SET name = ?, weight = ? WHERE id = ?",
                    role.getName(), role.getWeight(), role.getId());
        }
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM role WHERE id = ?", id);
    }
}


