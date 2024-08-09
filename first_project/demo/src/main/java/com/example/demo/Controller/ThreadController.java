package com.example.demo.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repository.ThreadRepository;
import com.example.demo.Service.ThreadService;
import com.example.demo.Model.Post;
import com.example.demo.Model.Thread;
import com.example.demo.Model.User;

@RestController
@RequestMapping("/api/threads")
@CrossOrigin(origins = {"http://localhost:4200","http://192.168.56.1:4200"})
public class ThreadController {
    @Autowired
    //private ThreadRepository threadRepository;
    private ThreadService threadService;

    @GetMapping("/search")
    public ResponseEntity<List<Thread>> getThreadsByName(@RequestParam String name) {
        List<Thread> threads = threadService.findByNameContaining(name);
        return ResponseEntity.ok(threads);
    }

    @GetMapping("/")
    public List<Thread> searchPosts(@RequestParam(name = "name") String term) {
        return threadService.findByNameContaining(term);
    }

    @PostMapping
    public ResponseEntity<Thread> createThread(@RequestBody Thread thread) {
        threadService.createThread(thread);
        return ResponseEntity.status(HttpStatus.CREATED).body(thread);
    }

    @GetMapping
    public List<Thread> getAllThreads() {
        return threadService.getAllThreads();
    }

    @GetMapping("/{id}")
    public Thread getThreadById(@PathVariable Integer id){
        return threadService.getThreadById(id)
            .orElseThrow(() -> new RuntimeException("Thread not found with id " + id));
    }



    
    @GetMapping("/user/{idUser}")
    public ResponseEntity<List<Thread>> getPostsByUserId(@PathVariable Integer idUser) {
        List<Thread> thread = threadService.findByUserId(idUser);
        return ResponseEntity.ok(thread);
    }


    
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        Thread thread = threadService.getThreadById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
        threadService.deleteById(id);
    }

    
}

