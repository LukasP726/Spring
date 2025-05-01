package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.example.demo.model.Upload;
import com.example.demo.repository.UploadRepository;

@Service
public class UploadService {
    private final UploadRepository uploadRepository;
    public UploadService(UploadRepository uploadRepository){
        this.uploadRepository=uploadRepository;
    }

    // Vytvoří nový upload.
    public void createUpload(Upload upload) {
        uploadRepository.createUpload(upload);
    }

    // Vyhledá uploady podle názvu souboru obsahujícího zadaný termín.
    public List<Upload> findByFilenameContaining(String filename) {
        return uploadRepository.findByFilenameContaining(filename);
    }

    // Získá všechny uploady konkrétního uživatele podle jeho ID.
    public List<Upload> findByUserId(Long userId) {
        return uploadRepository.findByUserId(userId);
    }

    // Získá všechny uploady připojené k určitému příspěvku podle ID příspěvku.
    public List<Upload> findByPostId(Long postId) {
        return uploadRepository.findByPostId(postId);
    }

    // Získá 3 nejnovější obrázky seřazené podle data vytvoření.
    public List<Upload> getLatestImages() {
        return uploadRepository.findTop3ImagesOrderByCreatedAtDesc();
    }

    // Získá upload podle jeho ID.
    public Optional<Upload> findById(Long uploadId) {
        return uploadRepository.findById(uploadId);
    }

}
