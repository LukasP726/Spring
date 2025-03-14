package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.ThreadRepository;
import com.example.demo.repository.UploadRepository;
import com.example.demo.repository.UserRepository;

import jakarta.websocket.Session;

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
    private final FriendshipRepository friendshipRepository;






    public UserService(
        UserRepository userRepository, 
        RoleService roleService, 
        PostRepository postRepository,
        ThreadRepository threadRepository,
        UploadRepository uploadRepository,
        FriendshipRepository friendshipRepository
       
    ) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.postRepository = postRepository;
        this.threadRepository = threadRepository;
        this.uploadRepository = uploadRepository;
        this.friendshipRepository = friendshipRepository;
       

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
        friendshipRepository.deleteAllFriendshipsByUserId(id);
      
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
