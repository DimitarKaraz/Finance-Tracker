package com.example.financetracker.controller;

import com.example.financetracker.exceptions.FileTransferException;
import com.example.financetracker.exceptions.NotFoundException;
import lombok.SneakyThrows;
import org.aspectj.apache.bcel.util.ClassPath;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/files")
public class FileController {


    public static final String PROFILE_IMAGES_PATH = "profileImages";

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
            throw new FileTransferException("A problem occurred when retrieving file.");
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

}
