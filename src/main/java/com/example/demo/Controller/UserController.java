package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.demo.config.Md5PasswordEncoder;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.UserUpdateDTO;
import com.example.demo.request.ChangePasswordRequest;
import com.example.demo.request.PasswordVerificationRequest;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:4200","http://192.168.56.1:4200"})
public class UserController {

    final int ADMIN_WEIGHT = 10;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private Md5PasswordEncoder passwordEncoder;

    



    @GetMapping
    public ResponseEntity<List<User>> getAllUser(HttpServletRequest request) {
        // 1. Získání aktuálního uživatele ze session
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. Získání všech uživatelů a filtrování
        List<User> users = userService.getAllUsers();
        List<User> filteredUsers = users.stream()
            .filter(user -> !user.getId().equals(currentUser.getId())) // Nezahrnout aktuálního uživatele
            .filter(user -> user.getIdRole() > currentUser.getIdRole()) // Zahrnout pouze uživatele s vyšším idRole (nižší práva)
            .collect(Collectors.toList());

        return ResponseEntity.ok(filteredUsers);
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
                

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setLogin(userDetails.getLogin());
        //user.setPassword(userDetails.getPassword());
        user.setEmail(userDetails.getEmail());
        user.setIdRole(userDetails.getIdRole());
        user.setIsBanned(userDetails.getIsBanned());
        userService.saveUser(user);
        return user;
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(
            @RequestBody UserUpdateDTO userDetails,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Aktualizuj pouze to, co má DTO
        if (userDetails.getLogin() != null && !userDetails.getLogin().isEmpty()) {
            currentUser.setLogin(userDetails.getLogin());
        }

        if (userDetails.getEmail() != null && !userDetails.getEmail().isEmpty()) {
            currentUser.setEmail(userDetails.getEmail());
        }
        //userService.saveUser(currentUser);
        userService.updateLoginAndEmail(currentUser.getId(), currentUser.getLogin(), currentUser.getEmail()); // zachová stávající heslo

        return ResponseEntity.ok(currentUser);
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
    public List<User> searchUsers(@RequestParam(name = "name") String term) {
        return userService.searchByName(term);
    }


    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // Nevytvářet novou session
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                System.out.println("No user found for session ID.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            System.out.println("No active session found.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }      
 
    }
    
        
    


    @GetMapping("/top-users")
    public List<User> getTopUsers() {
        return userService.getTopUsers();
    }

    @PostMapping("/verify-password")
    public ResponseEntity<Boolean> verifyPassword(@RequestBody PasswordVerificationRequest request, Authentication authentication) {
        Optional<User> optionalUser = userService.findByName(authentication.getName());

        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

        User currentUser = optionalUser.get();

        boolean matches = passwordEncoder.matches(request.getPassword(), currentUser.getPassword());

        if (matches) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }



    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody ChangePasswordRequest request, HttpServletRequest r) {
        String oldPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();
    



        if (oldPassword == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "OldPassword cannot be null"));
        }
    
        if (newPassword == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "NewPassword cannot be null"));
        }
    
        ResponseEntity<User> responseEntity = getCurrentUser(r);
        User currentUser = responseEntity.getBody();
    

        String currentPassword = request.getCurrentPassword();
        if (currentPassword != null) {
            currentPassword = currentPassword.trim();
        }
        
        // Ověření stávajícího hesla
         
        if (!passwordEncoder.matches( currentPassword, currentUser.getPassword())) {
            System.out.println("currentpass: "+currentPassword);
            System.out.println(currentUser.getPassword());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Current password is incorrect"));
        }
            
    
        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be null or empty");
        }
    
        // Ověření, že nová hesla se shodují
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "New passwords do not match"));
        }
    
        // Aktualizace hesla
        currentUser.setPassword(request.getNewPassword());
        //userService.saveUser(currentUser)
        if(userService.updatePassword(currentUser.getId(), currentUser.getPassword()) > 0){
            return ResponseEntity.ok(Collections.singletonMap("message", "Password changed successfully"));
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Password did not change"));
        }
    }


    @GetMapping("/is-admin")
    public ResponseEntity<Boolean> isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // Nepokoušej se vytvořit novou session

        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                Long idRole = user.getIdRole();
                Optional<Role> roleOpt = roleService.getRoleById(idRole);
    
                int weightStrict = 0;
                try {
                    Role role = roleOpt.orElseThrow(() -> new IllegalArgumentException("Role does not exist."));
                    weightStrict = role.getWeight();
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
    
                return ResponseEntity.ok(weightStrict >= ADMIN_WEIGHT); // true/false podle váhy
            } else {
                System.out.println("No user object in session.");
            }
        } else {
            System.out.println("No session found.");
        }
    
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false); // Pokud není přihlášen

    }




}
