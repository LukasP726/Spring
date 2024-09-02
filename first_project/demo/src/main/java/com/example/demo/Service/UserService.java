package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Model.Role;
import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepository;

import jakarta.websocket.Session;

import com.example.demo.Repository.PostRepository;
import com.example.demo.Repository.ThreadRepository;
import com.example.demo.Repository.UploadRepository;
import com.example.demo.Repository.HashedPasswordsRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;



@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PostRepository postRepository;
    private final ThreadRepository threadRepository;
    private final UploadRepository uploadRepository;
    private final HashedPasswordsRepository hashedPasswordsRepository;





    public UserService(
        UserRepository userRepository, 
        RoleService roleService, 
        PostRepository postRepository,
        ThreadRepository threadRepository,
        UploadRepository uploadRepository,
        HashedPasswordsRepository hashedPasswordsRepository
    ) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.postRepository = postRepository;
        this.threadRepository = threadRepository;
        this.uploadRepository = uploadRepository;
        this.hashedPasswordsRepository = hashedPasswordsRepository;

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
        threadRepository.deleteByIdUser(id);
        postRepository.deleteByIdUser(id);
        uploadRepository.deleteByIdUser(id);
        hashedPasswordsRepository.deleteByIdUser(id);
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

    public User findBySessionId(String sessionId) {
        return userRepository.findBySessionId(sessionId).orElse(null);
    }

    


    
}
