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

    public String findOwnerByThreadId(int idThread) {

        int idUser = threadRepository.findOwnerByThreadId(idThread);
        return userRepository.getLoginByIdUser(idUser);

         
    }


}
