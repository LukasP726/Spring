package com.example.demo.model;

import java.time.LocalDateTime;

public class FriendRequestDTO {


    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private LocalDateTime createdAt;
    private String status; // Například "pending", "accepted", "declined"
    private String fromUserLogin;

    public String getFromUserLogin(){
        return fromUserLogin;
    }

    public void setFromUserLogin(String s){
        this.fromUserLogin = s;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}
