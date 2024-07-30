package com.example.demo.Model;

import java.sql.Timestamp;
import java.sql.Date;

import javax.persistence.*;



@Table(name = "threads")
@Entity
public class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer idUser;
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "idUser", insertable = false, updatable = false)
    private User user;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(Integer idUser) {
        this.idUser = idUser;
    }

    public void setCreatedAt(java.sql.Timestamp timestamp) {
        this.createdAt = timestamp;
        
    }

    public String getName() {
      return name;
    }

    public Integer getId() {
        return id;
    }

    // Gettery a settery
}