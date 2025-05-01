package com.example.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.demo.model.Post;
import com.example.demo.model.PostDTO;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UploadRepository;

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

    // Vyhledá příspěvky, které obsahují zadaný text v obsahu.
    public List<Post> findByContentContaining(String content) {
        return postRepository.findByContentContaining(content);
    }

    // Získá seznam příspěvků podle ID uživatele.
    public List<Post> findByUserId(Long idUser) {
        return postRepository.findByUserId(idUser);
    }

    // Vytvoří nový příspěvek.
    public void createPost(Post post) {
        postRepository.createPost(post);
    }

    // Získá ID posledního vloženého příspěvku.
    public Integer getLastInsertId() {
        return postRepository.getLastInsertId();
    }

    // Získá seznam příspěvků podle ID vlákna.
    public List<Post> findByThreadId(Integer idThread) {
        return postRepository.findByThreadId(idThread);
    }

    // Získá seznam příspěvků ve formě DTO objektů podle ID vlákna.
    public List<PostDTO> findPostDTOsByThreadId(Integer idThread) {
        return postRepository.findPostDTOsByThreadId(idThread);
    }

    // Získá příspěvek podle jeho ID.
    public Optional<Post> getPostById(Integer id) {
        return postRepository.getPostById(id);
    }

    // Aktualizuje obsah příspěvku podle jeho ID.
    public int updatePost(int id, String content) {
        return postRepository.updatePost(id, content);
    }

    // Odstraní příspěvek a jeho příslušný soubor (pokud existuje).
    public int deletePost(Long id) {
        int rowsAffected = 0;
        String filename = uploadRepository.getFileNameByPostId(id); // Přizpůsobte podle vašeho úložiště
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
