package com.example.demo.repository;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.demo.model.FriendRequest;
import com.example.demo.model.FriendRequestDTO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@Repository
public class FriendRequestRepository {

    private final JdbcTemplate jdbcTemplate;

    public FriendRequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Přidání nové žádosti o přátelství
    public void addFriendRequest(FriendRequest friendRequest) {
        String sql = "INSERT INTO friend_request (from_user_id, to_user_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, friendRequest.getFromUserId(), friendRequest.getToUserId(), friendRequest.getStatus());
    }

    // Získání všech žádostí o přátelství pro uživatele
    public List<FriendRequest> getFriendRequestsForUser(Long userId) {
        String sql = "SELECT * FROM friend_request WHERE to_user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, this::mapRowToFriendRequest);
    }

    // Změna stavu žádosti o přátelství
    public void updateFriendRequestStatus(Long requestId, String status) {
        String sql = "UPDATE friend_request SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, requestId);
    }

    // Smazání žádosti o přátelství
    public void deleteFriendRequest(Long requestId) {
        String sql = "DELETE FROM friend_request WHERE id = ?";
        jdbcTemplate.update(sql, requestId);
    }

    // Nalezení žádosti o přátelství podle ID
    public Optional<FriendRequest> findById(Long requestId) {
        String sql = "SELECT * FROM friend_request WHERE id = ?";
        return jdbcTemplate.query(sql, new Object[]{requestId}, this::mapRowToFriendRequest).stream().findFirst();
    }

    /**
    * Získá seznam žádostí o přátelství, které byly odeslány konkrétnímu uživateli.
    * Propojuje tabulku friend_request s tabulkou users podle ID cílového uživatele.
    */
    public List<FriendRequest> getRequests(Long id) {
        String sql = "SELECT * FROM friend_request fr " +
        "JOIN users u ON fr.to_user_id = u.id " +
        "WHERE u.id = ?";

        return jdbcTemplate.query(sql, new Object[]{id}, this::mapRowToFriendRequest);



    }


    /**
     * Načte seznam žádostí o přátelství pro zadaného uživatele ve formátu DTO.
     * Výsledek zahrnuje informace o odesílateli včetně jeho přihlašovacího jména.
     * Výsledky jsou seřazeny sestupně podle data vytvoření.
     */
    public List<FriendRequestDTO> getFriendRequestsForUserDTO(Long toUserId) {
        String sql = """
            SELECT 
                fr.id,
                fr.from_user_id AS fromUserId,
                fr.to_user_id AS toUserId,
                fr.created_at AS createdAt,
                fr.status,
                u.login AS fromUserLogin
            FROM 
                friend_request fr
            JOIN 
                users u ON fr.from_user_id = u.id
            WHERE 
                fr.to_user_id = ?
            ORDER BY fr.created_at DESC
        """;

        return jdbcTemplate.query(sql, new Object[]{toUserId}, new BeanPropertyRowMapper<>(FriendRequestDTO.class));
    }



    // Mapování výsledků dotazu na objekt FriendRequest
    private FriendRequest mapRowToFriendRequest(ResultSet rs, int rowNum) throws SQLException {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setId(rs.getLong("id"));
        friendRequest.setFromUserId(rs.getLong("from_user_id"));
        friendRequest.setToUserId(rs.getLong("to_user_id"));
        friendRequest.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        friendRequest.setStatus(rs.getString("status"));
        return friendRequest;
    }


}

