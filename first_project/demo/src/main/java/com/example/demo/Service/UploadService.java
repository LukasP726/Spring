package com.example.demo.Service;

import java.util.List;

import com.example.demo.Model.Upload;
import com.example.demo.Repository.UploadRepository;


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

}
