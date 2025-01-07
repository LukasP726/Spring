package com.example.demo.Controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserService;

@RestController
@RequestMapping("/api/register")
@CrossOrigin(origins = {"http://localhost:4200","http://192.168.56.1:4200"})
public class RegistrationController {
    private static final Logger logger = Logger.getLogger(RegistrationController.class.getName());
    private final UserService userService;
    //private final UserRepository userRepository;
    
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }
    
    /* 
    public RegistrationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
        */

    @PostMapping
    public ResponseEntity<User> register(@RequestBody User user) {
        logger.log(Level.INFO, "Received registration request: {0}", user);
        int result = userService.saveUser(user);
        if (result == 1) {
            logger.log(Level.INFO, "Registration successful for user: {0}", user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } else {
            logger.log(Level.SEVERE, "Registration failed for user: {0}", user);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

