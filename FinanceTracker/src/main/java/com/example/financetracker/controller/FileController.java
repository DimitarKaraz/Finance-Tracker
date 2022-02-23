package com.example.financetracker.controller;


import com.example.financetracker.model.dto.transactionDTOs.TransactionByFiltersRequestDTO;
import com.example.financetracker.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@RestController
@PreAuthorize("hasRole('ROLE_USER')")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/files/profile_images/{filename}/download")
    public void downloadProfileImage(@PathVariable("filename") String filename, HttpServletResponse response){
        fileService.downloadProfileImage(filename, response);
    }


    @GetMapping("/files/category_icons/{filename}/download")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public void downloadCategoryIcon(@PathVariable("filename") String filename, HttpServletResponse response){
        fileService.downloadCategoryIcon(filename, response);
    }

   @PutMapping("/files/pdf_statement")
    public void sendPDFToEmail(@Valid @RequestBody TransactionByFiltersRequestDTO requestDTO){
        fileService.sendPDFToEmail(requestDTO);
    }


}
