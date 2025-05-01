package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.example.demo.model.Thread;
import com.example.demo.repository.ThreadRepository;
import com.example.demo.repository.UserRepository;

@Service
public class ThreadService {
 private final ThreadRepository threadRepository;
 private final UserRepository userRepository;
 


    public ThreadService(ThreadRepository threadRepository, UserRepository userRepository) {
        this.threadRepository=threadRepository;
        this.userRepository=userRepository;
    }



    // Vyhledá vlákna podle názvu obsahujícího zadaný termín.
    public List<Thread> findByNameContaining(String name) {
        return threadRepository.findByNameContaining(name);
    }

    // Vytvoří nové diskuzní vlákno.
    public void createThread(Thread thread) {
        threadRepository.createThread(thread);
    }

    // Získá seznam všech diskuzních vláken.
    public List<Thread> getAllThreads() {
        return threadRepository.getAllThreads();
    }

    // Získá diskuzní vlákno podle jeho ID.
    public Optional<Thread> getThreadById(Integer id) {
        return threadRepository.getThreadById(id);
    }

    // Získá seznam diskuzních vláken vytvořených konkrétním uživatelem podle jeho ID.
    public List<Thread> findByUserId(Integer idUser) {
        return threadRepository.findByUserId(idUser);
    }

    // Odstraní diskuzní vlákno podle jeho ID.
    public void deleteById(Integer id) {
        threadRepository.deleteById(id);
    }

    // Získá login vlastníka diskuzního vlákna podle jeho ID.
    public String findOwnerByThreadId(int idThread) {
        int idUser = threadRepository.findOwnerByThreadId(idThread);
        return userRepository.getLoginByIdUser(idUser);
    }


}
