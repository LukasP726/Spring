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
    @JoinColumn(name = "idRole")
    private Long idRole;

       public User(Long id, String firstName, String lastName, String login, String password, String email, Long idRole) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.login =login;
        this.password = password;
        this.email = email;
        this.idRole=idRole;
    }

    public User() {}

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


    public Long getIdRole(){
        return this.idRole;
    }

    public void setIdRole(Long role) {
        this.idRole = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", role=" + idRole +
                '}';
    }



    // Getters and setters
}
