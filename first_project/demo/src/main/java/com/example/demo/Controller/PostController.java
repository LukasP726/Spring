package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.example.demo.Model.Post;
import com.example.demo.Model.PostDTO;
import com.example.demo.Model.Upload;
import com.example.demo.Model.User;
import com.example.demo.Repository.PostRepository;
import com.example.demo.Service.PostService;
import com.example.demo.Service.ThreadService;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = {"http://localhost:4200","http://192.168.56.1:4200"})
public class PostController {
    @Autowired
    //private PostRepository postRepository;
    private PostService postService;



    @GetMapping("/search")
    public ResponseEntity<List<Post>> getPostsByContent(@RequestParam String content) {
        List<Post> posts = postService.findByContentContaining(content);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{idUser}")
    public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable Long idUser) {
        List<Post> posts = postService.findByUserId(idUser);
        return ResponseEntity.ok(posts);
    }

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
    
        


        /* 
   
    @GetMapping("/thread/{idThread}")
    public ResponseEntity<List<Post>> getPostsByThreadId(@PathVariable Integer idThread) {
        List<Post> posts = postService.findByThreadId(idThread);
        return ResponseEntity.ok(posts);
    }
        
*/
    @GetMapping("/thread/{idThread}")
    public ResponseEntity<List<PostDTO>> getPostsByThreadId(@PathVariable Integer idThread) {
        List<PostDTO> posts = postService.findPostDTOsByThreadId(idThread);
        return ResponseEntity.ok(posts);
    }












    @GetMapping("/")
    public List<Post> searchPosts(@RequestParam(name = "name") String term) {
        return postService.findByContentContaining(term);
    }


    @GetMapping("/{id}")
    public Post getPostById(@PathVariable Integer id) {
        return postService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updatePost(@PathVariable int id, @RequestBody Post postDetails) {
        int rowsAffected = postService.updatePost(id, postDetails.getContent());
        if (rowsAffected > 0) {
            return ResponseEntity.ok().build();  // Úspěšná aktualizace
        } else {
            return ResponseEntity.notFound().build();  // Příspěvek nenalezen nebo nebyl aktualizován
        }
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }




}

