package com.example.demo.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.model.Post;
import com.example.demo.model.PostDTO;
import com.example.demo.service.PostService;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = {"http://localhost:4200","http://192.168.56.1:4200"})
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * Endpoint pro vyhledávání příspěvků na základě obsahu.
     * 
     * Hledání příspěvků, které obsahují daný text v poli 'content'.
     * Vrací seznam příspěvků, které odpovídají hledanému obsahu.
     *
     */
    @GetMapping("/search")
    public ResponseEntity<List<Post>> getPostsByContent(@RequestParam String content) {
        List<Post> posts = postService.findByContentContaining(content);
        return ResponseEntity.ok(posts);
    }
    


    
    /**
     * Endpoint pro získání příspěvků podle ID uživatele.
     * 
     * Vrací seznam příspěvků patřících určitému uživateli, identifikovanému pomocí jeho ID.
     * 
     */
    @GetMapping("/user/{idUser}")
    public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable Long idUser) {
        List<Post> posts = postService.findByUserId(idUser);
        return ResponseEntity.ok(posts);
    }


    /**
     * Endpoint pro vytvoření nového příspěvku.
     * 
     * Příspěvek je vytvořen na základě dat odeslaných v těle požadavku.
     * Uloží příspěvek do databáze a vrací objekt příspěvku včetně vygenerovaného ID.
     */
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        // Nastavíme potřebné hodnoty, např. ID uživatele, pokud není předáno frontendem
        // post.setIdUser(someUserId);
    
        // Uložení příspěvku do databáze
        postService.createPost(post);
    
        // Získání vygenerovaného ID
        Integer generatedId = postService.getLastInsertId();
    
        // Nastavení vygenerovaného ID a dalších atributů zpět do objektu post
        post.setId(generatedId);
    
        // Příklad: nastavení dalších atributů, pokud nejsou správně inicializovány
        post.setIdUser(post.getIdUser());
        post.setIdUser(post.getIdUser());
        post.setIdThread(post.getIdThread());
        post.setCreatedAt(post.getCreatedAt());
    
        // Vytvoření a vrácení ResponseEntity s naplněným objektem Post
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }
    
        

    /**
     * Endpoint pro získání příspěvků podle ID vlákna.
     * 
     * Vrací seznam příspěvků spojených s konkrétním diskuzním vláknem, identifikovaným pomocí jeho ID.
     */
    @GetMapping("/thread/{idThread}")
    public ResponseEntity<List<PostDTO>> getPostsByThreadId(@PathVariable Integer idThread) {
        List<PostDTO> posts = postService.findPostDTOsByThreadId(idThread);
        return ResponseEntity.ok(posts);
    }


    /**
     * Endpoint pro vyhledávání příspěvků podle termínu v obsahu.
     * 
     * Hledá příspěvky obsahující zadaný termín v textu.
     */
    @GetMapping("/")
    public List<Post> searchPosts(@RequestParam(name = "name") String term) {
        return postService.findByContentContaining(term);
    }

    /**
     * Endpoint pro získání konkrétního příspěvku podle jeho ID.
     * 
     * Pokud příspěvek s daným ID neexistuje, vyvolá výjimku a vrátí odpověď s chybou.
     */
    @GetMapping("/{id}")
    public Post getPostById(@PathVariable Integer id) {
        return postService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    /**
     * Endpoint pro aktualizaci příspěvku.
     * 
     * Příspěvek je aktualizován na základě dat, která jsou odeslána v těle požadavku.
     * Pokud příspěvek neexistuje nebo nebyl aktualizován, vrátí odpověď s chybou.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePost(@PathVariable int id, @RequestBody Post postDetails) {
        int rowsAffected = postService.updatePost(id, postDetails.getContent());
        if (rowsAffected > 0) {
            return ResponseEntity.ok().build();  // Úspěšná aktualizace
        } else {
            return ResponseEntity.notFound().build();  // Příspěvek nenalezen nebo nebyl aktualizován
        }
    }

    /**
     * Endpoint pro smazání příspěvku.
     * 
     * Příspěvek je odstraněn podle jeho ID. Vrací odpověď s HTTP kódem 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }




}

