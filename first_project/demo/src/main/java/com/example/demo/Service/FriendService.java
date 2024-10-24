package com.example.demo.Service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Model.FriendRequest;
import com.example.demo.Model.Friendship;
import com.example.demo.Model.User;
import com.example.demo.Repository.FriendRequestRepository;
import com.example.demo.Repository.FriendshipRepository;
import com.example.demo.Repository.UserRepository;

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
    public boolean isFriend(String username, Long userId) {
        try {
            Optional<User> optionalUser = userRepository.findByLogin(username);
            if (optionalUser.isPresent()) {
                Long fromUserId = optionalUser.get().getId();
                return friendshipRepository.existsByUserIdAndFriendId(fromUserId, userId);
            } else {
                // Zpracování situace, kdy uživatel nebyl nalezen
                System.out.println("User with login " + username + " not found."); // Místo throw můžete logovat
                return false; // Vrátíme false, pokud uživatel neexistuje
            }
        } catch (Exception e) {
            System.out.println("An error occurred while checking friendship: " + e.getMessage());
            return false; // Můžete také vrátit false při chybě
        }
    }
    
    // Odesílá žádost o přátelství
    public void sendFriendRequest(String fromUsername, Long toUserId) {
        try {
            // Zkontroluj, zda existuje uživatel odesílatel
            Optional<User> optionalUser = userRepository.findByLogin(fromUsername);
            if (optionalUser.isPresent()) {
                Long fromUserId = optionalUser.get().getId();
    
                // Zkontroluj, zda už není přátelství
                if (isFriend(fromUsername, toUserId)) {
                    System.out.println("You are already friends.");
                    return; // Ukončíme metodu, pokud jsou přátelé
                }
    
                // Vytvoř a ulož žádost o přátelství
                FriendRequest request = new FriendRequest();
                request.setFromUserId(fromUserId);
                request.setToUserId(toUserId);
                friendRequestRepository.addFriendRequest(request);
                System.out.println("Friend request sent successfully.");
            } else {
                // Zpracování situace, kdy uživatel nebyl nalezen
                System.out.println("User with login " + fromUsername + " not found.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while sending the friend request: " + e.getMessage());
        }
    }
    

    // Přijímá žádost o přátelství
    public void acceptFriendRequest(Long requestId, String username) {
        // Získání uživatele podle uživatelského jména
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    
        // Najděte žádost o přátelství
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
    
        // Zkontrolujte, zda uživatel je ten, kdo žádost přijal
        if (!request.getToUserId().equals(user.getId())) {
            throw new IllegalArgumentException("You cannot accept this friend request.");
        }
    
        // Ulož přátelství do databáze
        Friendship friendship = new Friendship();
        friendship.setUserId(request.getFromUserId());
        friendship.setFriendId(request.getToUserId());
        friendshipRepository.addFriendship(friendship);
    
        // Odstranění žádosti o přátelství
        friendRequestRepository.deleteFriendRequest(requestId);
    }


    public List<User> getFriends(String username) {
        User user = userRepository.findByLogin(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Získat seznam přátel z FriendshipRepository
        return friendshipRepository.findFriendsByUserId(user.getId());
    }
    
}

