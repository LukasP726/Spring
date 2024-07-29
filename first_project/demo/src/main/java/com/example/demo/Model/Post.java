package com.example.demo.Model;

import java.security.Timestamp;

import javax.persistence.*;

@Table(name = "posts")
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Long idUser;
    private Long idThread;
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "idUser", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "idThread", insertable = false, updatable = false)
    private Thread thread;

    public Object getContent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getContent'");
    }

    public Object getUserId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserId'");
    }

    public Object getThreadId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getThreadId'");
    }

    // Gettery a settery
}

