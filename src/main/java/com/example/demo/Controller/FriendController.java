package com.example.demo.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.model.FriendRequest;
import com.example.demo.model.FriendRequestDTO;
import com.example.demo.model.User;
import com.example.demo.service.FriendService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    @Autowired
    private FriendService friendService;

    /**
     * Kontroluje, zda je aktuální uživatel ve vztahu přátelství s jiným uživatelem.
     * Pokud nelze zjistit ID přihlášeného uživatele, vrací false.
     */
    @GetMapping("/is-friend/{userId}")
    public boolean checkIfFriend(@PathVariable Long userId, HttpServletRequest request) {
        long id = getUserIdFromRequest(request);
        if(id != -1){
            return friendService.isFriend(id, userId);
        }
        return false;
    }
    
    /**
     * Odesílá žádost o přátelství z účtu aktuálně přihlášeného uživatele.
     * Pokud nelze zjistit ID uživatele, nedojde k žádné akci.
     */
    @PostMapping("/request/{userId}")
    public ResponseEntity<?> sendFriendRequest(@PathVariable Long userId, HttpServletRequest request) {
        long id = getUserIdFromRequest(request);
        if(id != -1){
            friendService.sendFriendRequest(id, userId);
        }
        return ResponseEntity.ok().build();
    }


    /**
     * Přijímá žádost o přátelství na základě ID žádosti.
     * Pokud nelze zjistit ID aktuálního uživatele, nic se nestane.
     */
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long requestId, HttpServletRequest request) {
        long id = getUserIdFromRequest(request);
        if(id != -1){
            friendService.acceptFriendRequest(requestId, id);
        }
        return ResponseEntity.ok().build();
    }


    /**
     * Odstraňuje uživatele z přátel přihlášeného uživatele.
     * Pokud ID přihlášeného uživatele není získáno, nic se neprovede.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> removeFriend(@PathVariable Long userId, HttpServletRequest request) {
        long id = getUserIdFromRequest(request);
        if(id != -1){
            friendService.deleteFriend(userId, id);
        }
        return ResponseEntity.ok().build();
    }
    

    /**
     * Vrací seznam přátel aktuálně přihlášeného uživatele.
     * Pokud se ID uživatele nezjistí, vrací prázdný seznam (null).
     */
    @GetMapping("/list")
    public ResponseEntity<List<User>> getFriends(HttpServletRequest request) {
        long id = getUserIdFromRequest(request); // Získání username z cookies
        List<User> friends = null;
        if(id != -1){
            friends = friendService.getFriends(id);
        }
      
        return ResponseEntity.ok(friends);
    }

    /**
     * Vrací seznam příchozích žádostí o přátelství pro přihlášeného uživatele.
     * Pokud není uživatel autentizován, vrací null.
     */
    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequest>> getRequests(HttpServletRequest request) {
        long id = getUserIdFromRequest(request); 
        List<FriendRequest> requests = null;
        if(id != -1){
           requests = friendService.getRequests(id);
        }
        return ResponseEntity.ok(requests);
    }

    /**
     * Vrací příchozí žádosti o přátelství ve formátu DTO.
     * DTO může obsahovat jen omezené nebo přizpůsobené informace pro frontend.
     */
    @GetMapping("/requestsDTO")
    public ResponseEntity<List<FriendRequestDTO>> getRequestsDTO(HttpServletRequest request) {
        long id = getUserIdFromRequest(request); 
        List<FriendRequestDTO> requests = null;
        if(id != -1){
           requests = friendService.getRequestsDTO(id);
        }
        return ResponseEntity.ok(requests);
    }


    /**
    * Získá ID aktuálně přihlášeného uživatele ze session.
    * Vrací -1, pokud není session nebo pokud není uživatel přihlášen.
    */
    private long getUserIdFromRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // false = nevytvářet novou session
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                return user.getId();
            }
        }
        return -1;
    }
    





}


