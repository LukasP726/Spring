package com.example.demo.Controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.UrlResource;

import com.example.demo.Model.Post;
import com.example.demo.Model.Upload;
import com.example.demo.Repository.UploadRepository;

@RestController
@RequestMapping("/api/uploads")
@CrossOrigin(origins = {"http://localhost:4200","http://192.168.56.1:4200"})
public class UploadController {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private UploadRepository uploadRepository;

    @PostMapping
    public ResponseEntity<?> uploadFile(
        @RequestParam("file") MultipartFile file,
        @RequestParam("idUser") Integer idUser,
        @RequestParam("idPost") Integer idPost
    ) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Please select a file!", HttpStatus.BAD_REQUEST);
        }

        try {
            // Uložení souboru
            byte[] bytes = file.getBytes();
            Path path = Paths.get(uploadPath + file.getOriginalFilename());
            Files.write(path, bytes);

            // Uložení informací o uploadu do databáze
            Upload upload = new Upload();
            upload.setFilename(file.getOriginalFilename());
            upload.setIdUser(idUser);
            upload.setIdPost(idPost);
            upload.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            uploadRepository.createUpload(upload);

            return new ResponseEntity<>("Successfully uploaded - " + file.getOriginalFilename(), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to upload file.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Další endpointy

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

    /* 
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) throws MalformedURLException {
        Path filePath = Paths.get(uploadPath + filename);
        Resource resource = new UrlResource(filePath.toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
    */
    @GetMapping("/download/{uploadId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long uploadId) {
    Upload upload = uploadRepository.findById(uploadId).orElseThrow(() -> new ResourceNotFoundException("File not found"));
    Path filePath = Paths.get(uploadPath + upload.getFilename());
    Resource resource = new FileSystemResource(filePath);
    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);
    }


    @GetMapping("/{postId}")
    public ResponseEntity<List<Upload>> getUploadsForPost(@PathVariable Long postId) {
    List<Upload> upload = uploadRepository.findByPostId(postId);
    return ResponseEntity.ok(upload);
    }

    @GetMapping("/")
    public List<Upload> searchUploads(@RequestParam(name = "name") String term) {
        return uploadRepository.findByFilenameContaining(term);
    }

    

}
