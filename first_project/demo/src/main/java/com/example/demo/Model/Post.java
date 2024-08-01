package com.example.demo.Model;

import java.sql.Timestamp;

import javax.persistence.*;

@Table(name = "posts")
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String content;
    
    @ManyToOne
    @JoinColumn(name = "idUser")
    private Integer idUser;

    @ManyToOne
    @JoinColumn(name = "idThread")
    private Integer idThread;
    private Timestamp createdAt;




    public String getContent() {
        return content;
    }

    public Integer getIdUser() {
       return idUser;
    }

    public Integer getIdThread() {
       return idThread;
    }

    public void setId(Integer id){
        this.id = id;

    }

    public Integer getId(){
        return id;
    }

    public void setIdUser(Integer idUser){
        this.idUser = idUser;
        
    }


    public void setIdThread(Integer idThread){
        this.idThread = idThread;
        
    }

    public void setContent(String content){
        this.content = content;
        
    }

    public void setCreatedAt(java.sql.Timestamp createdAt) {
       this.createdAt = createdAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

 
}

