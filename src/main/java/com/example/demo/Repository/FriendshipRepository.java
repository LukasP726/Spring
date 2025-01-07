package com.example.demo.repository;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Friendship;
import com.example.demo.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FriendshipRepository {

    private final JdbcTemplate jdbcTemplate;

    public FriendshipRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Přidání nového přátelství
    public void addFriendship(Friendship friendship) {
        String sql = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, friendship.getUserId(), friendship.getFriendId());
    }

    // Získání všech přátel pro uživatele
    public List<Friendship> getFriendsForUser(Long userId) {
        String sql = "SELECT * FROM friendship WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, this::mapRowToFriendship);
    }

    public boolean existsByUserIdAndFriendId(Long userId, Long friendId) {
        String sql = "SELECT COUNT(*) FROM friendship WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{userId, friendId}, Integer.class);
        return count != null && count > 0;
    }

    public void deleteFriendship(Long userId, Long friendId) {
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public void deleteAllFriendshipsByUserId(Long userId) {
        String sql = "DELETE FROM friendship WHERE user_id = ? OR friend_id = ?";
        jdbcTemplate.update(sql, userId, userId);
    }


    public List<User> findFriendsByUserId(Long userId) {
        
        String sql = "SELECT u.* FROM users AS u " +
        "JOIN friendship AS f ON (u.id = f.friend_id) " +
        "WHERE ( f.user_id = ?)";
         /*
        String sql = "SELECT u.* FROM users AS u " +    
        "JOIN friendship AS f ON (u.id = f.user_id) " +
        "WHERE ( f.user_id = ?)";
        

         String sql = "SELECT u.* FROM users AS u " + // Uprav podle sloupců v tabulce users
         "JOIN friendship AS f ON u.id = f.friend_id " +
         "WHERE f.user_id = ? " +
         "UNION ALL " +
         "SELECT u.* FROM users AS u " + // Uprav podle sloupců v tabulce users
         "JOIN friendship AS f ON u.id = f.user_id " +
         "WHERE f.friend_id = ?";
          */



         


       
        
        /* 
        String sql = "SELECT u.* FROM users u "+
        "WHERE u.id = ?";
        */




    
        return jdbcTemplate.query(sql, new Object[]{userId}, this::mapRowToUser);
    }
    

    // Mapování výsledků dotazu na objekt Friendship
    private Friendship mapRowToFriendship(ResultSet rs, int rowNum) throws SQLException {
        Friendship friendship = new Friendship();
        friendship.setId(rs.getLong("id"));
        friendship.setUserId(rs.getLong("user_id"));
        friendship.setFriendId(rs.getLong("friend_id"));
        friendship.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return friendship;
    }



    
    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setFirstName(rs.getString("firstName"));
        user.setLastName(rs.getString("lastName"));
        user.setLogin(rs.getString("login"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setIdRole(rs.getLong("idRole"));
        return user;
    }
}

