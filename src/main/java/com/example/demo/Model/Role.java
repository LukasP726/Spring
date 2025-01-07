package com.example.demo.model;

import javax.persistence.*;

@Table(name = "role")
@Entity
public class Role {
    
    public static int getId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private int weight;

    public Role(long id, String name, int weight) {
        this.id = id;
        this.name = name;
        this.weight = weight;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}