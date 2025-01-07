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

    public void createUpload(Upload upload) {
       uploadRepository.createUpload(upload);
    }

    public List<Upload> findByFilenameContaining(String filename) {
      return uploadRepository.findByFilenameContaining(filename);
    }

    public List<Upload> findByUserId(Long userId) {
        return uploadRepository.findByUserId(userId);
    }

    public List<Upload> findByPostId(Long postId) {
       return uploadRepository.findByPostId(postId);
    }




    public List<Upload> getLatestImages() {
        return uploadRepository.findTop3ImagesOrderByCreatedAtDesc();
    }

    public  Optional<Upload> findById(Long uploadId) {
        return uploadRepository.findById(uploadId);
    }
}
