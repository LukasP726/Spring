package com.example.demo.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.example.demo.Model.Post;
import com.example.demo.Repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;
 


    public PostService(PostRepository postRepository) {
        this.postRepository=postRepository;
    }

    public List<Post> findByContentContaining(String content) {
        return postRepository.findByContentContaining(content);
     
    }

    public List<Post> findByUserId(Long idUser) {
       return postRepository.findByUserId(idUser);
    }

    public void createPost(Post post) {
        postRepository.createPost(post);
    }

    public Integer getLastInsertId() {
       return postRepository.getLastInsertId();
    }

    public List<Post> findByThreadId(Integer idThread) {
      return postRepository.findByThreadId(idThread);
    }

    public Optional<Post> getPostById(Integer id) {
       return postRepository.getPostById(id);
    }
    
}
