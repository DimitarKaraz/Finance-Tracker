package com.example.financetracker.service;

import com.example.financetracker.exceptions.FileTransferException;
import com.example.financetracker.exceptions.ForbiddenException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.exceptions.UnauthorizedException;
import com.example.financetracker.model.dto.transactionDTOs.TransactionByDateAndFiltersRequestDTO;
import com.example.financetracker.model.pojo.Transaction;
import com.example.financetracker.model.pojo.User;
import com.example.financetracker.model.repositories.UserRepository;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

@Service
public class FileService {

    public static final String PROFILE_IMAGES_PATH = "profileImages";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionService transactionService;

    public void downloadProfileImage(String filename, HttpServletResponse response){
        User user = userRepository.findById(MyUserDetailsService.getCurrentUserId())
                .orElseThrow(() -> {throw new UnauthorizedException("Invalid user id.");});
        if (!user.getProfileImageUrl().equals(filename)) {
            throw new ForbiddenException("You do not have access to this image.");
        }
        try {
            File file = new File(PROFILE_IMAGES_PATH + File.separator + filename);
            if(!file.exists()){
                throw new NotFoundException("File does not exist");
            }
            Files.copy(file.toPath(), response.getOutputStream());
        } catch (IOException e) {
            throw new FileTransferException("Image download failed");
        }
    }

    public void downloadCategoryIcon(String filename, HttpServletResponse response){
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

    public void sendPDFToEmail(TransactionByDateAndFiltersRequestDTO requestDTO){
        List<Transaction> requestedTransactions = transactionService.getRequiredTransactions(requestDTO);
        String userEmail = requestedTransactions.get(0).getAccount().getUser().getEmail();
        PDDocument document = convertToPDF(requestedTransactions);
        File file = new File("Statement.pdf");
        try {
            document.save(file);
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", CronJobs.HOST);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(CronJobs.SENDER_MAIL, CronJobs.PASSWORD);
            }
        });

        session.setDebug(true);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(CronJobs.SENDER_MAIL));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));
            message.setSubject("Transaction Statement for the period between "+requestDTO.getStartDate()+" and "+requestDTO.getEndDate()+".");
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(file);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName("StatementPDF");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
    //todo remove sneaky throws, add try/catch
    @SneakyThrows
    public PDDocument convertToPDF(List<Transaction> transactions){
        LinkedList<Transaction> transactionsList = new LinkedList<>(transactions);
        PDDocument document = new PDDocument();
        for (int i = 0; i < transactions.size() / 10; i++) {
            document.addPage(new PDPage());
        }
        for (int i = 0; i < document.getNumberOfPages(); i++) {
            PDPage page = document.getPage(i);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
            contentStream.newLineAtOffset(25, 500);
            String text = "";
            for (int j = 0; j < 10; j++) {
                if (transactionsList.isEmpty()) {
                    break;
                }
                Transaction transaction = transactionsList.removeFirst();
                text = "\n " + transaction.getDateTime()
                        + "Amount: "+transaction.getAmount()
                        + "Category: "+transaction.getCategory().getName()
                        + "Paid with:"+transaction.getPaymentMethod().getName()
                        + " " + transaction.getTransactionType().getName().toUpperCase();
            }
            contentStream.showText(text);
            contentStream.endText();
            contentStream.close();
        }
        return document;
    }

}
