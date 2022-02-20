package com.example.financetracker.controller;

import com.example.financetracker.exceptions.FileTransferException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.model.dto.transactionDTOs.TransactionByDateAndFiltersRequestDTO;
import com.example.financetracker.model.pojo.Transaction;
import com.example.financetracker.service.TransactionService;
import com.example.financetracker.service.UtilityService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {

    public static final String PROFILE_IMAGES_PATH = "profileImages";

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UtilityService utilityService;

    @GetMapping("/profile_images/{filename}")
    public void downloadProfileImage(@PathVariable String filename, HttpServletResponse response){
        File file = new File(PROFILE_IMAGES_PATH + File.separator + filename);
        if(!file.exists()){
            throw new NotFoundException("File does not exist");
        }
        try {
            Files.copy(file.toPath(), response.getOutputStream());
        } catch (IOException e) {
            throw new FileTransferException("Image download failed");
        }
    }

    @GetMapping("/category_icons/{filename}")
    public void downloadCategoryIcon(@PathVariable String filename, HttpServletResponse response){
        File file;
        try {
            file = new ClassPathResource("static"+File.separator+"category_icons"+File.separator+filename).getFile();
        } catch (IOException e) {
            throw new FileTransferException("A problem occurred while retrieving file.");
        }

        if(!file.exists()){
            throw new NotFoundException("File does not exist");
        }
        try {
            Files.copy(file.toPath(), response.getOutputStream());
        } catch (IOException e) {
            throw new FileTransferException("Image download failed");
        }
    }

    //todo fix, doesn't work
/*    @GetMapping("/PdfStatement")
    public void downloadPDFStatement(@Valid @RequestBody TransactionByDateAndFiltersRequestDTO requestDTO, HttpServletResponse response){
        List<Transaction> requestedTransactions = transactionService.getTransactionsByDates(requestDTO);
        PDDocument document = utilityService.convertToPDF(requestedTransactions);
        try {
            document.save(response.getOutputStream());
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("fak");
        }
    }*/


}
