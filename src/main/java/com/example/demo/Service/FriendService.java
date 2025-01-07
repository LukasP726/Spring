package com.example.demo.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.FriendRequest;
import com.example.demo.model.Friendship;
import com.example.demo.model.User;
import com.example.demo.repository.FriendRequestRepository;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.repository.UserRepository;

import java.util.List;

@Service
public class FriendService {

    @Autowired
    private UserRepository userRepository; // Repo pro uživatele
    @Autowired
    private FriendRequestRepository friendRequestRepository; // Repo pro žádosti o přátelství
    @Autowired
    private FriendshipRepository friendshipRepository; // Repo pro přátelství

    // Zkontroluje, zda je uživatel již přítelem
    public boolean isFriend(Long fromUserId, Long toUserId) {
        try {
                //System.out.println("isFriend: "+friendshipRepository.existsByUserIdAndFriendId(fromUserId, userId));
                return friendshipRepository.existsByUserIdAndFriendId(fromUserId, toUserId);
        
        } catch (Exception e) {
            System.out.println("An error occurred while checking friendship: " + e.getMessage());
            return false; // Můžete také vrátit false při chybě
        }
    }
    
    // Odesílá žádost o přátelství
    public void sendFriendRequest(Long fromUserId, Long toUserId) {
        try {
            // Zkontroluj, zda existuje uživatel odesílatel
        
         
                
    
                // Zkontroluj, zda už není přátelství
                if (isFriend(fromUserId, toUserId)) {
                    System.out.println("You are already friends.");
                    return; // Ukončíme metodu, pokud jsou přátelé
                }
    
                // Vytvoř a ulož žádost o přátelství
                FriendRequest request = new FriendRequest();
                request.setFromUserId(fromUserId);
                request.setToUserId(toUserId);
                friendRequestRepository.addFriendRequest(request);
                System.out.println("Friend request sent successfully.");
           
        } catch (Exception e) {
            System.out.println("An error occurred while sending the friend request: " + e.getMessage());
        }
    }
    

    // Přijímá žádost o přátelství
    public void acceptFriendRequest(Long requestId, Long id) {
        // Získání uživatele podle uživatelského jména
       
    
        // Najděte žádost o přátelství
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
    
        // Zkontrolujte, zda uživatel je ten, kdo žádost přijal
        if (!request.getToUserId().equals(id)) {
            throw new IllegalArgumentException("You cannot accept this friend request.");
        }
    
        // Ulož přátelství do databáze
        Friendship friendship1 = new Friendship();
        friendship1.setUserId(request.getFromUserId());
        friendship1.setFriendId(request.getToUserId());
        friendshipRepository.addFriendship(friendship1);

        Friendship friendship2 = new Friendship();
        friendship2.setUserId(request.getToUserId());
        friendship2.setFriendId(request.getFromUserId());
        friendshipRepository.addFriendship(friendship2);
    
        // Odstranění žádosti o přátelství
        friendRequestRepository.deleteFriendRequest(requestId);
    }


    public List<User> getFriends(Long id) {

        return friendshipRepository.findFriendsByUserId(id);
    }

    public List<FriendRequest> getRequests(Long id) {
        return friendRequestRepository.getRequests(id);
    }
    /* 
    public List<String> getRequestsDTO(String username) {
        return friendRequestRepository.getRequestsUsersLogin(username);
    }
        */

    public void deleteFriend(Long userId, Long id ){
     

        friendshipRepository.deleteFriendship(userId, id);
        friendshipRepository.deleteFriendship(id, userId);
    }
    
}

