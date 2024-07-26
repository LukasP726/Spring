package com.example.demo.Controller;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.User;
import com.example.demo.Service.UserService;

@RestController
@RequestMapping("/api/register")
@CrossOrigin(origins = {"http://localhost:4200","http://192.168.56.1:4200"})
public class RegistrationController {
    private static final Logger logger = Logger.getLogger(RegistrationController.class.getName());
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User register(@RequestBody User user) {
        logger.log(Level.INFO, "Received registration request: {0}", user);
        int result = userService.saveUser(user);
        if (result == 1) {
            logger.log(Level.INFO, "Registration successful for user: {0}", user);
            return user; // Úspěšná registrace
        } else {
            logger.log(Level.SEVERE, "Registration failed for user: {0}", user);
            //throw new RuntimeException("Failed to register user");
            throw new RuntimeException("Failed to register user");
        }

    }
}

