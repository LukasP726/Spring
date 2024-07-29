package com.example.demo.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Model.Upload;
import com.example.demo.Repository.UploadRepository;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = {"http://localhost:4200","http://192.168.56.1:4200"})
public class FileUploadController {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private UploadRepository uploadRepository;

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Please select a file!", HttpStatus.OK);
        }
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(uploadPath + file.getOriginalFilename());
            Files.write(path, bytes);
            return new ResponseEntity<>("Successfully uploaded - " + file.getOriginalFilename(), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to upload file.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

     @GetMapping("/search")
    public ResponseEntity<List<Upload>> getUploadsByFilename(@RequestParam String filename) {
        List<Upload> uploads = uploadRepository.findByFilenameContaining(filename);
        return ResponseEntity.ok(uploads);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Upload>> getUploadsByUserId(@PathVariable Long userId) {
        List<Upload> uploads = uploadRepository.findByUserId(userId);
        return ResponseEntity.ok(uploads);
    }
}
