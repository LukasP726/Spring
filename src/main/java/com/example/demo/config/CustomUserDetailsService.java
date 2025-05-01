package com.example.demo.config;

import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // Přetížená metoda z rozhraní UserDetailsService, volaná při autentizaci uživatele
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(username)
            .orElseThrow(() -> new UsernameNotFoundException("Uživatel nenalezen: " + username));

        Role role = roleRepository.findById(user.getIdRole())
            .orElseThrow(() -> new IllegalStateException("Role s ID " + user.getIdRole() + " nenalezena"));

        return new org.springframework.security.core.userdetails.User(
            user.getLogin(),
            user.getPassword(),
            getAuthorities(role)
        );
    }
    
    // Přetížená metoda z rozhraní UserDetailsService, volaná při autentizaci uživatele
    private Collection<? extends GrantedAuthority> getAuthorities(Role role) {
        String formattedRole = "ROLE_" + role.getName().toUpperCase(); // např. ROLE_SUPERADMIN
        return List.of(new SimpleGrantedAuthority(formattedRole));
    }
}
