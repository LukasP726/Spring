package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.Post;
import com.example.demo.Repository.PostRepository;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = {"http://localhost:4200","http://192.168.56.1:4200"})
public class PostController {
    @Autowired
    private PostRepository postRepository;

    @GetMapping("/search")
    public ResponseEntity<List<Post>> getPostsByContent(@RequestParam String content) {
        List<Post> posts = postRepository.findByContentContaining(content);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable Long idUser) {
        List<Post> posts = postRepository.findByUserId(idUser);
        return ResponseEntity.ok(posts);
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        postRepository.createPost(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }


    
    @GetMapping("/thread/{idThread}")
    public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable Integer idThread) {
        List<Post> posts = postRepository.findByThreadId(idThread);
        return ResponseEntity.ok(posts);
    }



}

