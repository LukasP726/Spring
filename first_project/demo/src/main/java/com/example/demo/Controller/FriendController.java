package com.example.demo.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.FriendRequest;
import com.example.demo.Model.User;
import com.example.demo.Service.FriendService;
import com.example.demo.Service.SessionService;
import com.example.demo.Service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;


@RestController
@RequestMapping("/api/friends")
public class FriendController {

    @Autowired
    private FriendService friendService;
    @Autowired
    private UserService userService;

    @GetMapping("/is-friend/{userId}")
    public boolean checkIfFriend(@PathVariable Long userId, HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        return friendService.isFriend(username, userId);
    }

    @PostMapping("/request/{userId}")
    public ResponseEntity<?> sendFriendRequest(@PathVariable Long userId, HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        friendService.sendFriendRequest(username, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long requestId, HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        friendService.acceptFriendRequest(requestId, username);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/list")
    public ResponseEntity<List<User>> getFriends(HttpServletRequest request) {
        String username = getUsernameFromRequest(request); // Získání username z cookies
        List<User> friends = friendService.getFriends(username);
        return ResponseEntity.ok(friends);
    }
    /* 

    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequest>> getRequests(HttpServletRequest request) {
        String username = getUsernameFromRequest(request); 
        List<FriendRequest> requests = friendService.getRequests(username);
        return ResponseEntity.ok(requests);
    }
        */




    private String getUsernameFromRequest(HttpServletRequest request) {
        // Získání session ID z cookies a validace uživatele
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    String sessionId = cookie.getValue();
                    User user = userService.findBySessionId(sessionId);
                    if (user != null) {
                        return user.getLogin(); // Vraťte uživatelské jméno
                    }
                }
            }
        }
        throw new IllegalArgumentException("User is not authenticated");
    }
}


