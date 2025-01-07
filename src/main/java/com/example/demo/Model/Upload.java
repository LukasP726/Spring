package com.example.demo.model;

import java.sql.Timestamp;

import javax.persistence.*;

@Table(name = "uploads")
@Entity
public class Upload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String filename;
    @ManyToOne
    @JoinColumn(name = "id_user")
    private Integer idUser;

    @ManyToOne
    @JoinColumn(name = "id_post")
    private Integer idPost;
    
    @JoinColumn(name = "created_at")
    private Timestamp createdAt;

    


    public String getFilename() {
        return filename;
    }

    public Integer getIdUser() {
       return idUser;
    }

    public Integer getIdPost() {
       return idPost;
    }

    public void setFilename(String originalFilename) {
      this.filename = originalFilename;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public void setIdPost(Integer idPost) {
        this.idPost = idPost;
    }

    public void setCreatedAt(java.sql.Timestamp createAt) {
        this.createdAt = createAt;
    }

    public Timestamp getCreatedAt(){
        return createdAt;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId(){
        return id;
    }

    // Gettery a settery
}

