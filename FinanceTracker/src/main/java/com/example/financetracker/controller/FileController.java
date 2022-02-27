package com.example.financetracker.controller;


import com.example.financetracker.model.dto.transactionDTOs.TransactionByFiltersRequestDTO;
import com.example.financetracker.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;


@RestController
@PreAuthorize("hasRole('ROLE_USER')")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/files/profile_images/{filename}/download")
    public ResponseEntity<String> downloadProfileImage(@PathVariable("filename") String filename, HttpServletResponse response){
        fileService.downloadProfileImage(filename, response);
        return ResponseEntity.ok().body("\"message\": \"Image successfully downloaded.\"\n" + "\"timestamp\": "
                + LocalDateTime.now());
    }


    @GetMapping("/files/category_icons/{filename}/download")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> downloadCategoryIcon(@PathVariable("filename") String filename, HttpServletResponse response){
        fileService.downloadCategoryIcon(filename, response);
        return ResponseEntity.ok().body("\"message\": \"Category icon successfully downloaded.\"\n" + "\"timestamp\": "
                + LocalDateTime.now());
    }

   @PutMapping("/files/pdf_statement")
    public ResponseEntity<String> sendPDFToEmail(@Valid @RequestBody TransactionByFiltersRequestDTO requestDTO){
        fileService.sendPDFToEmail(requestDTO);
       return ResponseEntity.ok().body("message: Email was sent.\n" + "timestamp: "
               + LocalDateTime.now());
    }


}
