package com.example.demo.Model;

import javax.persistence.*;

@Table(name = "users")
@Entity
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String firstName;
    private String lastName;
    private String login;
    private String password;
    private String email;
    
    @ManyToOne
    //@JoinColumn(name = "id")
    private Long role;

       public User(long id, String firstName, String lastName, String login, String password, String email, long role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.login =login;
        this.password = password;
        this.email = email;
        this.role=role;
    }

    // Getters and Setters
       public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getRoleId() {
        return Role.getId;
    }

    public Long getRole(){
        return this.role;
    }

    public void setRole(Long role) {
        this.role = role;
    }



    // Getters and setters
}
