package com.example.demo.Repository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class HashedPasswordsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int deleteByIdUser(Long idUser) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM hashed_passwords WHERE idUser = ?", idUser);
        return rowsAffected;
    }



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




}
