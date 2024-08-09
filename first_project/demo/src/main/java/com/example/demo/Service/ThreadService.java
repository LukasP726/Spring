package com.example.demo.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.example.demo.Model.Thread;
import com.example.demo.Repository.ThreadRepository;

@Service
public class ThreadService {
 private final ThreadRepository threadRepository;
 


    public ThreadService(ThreadRepository threadRepository) {
        this.threadRepository=threadRepository;
    }



    public List<Thread> findByNameContaining(String name) {
        return threadRepository.findByNameContaining(name);
    }



    public void createThread(Thread thread) {
        threadRepository.createThread(thread);
    }



    public List<Thread> getAllThreads() {
       return threadRepository.getAllThreads();
    }



    public Optional<Thread> getThreadById(Integer id) {
       return threadRepository.getThreadById(id);
    }



    public List<Thread> findByUserId(Integer idUser) {
        return threadRepository.findByUserId(idUser);
    }



    public void deleteById(Integer id) {
      threadRepository.deleteById(id);
    }


}
