package com.example.demo.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.Hero;

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
            Hero hero = new Hero(rs.getLong("id"), rs.getString("name"));
            //hero.setId(rs.getLong("id"));
            //hero.setName(rs.getString("name"));
            return hero;
        }
    };

    public List<Hero> findAll() {
        return jdbcTemplate.query("SELECT * FROM hero", heroRowMapper);
    }

    public int save(Hero hero) {
        if (hero.getId() == null) {
            // Insert
            if (findByNameContaining(hero.getName()).isEmpty()) {
                return jdbcTemplate.update("INSERT INTO hero (name) VALUES (?)", hero.getName());
            } else {
                throw new RuntimeException("Hero with name " + hero.getName() + " already exists.");
            }
        } else {
            // Update
            Hero existingHero = findById(hero.getId())
                    .orElseThrow(() -> new RuntimeException("Hero not found with id " + hero.getId()));
            
            // Check if the updated name already exists for another hero
            if (!existingHero.getName().equals(hero.getName()) && !findByNameContaining(hero.getName()).isEmpty()) {
                throw new RuntimeException("Hero with name " + hero.getName() + " already exists.");
            }
            
            return jdbcTemplate.update("UPDATE hero SET name = ? WHERE id = ?", hero.getName(), hero.getId());
        }
    }
    

    public Optional<Hero> findById(Long id) {
        String sql = "SELECT * FROM hero WHERE id = '"+id+"'";
        //String sql = "SELECT * FROM hero WHERE id = " + id;
        //SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);
        //List<Hero> heroes = namedParameterJdbcTemplate.query(sql, namedParameters, heroRowMapper);
        List<Hero> heroes = namedParameterJdbcTemplate.query(sql, heroRowMapper);
        if (heroes.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(heroes.get(0));
        }
    }

    public int delete(Hero hero) {
        return jdbcTemplate.update("DELETE FROM hero WHERE id = ?", hero.getId());
    }


    public List<Hero> findByNameContaining(String term) {
        String sql = "SELECT * FROM hero WHERE name LIKE ?";
        RowMapper<Hero> rowMapper = (rs, rowNum) -> new Hero(rs.getLong("id"), rs.getString("name"));
        return jdbcTemplate.query(sql, rowMapper, "%" + term + "%");
    }
}
