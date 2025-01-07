package com.example.demo.Model;

import java.time.LocalDateTime;

public class PostDTO {

    private Integer id;
    private String content;
    private LocalDateTime createdAt;
    private String owner;

    // Getter pro id
    public Integer getId() {
        return id;
    }

    // Setter pro id
    public void setId(Integer id) {
        this.id = id;
    }

    // Getter pro content
    public String getContent() {
        return content;
    }

    // Setter pro content
    public void setContent(String content) {
        this.content = content;
    }

    // Getter pro createdAt
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setter pro createdAt
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Getter pro owner
    public String getOwner() {
        return owner;
    }

    // Setter pro owner
    public void setOwner(String owner) {
        this.owner = owner;
    }
}
