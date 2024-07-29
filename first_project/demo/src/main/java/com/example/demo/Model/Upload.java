package com.example.demo.Model;

import java.security.Timestamp;

import javax.persistence.*;

@Table(name = "uploads")
@Entity
public class Upload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filename;
    private Long userId;
    private Long postId;
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private Post post;

    public Object getFilename() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFilename'");
    }

    public Object getUserId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserId'");
    }

    public Object getPostId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPostId'");
    }

    // Gettery a settery
}

