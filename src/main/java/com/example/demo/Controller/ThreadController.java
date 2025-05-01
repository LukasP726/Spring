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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.model.Thread;
import com.example.demo.service.ThreadService;

@RestController
@RequestMapping("/api/threads")
@CrossOrigin(origins = {"http://localhost:4200","http://192.168.56.1:4200"})
public class ThreadController {
    @Autowired
    private ThreadService threadService;

    /**
     * Endpoint pro vyhledání diskuzních vláken podle názvu.
     * 
     * Tento endpoint přijímá parametr `name`, který vyhledává diskuzní vlákna podle názvu obsahujícího tento termín.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Thread>> getThreadsByName(@RequestParam String name) {
        List<Thread> threads = threadService.findByNameContaining(name);
        return ResponseEntity.ok(threads);
    }

    /**
     * Endpoint pro vyhledání diskuzních vláken podle názvu, kdy je specifikován parametr `name`.
     * Tento endpoint vrací seznam vláken, která obsahují hledaný termín v názvu.
     * 
     */
    @GetMapping("/")
    public List<Thread> searchPosts(@RequestParam(name = "name") String term) {
        return threadService.findByNameContaining(term);
    }


    
    /**
     * Endpoint pro vytvoření nového diskuzního vlákna.
     * 
     * Tento endpoint přijímá JSON objekt `Thread` a uloží ho do databáze. Vrátí status 201 (CREATED) a vrátí uložený objekt vlákna.
     * 
     */
    @PostMapping
    public ResponseEntity<Thread> createThread(@RequestBody Thread thread) {
        threadService.createThread(thread);
        return ResponseEntity.status(HttpStatus.CREATED).body(thread);
    }

    /**
     * Endpoint pro získání všech diskuzních vláken.
     * 
     * Tento endpoint vrací seznam všech diskuzních vláken v systému.
     * 
     */
    @GetMapping
    public List<Thread> getAllThreads() {
        return threadService.getAllThreads();
    }

    /**
     * Endpoint pro získání konkrétního diskuzního vlákna podle ID.
     * 
     * Tento endpoint vrací jedno vlákno na základě jeho ID. Pokud vlákno neexistuje, vyvolá výjimku.
     * 
     */
    @GetMapping("/{id}")
    public Thread getThreadById(@PathVariable Integer id){
        return threadService.getThreadById(id)
            .orElseThrow(() -> new RuntimeException("Thread not found with id " + id));
    }

    /**
     * Endpoint pro získání všech vláken podle ID uživatele, který je vlastníkem vláken.
     * 
     * Tento endpoint vrací seznam vláken, které jsou přiřazeny konkrétnímu uživateli (ID uživatele).
     * 
     */
    @GetMapping("/user/{idUser}")
    public ResponseEntity<List<Thread>> getPostsByUserId(@PathVariable Integer idUser) {
        List<Thread> thread = threadService.findByUserId(idUser);
        return ResponseEntity.ok(thread);
    }

    /**
     * Endpoint pro smazání diskuzního vlákna podle ID.
     * 
     * Tento endpoint umožňuje smazání vlákna na základě jeho ID. Pokud vlákno neexistuje, vyvolá se výjimka.
     * 
     */
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        Thread thread = threadService.getThreadById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
        threadService.deleteById(id);
    }

    /**
     * Endpoint pro získání vlastníka diskuzního vlákna na základě jeho ID.
     * 
     * Tento endpoint vrací jméno vlastníka diskuzního vlákna. Pokud je vlákno nalezeno a má vlastníka, vrátí se jeho jméno.
     */
    @GetMapping("/{idThread}/owner")
    public ResponseEntity<String> getOwnerOfThread(@PathVariable int idThread) {         
        String ownerName = threadService.findOwnerByThreadId(idThread);
        if (ownerName != null) {
            return ResponseEntity.ok(ownerName);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
}

