package com.example.demo.model;

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


    @ManyToOne
    @JoinColumn(name = "id_user", insertable = false, updatable = false)
    private Integer idUser;
    
    @JoinColumn(name = "create_at")
    private Timestamp createdAt;


    //private User user;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public Integer getIdUser(){
        return idUser;
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