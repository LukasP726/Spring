package com.example.demo.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.demo.Model.Post;
import com.example.demo.Repository.PostRepository;
import com.example.demo.Repository.UploadRepository;

@Service
public class PostService {

    @Value("${upload.path}")
    private String uploadPath;

    private final PostRepository postRepository;
    private final UploadRepository uploadRepository;


    public PostService(PostRepository postRepository, UploadRepository uploadRepository) {
        this.postRepository=postRepository;
        this.uploadRepository=uploadRepository;
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

    public int updatePost(int id, String content) {
       return postRepository.updatePost(id, content);
    }

    public int deletePost(Long id) {
        int rowsAffected = 0;
        String filename = uploadRepository.getFileNameByPostId(id); // Přizpůsobte podle vašeho úložiště
        System.out.println("filename: "+ filename);
    if (filename != null && !filename.isEmpty()) {
        try {
            Path path = Paths.get(uploadPath + filename);
            Files.delete(path); // Odstraňte soubor
            

            System.out.println("Soubor byl úspěšně odstraněn: " + filename);
        } catch (IOException e) {
            System.err.println("Chyba při odstraňování souboru: " + e.getMessage());
            // Zde můžete vrátit chybu nebo ji logovat
        }
    } else {
        System.err.println("Cesta k souboru nebyla nalezena pro post ID: " + id);
    }

    rowsAffected += uploadRepository.deleteByIdPost(id);
    rowsAffected += postRepository.deletePost(id);
    return rowsAffected;

    }
    
}
