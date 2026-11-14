
package com.fundio.aichatbot.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fundio.aichatbot.service.DocumentIngestionService;

@RestController
@RequestMapping("/api/documents")
public class DocumentUploadController {

    private final DocumentIngestionService ingestionService;

    public DocumentUploadController(DocumentIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPdf(
            @RequestParam("file") MultipartFile file) {

        try {
            int chunksStored = ingestionService.ingestPdf(file);

            return ResponseEntity.ok(
                    java.util.Map.of(
                            "documentName", file.getOriginalFilename(),
                            "chunksStored", chunksStored,
                            "status", "PDF uploaded and processed successfully"
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    java.util.Map.of("error", e.getMessage())
            );

        } catch (IOException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    java.util.Map.of("error", "Unable to read PDF file")
            );
        }
    }
}
