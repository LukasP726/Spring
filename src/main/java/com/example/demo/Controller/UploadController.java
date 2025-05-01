package com.example.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.example.demo.model.Upload;
import com.example.demo.service.UploadService;

@RestController
@RequestMapping("/api/uploads")
@CrossOrigin(origins = {"http://localhost:4200","http://192.168.56.1:4200"})
public class UploadController {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private UploadService uploadService;

    /**
     * Endpoint pro nahrání souboru na server.
     * Tento endpoint umožňuje nahrání souboru a uložení informací o nahrávce do databáze.
     * 
     */
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
            // Uložení souboru na disk
            byte[] bytes = file.getBytes();
            Path path = Paths.get(uploadPath + file.getOriginalFilename());
            Files.write(path, bytes);

            // Uložení informací o uploadu do databáze
            Upload upload = new Upload();
            upload.setFilename(file.getOriginalFilename());
            upload.setIdUser(idUser);
            upload.setIdPost(idPost);
            upload.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            uploadService.createUpload(upload);

            return new ResponseEntity<>("Successfully uploaded - " + file.getOriginalFilename(), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to upload file.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint pro vyhledání souborů podle názvu souboru.
     * Tento endpoint umožňuje uživatelům vyhledávat soubory podle fragmentu názvu souboru.
     * 
     */
    @GetMapping("/search")
    public ResponseEntity<List<Upload>> getUploadsByFilename(@RequestParam String filename) {
        List<Upload> uploads = uploadService.findByFilenameContaining(filename);
        return ResponseEntity.ok(uploads);
    }

    /**
     * Endpoint pro vyhledání souborů podle ID uživatele.
     * Tento endpoint vrací seznam všech souborů nahraných konkrétním uživatelem.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Upload>> getUploadsByUserId(@PathVariable Long userId) {
        List<Upload> uploads = uploadService.findByUserId(userId);
        return ResponseEntity.ok(uploads);
    }

    /**
     * Endpoint pro stažení souboru na základě jeho ID.
     * Tento endpoint vrací soubor z úložiště a poskytuje jej ke stažení.
     */
    @GetMapping("/download/{uploadId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long uploadId) {
        // Najděte soubor v databázi na základě ID
        Upload upload = uploadService.findById(uploadId).orElseThrow(() -> new ResourceNotFoundException("File not found"));

        // Cesta k souboru na disku
        Path filePath = Paths.get(uploadPath + upload.getFilename());
        Resource resource = new FileSystemResource(filePath);

        // Získání skutečného názvu souboru
        String originalFilename = upload.getFilename();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFilename + "\"")
                .body(resource);
    }

    /**
     * Endpoint pro získání souborů podle ID příspěvku.
     * Tento endpoint vrací seznam všech souborů připojených k danému příspěvku.
     */
    @GetMapping("/{postId}")
    public ResponseEntity<List<Upload>> getUploadsForPost(@PathVariable Long postId) {
        List<Upload> upload = uploadService.findByPostId(postId);
        return ResponseEntity.ok(upload);
    }

    /**
     * Endpoint pro vyhledávání souborů podle názvu souboru.
     * Tento endpoint umožňuje uživatelům vyhledávat soubory podle názvu souboru.
     */
    @GetMapping("/")
    public List<Upload> searchUploads(@RequestParam(name = "name") String term) {
        return uploadService.findByFilenameContaining(term);
    }

    /**
     * Endpoint pro získání nejnovějších obrázků.
     * Tento endpoint vrací seznam nejnovějších obrázků nahraných do systému.
     */
    @GetMapping("/latest-images")
    public ResponseEntity<List<Upload>> getLatestImages() {
        List<Upload> latestImages = uploadService.getLatestImages();
        return ResponseEntity.ok(latestImages);
    }


    

}
