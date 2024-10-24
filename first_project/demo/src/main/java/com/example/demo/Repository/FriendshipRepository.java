package com.example.demo.Repository;


import com.example.demo.Model.Friendship;
import com.example.demo.Model.User;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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


    public List<User> findFriendsByUserId(Long userId) {
        String sql = "SELECT u.* FROM users u " +
                     "JOIN friendship f ON u.id = f.friend_id " +
                     "WHERE f.user_id = ?";
    
        return jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) -> {
            User friend = new User();
            friend.setId(rs.getLong("id"));
            friend.setFirstName(rs.getString("firstName"));
            friend.setLastName(rs.getString("lastName"));
            friend.setLogin(rs.getString("login"));
            friend.setEmail(rs.getString("email"));
            friend.setIdRole(rs.getLong("idRole"));
            // Můžete nastavit i další vlastnosti, pokud budou přidány do User objektu
            return friend;
        });
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
}

