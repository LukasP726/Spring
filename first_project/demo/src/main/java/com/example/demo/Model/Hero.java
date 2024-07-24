package com.example.demo.Model;

public class Hero {
    private Long id;
    private String name;

    public Hero(Long id, String name){
        this.id = id;
        this.name =name;
    }

    // Gettery a settery
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
}
