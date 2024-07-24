package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class HeroRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RowMapper<Hero> heroRowMapper = new RowMapper<Hero>() {
        @Override
        public Hero mapRow(ResultSet rs, int rowNum) throws SQLException {
            Hero hero = new Hero();
            hero.setId(rs.getLong("id"));
            hero.setName(rs.getString("name"));
            return hero;
        }
    };

    public List<Hero> findAll() {
        return jdbcTemplate.query("SELECT * FROM hero", heroRowMapper);
    }

    public int save(Hero hero) {
        if (hero.getId() == null) {
            // Insert
            return jdbcTemplate.update("INSERT INTO hero (name) VALUES (?)", hero.getName());
        } else {
            // Update
            return jdbcTemplate.update("UPDATE hero SET name = ? WHERE id = ?", hero.getName(), hero.getId());
        }
    }

    public Optional<Hero> findById(Long id) {
        String sql = "SELECT * FROM hero WHERE id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);
        List<Hero> heroes = namedParameterJdbcTemplate.query(sql, namedParameters, heroRowMapper);
        if (heroes.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(heroes.get(0));
        }
    }

    public int delete(Hero hero) {
        return jdbcTemplate.update("DELETE FROM hero WHERE id = ?", hero.getId());
    }
}
