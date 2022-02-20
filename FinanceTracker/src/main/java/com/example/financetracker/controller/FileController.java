package com.example.financetracker.controller;


import com.example.financetracker.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.example.financetracker.model.dto.transactionDTOs.TransactionByDateAndFiltersRequestDTO;
import com.example.financetracker.service.TransactionService;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@RestController
@PreAuthorize("hasRole('ROLE_USER')")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/files/profile_images/{filename}/download")
    public void downloadProfileImage(@PathVariable String filename, HttpServletResponse response){
        fileService.downloadProfileImage(filename, response);
    }


    @GetMapping("/files/category_icons/{filename}/download")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public void downloadCategoryIcon(@PathVariable String filename, HttpServletResponse response){
        fileService.downloadCategoryIcon(filename, response);
    }

    //todo fix, doesn't work
   @GetMapping("/PdfStatement")
    public void sendPDFToEmail(@Valid @RequestBody TransactionByDateAndFiltersRequestDTO requestDTO){
        fileService.sendPDFToEmail(requestDTO);
    }


}
