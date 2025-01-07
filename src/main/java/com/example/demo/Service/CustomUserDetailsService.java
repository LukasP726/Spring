package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Načtení uživatele z databáze
        String userQuery = "SELECT id, firstName, lastName, login, password, email, idRole FROM users WHERE login = ?";
        User user = jdbcTemplate.queryForObject(userQuery, new Object[]{username}, new BeanPropertyRowMapper<>(User.class));

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Načtení role uživatele z databáze
        String roleQuery = "SELECT name FROM role WHERE id = ?";
        //"ROLE_"+
        String roleName = jdbcTemplate.queryForObject(roleQuery, new Object[]{user.getIdRole()}, String.class);

        // Převod role na GrantedAuthority
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(roleName));

        // Vytvoření instanci UserDetails s uživatelskými informacemi a rolemi
        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), authorities);
    }
}

