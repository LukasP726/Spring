package com.example.demo.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.demo.model.Role;
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

    /**
     * Vrací seznam všech rolí z tabulky `role`.
     */
    public List<Role> findAll() {
        return jdbcTemplate.query("SELECT * FROM role", ROLE_ROW_MAPPER);
    }

    /**
     * Vyhledá roli podle jejího ID.
     * Pokud neexistuje, vrací prázdný Optional.
     */
    public Optional<Role> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM role WHERE id = ?", ROLE_ROW_MAPPER, id)
                .stream()
                .findFirst();
    }

    /**
     * Uloží roli do databáze. 
     * Pokud role nemá ID, provede INSERT, jinak UPDATE podle ID.
     * Vrací počet ovlivněných řádků.
     */
    public int save(Role role) {
        if (role.getId() == null) {
            return jdbcTemplate.update("INSERT INTO role (name, weight) VALUES (?, ?)",
                    role.getName(), role.getWeight());
        } else {
            return jdbcTemplate.update("UPDATE role SET name = ?, weight = ? WHERE id = ?",
                    role.getName(), role.getWeight(), role.getId());
        }
    }

    /**
     * Smaže roli podle jejího ID.
     * Vrací počet ovlivněných řádků.
     */
    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM role WHERE id = ?", id);
    }

    /**
     * Vrací hodnotu `weight` pro roli podle jejího ID.
     */
    public int getWeightByRoleId(int idRole) {
        String sql = "SELECT weight FROM role WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{idRole}, Integer.class);
    }

}


