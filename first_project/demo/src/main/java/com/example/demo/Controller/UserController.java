package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Model.Hero;
import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.AuthService;
import com.example.demo.Service.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:4200","http://192.168.56.1:4200"})
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;


    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "API is working!";
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }
/* 
    @PostMapping
    public void createUser(@RequestBody User user) {
        userService.saveUser(user);
    }
*/
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setLogin(userDetails.getLogin());
        user.setPassword(userDetails.getPassword());
        user.setEmail(userDetails.getEmail());
        user.setIdRole(userDetails.getIdRole());
        userService.saveUser(user);
        return user;
    }

        @PostMapping
        public User addUser(@RequestBody User user) {
        int result = userService.saveUser(user);
        if (result == 1) {
            return user; // Úspěšné vložení nebo aktualizace
        } else {
            throw new RuntimeException("Failed to add or update user");
        }
    }
    

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
        userService.deleteUser(id);
    }

    @GetMapping("/")
    public List<User> searchHeroes(@RequestParam(name = "name") String term) {
        return userService.searchByName(term);
    }
/* 
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); 
        }
    
        User user = authService.findByLogin(principal.getName())
                               .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }
*/

@GetMapping("/me")
public ResponseEntity<User> getCurrentUser(Authentication authentication) {
    String username = authentication.getName();
    User user = userService.findByName(username)
        .orElseThrow(() -> new RuntimeException("User not found"));
    return ResponseEntity.ok(user);
}

}
