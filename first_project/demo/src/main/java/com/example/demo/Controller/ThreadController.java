package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repository.ThreadRepository;

@RestController
@RequestMapping("/api/threads")
public class ThreadController {
    @Autowired
    private ThreadRepository threadRepository;

    @GetMapping("/search")
    public ResponseEntity<List<Thread>> getThreadsByName(@RequestParam String name) {
        List<Thread> threads = threadRepository.findByNameContaining(name);
        return ResponseEntity.ok(threads);
    }

    @PostMapping
    public ResponseEntity<Thread> createThread(@RequestBody Thread thread) {
        threadRepository.createThread(thread);
        return ResponseEntity.status(HttpStatus.CREATED).body(thread);
    }

    // Další metody pro CRUD operace
}

