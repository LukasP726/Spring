package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Model.Role;
import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;


    public UserService(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }
 
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.getUserById(id);
    }

    public int saveUser(User user) {
        return userRepository.saveUser(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteUserById(id);
    }

    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    public Optional<Role> getRoleById(Long id) {
        return roleService.getRoleById(id);
    }

    public List<User> searchByName(String term) {
        return userRepository.findByNameContaining(term);
    }

    public Optional<User> findByName(String name){
        return userRepository.findByLogin(name);
    }

    public List<User> getTopUsers() {
       return userRepository.getTopUsersByPostFrequency();
    }


    
}
