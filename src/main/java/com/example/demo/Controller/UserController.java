package com.example.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Role;
import com.example.demo.model.Session;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.ChangePasswordRequest;
import com.example.demo.request.PasswordVerificationRequest;
import com.example.demo.service.AuthService;
import com.example.demo.service.RoleService;
import com.example.demo.service.SessionService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Observable;
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
    private AuthService authService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
/*
    @Autowired
    private Md5PasswordEncoder passwordEncoder;
     */

/* 
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
        */




@GetMapping
public ResponseEntity<List<User>> getAllUser(HttpServletRequest request) {
    // 1. Získání aktuálního uživatele podle session
    String sessionId = getSessionIdFromRequest(request);
    if (sessionId == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    User currentUser = userService.findBySessionId(sessionId);
    if (currentUser == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // 2. Získání všech uživatelů a filtrování
    List<User> users = userService.getAllUsers();
    List<User> filteredUsers = users.stream()
        .filter(user -> !user.getId().equals(currentUser.getId())) // Nezahrnout aktuálního uživatele
        .filter(user -> user.getIdRole() > currentUser.getIdRole()) // Zahrnout pouze uživatele s menším idRole
        .collect(Collectors.toList());

    return ResponseEntity.ok(filteredUsers);
}


// Pomocná metoda pro získání session ID
private String getSessionIdFromRequest(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("JSESSIONID".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
    }
    return null;
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
        user.setIsBanned(userDetails.getIsBanned());
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
    public List<User> searchUsers(@RequestParam(name = "name") String term) {
        return userService.searchByName(term);
    }


    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(HttpServletRequest request) {
        try {
            sessionService.checkAndExtendSession(request);
    
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("JSESSIONID".equals(cookie.getName())) {
                        String sessionId = cookie.getValue();
                        System.out.println("Session ID(getCurrentUser): " + sessionId);
    
                        User user = userService.findBySessionId(sessionId);
                        if (user != null) {
                            return ResponseEntity.ok(user);
                        } else {
                            System.out.println("No user found for session ID.");
                            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                        }
                    }
                }
            }
    
            System.out.println("No JSESSIONID cookie found.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            System.err.println("Error in getCurrentUser: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
        
    
/*
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(HttpServletRequest request) {
    // Získání session ID z aktuální session
    HttpSession session = request.getSession(false);  // Získat aktuální session, pokud existuje
    if (session == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();  // Není žádná session, vrátí 401 Unauthorized
    }

    String sessionId = session.getId();  // Získání ID session

    // Načtení uživatele podle ID session
    Optional<User> userOptional = userService.findBySessionId(sessionId);
    if (userOptional.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();  // Uživatele nelze najít, vrátí 401 Unauthorized
    }

    return ResponseEntity.ok(userOptional.get());
}
 */


    @GetMapping("/top-users")
    public List<User> getTopUsers() {
        return userService.getTopUsers();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminEndpoint() {
        return ResponseEntity.ok().body("Welcome, Admin!");
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
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request, Authentication authentication) {
        String oldPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();
    
        if (oldPassword == null) {
            return ResponseEntity.badRequest().body("OldPassword cannot be null");
        }

        if (newPassword == null) {
            return ResponseEntity.badRequest().body("NewPassword cannot be null");
        }

        

        Optional<User> optionalUser = userService.findByName(authentication.getName());
        
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        User currentUser = optionalUser.get();

        // Ověření stávajícího hesla
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Current password is incorrect");
        }

        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be null or empty");
        }

        // Ověření, že nová hesla se shodují
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            return ResponseEntity.badRequest().body("New passwords do not match");
        }

        // Aktualizace hesla
        currentUser.setPassword(request.getNewPassword());
        if(userService.saveUser(currentUser) > 0){
            return ResponseEntity.ok("Password changed successfully");
        }
        else{
            return ResponseEntity.badRequest().body("Password did not change");
            }
       
    }


    @GetMapping("/is-admin")
    public ResponseEntity<Boolean> isAdmin(HttpServletRequest request) {
        //HttpSession session = request.getSession(false);
        Cookie[] cookies = request.getCookies();
        //if (session != null) {
        if (cookies != null) {
            //User user = (User) session.getAttribute("user");
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    String sessionId = cookie.getValue();
                    User user = userService.findBySessionId(sessionId);
                    if (user != null) {
                        // Vrátí true pokud má idRole rovno 1, jinak false
                        Long id = user.getIdRole();
                        Optional<Role> role = roleService.getRoleById(id);
                        int weightStrict = 0;
                        try {
                            weightStrict = role.orElseThrow(() -> new IllegalArgumentException("Role does not exist."))
                                                    .getWeight();
                            //System.out.println("Strict weight: " + weightStrict);
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
        
                        return ResponseEntity.ok(weightStrict >= ADMIN_WEIGHT);
                    }
                   
                }
            }
           
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false); // Pokud není přihlášen, vrátí false
    }



}
