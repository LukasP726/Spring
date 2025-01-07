package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.FriendRequest;
import com.example.demo.model.User;
import com.example.demo.service.FriendService;
import com.example.demo.service.SessionService;
import com.example.demo.service.UserService;

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
        long id = getUserIdFromRequest(request);
        if(id != -1){
            return friendService.isFriend(id, userId);
        }
        return false;
    }

    @PostMapping("/request/{userId}")
    public ResponseEntity<?> sendFriendRequest(@PathVariable Long userId, HttpServletRequest request) {
        long id = getUserIdFromRequest(request);
        if(id != -1){
            friendService.sendFriendRequest(id, userId);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long requestId, HttpServletRequest request) {
        long id = getUserIdFromRequest(request);
        if(id != -1){
            friendService.acceptFriendRequest(requestId, id);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> removeFriend(@PathVariable Long userId, HttpServletRequest request) {
        long id = getUserIdFromRequest(request);
        if(id != -1){
            friendService.deleteFriend(userId, id);
        }
        return ResponseEntity.ok().build();
    }
    


    @GetMapping("/list")
    public ResponseEntity<List<User>> getFriends(HttpServletRequest request) {
        long id = getUserIdFromRequest(request); // Získání username z cookies
        //System.out.println(username);
        List<User> friends = null;
        if(id != -1){
            friends = friendService.getFriends(id);
        }
      
        return ResponseEntity.ok(friends);
    }


    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequest>> getRequests(HttpServletRequest request) {
        long id = getUserIdFromRequest(request); 
        List<FriendRequest> requests = null;
        if(id != -1){
           requests = friendService.getRequests(id);
        }
        return ResponseEntity.ok(requests);
    }
    /* 

    @GetMapping("/requests-dto")
    public ResponseEntity<List<String>> getRequestsDTO(HttpServletRequest request) {
        String username = getUserId
FromRequest(request); 
        List<String> requests = friendService.getRequestsDTO(username);
        return ResponseEntity.ok(requests);
    }
        */
        




    private long getUserIdFromRequest(HttpServletRequest request) {
        // Získání session ID z cookies a validace uživatele
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    String sessionId = cookie.getValue();
                    User user = userService.findBySessionId(sessionId);
                    if (user != null) {
                        return user.getId(); 
                    }
                }
            }
        }
        return -1;
   
    }
}


