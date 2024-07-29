package com.example.demo.Model;

import java.security.Timestamp;
import java.sql.Date;

import javax.persistence.*;



@Table(name = "threads")
@Entity
public class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long idUser;
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "idUser", insertable = false, updatable = false)
    private User user;

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(Long idUser) {
        this.idUser = idUser;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        
    }

    public String getName() {
      return name;
    }

    public Long getId() {
        return id;
    }

    // Gettery a settery
}