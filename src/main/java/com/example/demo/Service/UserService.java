package com.example.demo.service;

import org.springframework.stereotype.Service;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.ThreadRepository;
import com.example.demo.repository.UploadRepository;
import com.example.demo.repository.UserRepository;
import java.util.List;
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
 
    // Získá všechny uživatele.
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    // Získá uživatele podle jeho ID.
    public Optional<User> getUserById(Long id) {
        return userRepository.getUserById(id);
    }

    // Uloží nebo aktualizuje uživatele.
    public int saveUser(User user) {
        return userRepository.saveUser(user);
    }

    // Aktualizuje přihlašovací údaje a e-mail uživatele.
    public int updateLoginAndEmail(Long userId, String login, String email) {
        return userRepository.updateLoginAndEmail(userId, login, email);
    }

    // Aktualizuje heslo uživatele.
    public int updatePassword(Long userId, String rawPassword) {
        return userRepository.updatePassword(userId, rawPassword);
    }

    // Smaže uživatele a všechny jeho související data (příspěvky, nahrané soubory, přátelství).
    public void deleteUser(Long id) {
        userRepository.deleteUserById(id);
        threadRepository.deleteByIdUser(id);
        postRepository.deleteByIdUser(id);
        uploadRepository.deleteByIdUser(id);
        friendshipRepository.deleteAllFriendshipsByUserId(id);
    }

    // Získá všechny role.
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    // Získá roli podle jejího ID.
    public Optional<Role> getRoleById(Long id) {
        return roleService.getRoleById(id);
    }

    // Vyhledá uživatele podle jména.
    public List<User> searchByName(String term) {
        return userRepository.findByNameContaining(term);
    }

    // Najde uživatele podle jeho přihlašovacího jména.
    public Optional<User> findByName(String name) {
        return userRepository.findByLogin(name);
    }

    // Získá uživatele s nejvyšší frekvencí příspěvků.
    public List<User> getTopUsers() {
       return userRepository.getTopUsersByPostFrequency();
    }


}
