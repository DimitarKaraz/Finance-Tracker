package com.example.financetracker.service;

import com.example.financetracker.exceptions.*;
import com.example.financetracker.model.dao.StatisticsDAO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionByFiltersRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionResponseDTO;
import com.example.financetracker.model.dto.userDTOs.MyUserDetails;
import com.example.financetracker.model.pojo.User;
import com.example.financetracker.model.repositories.UserRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private StatisticsDAO statisticsDAO;

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

    //todo fix
    public void sendPDFToEmail(TransactionByFiltersRequestDTO requestDTO){
        List<TransactionResponseDTO> requestedTransactions = statisticsDAO.getTransactionsByFilters(requestDTO);
        if (requestedTransactions.isEmpty()){
            throw new BadRequestException("You have no transactions to show!");
        }
        MyUserDetails details = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail = details.getEmail();
        File file = new File(convertToPDF(requestedTransactions));

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
            messageBodyPart.setFileName("Statement.pdf");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            Transport.send(message);
        } catch (MessagingException mex) {
            throw new FileTransferException("Sending email failed.");
        }
        file.delete();
    }

    public String convertToPDF(List<TransactionResponseDTO> transactions){
        String fileName = "Statement-"+System.nanoTime()+".pdf";
        try(PDDocument document = new PDDocument()) {
            LinkedList<TransactionResponseDTO> transactionsList = new LinkedList<>(transactions);
            for (int i = 0; i < (transactions.size() / 30) +1; i++) {
                document.addPage(new PDPage());
            }
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                PDPage page = document.getPage(i);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.setLeading(20.0f);
                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                contentStream.newLineAtOffset(25, page.getTrimBox().getHeight()-25);
                String text;
                for (int j = 0; j < 30; j++) {
                    contentStream.newLine();
                    if (transactionsList.isEmpty()) {
                        break;
                    }
                    TransactionResponseDTO transaction = transactionsList.removeFirst();
                    text =  "Date: "+transaction.getDateTime().toLocalDate()+", "
                            +"Time: "+transaction.getDateTime().toLocalTime()+", "
                            + "Amount: "+transaction.getAmount()+" "+transaction.getCurrency().getAbbreviation().toUpperCase()+", "
                            + "Category: "+transaction.getCategoryResponseDTO().getName()+", "
                            + "Paid with: "+transaction.getPaymentMethod().getName()+", "
                            + "Type: " + transaction.getTransactionType().getName().toUpperCase();
                    contentStream.showText(text);
                }
                contentStream.endText();
                contentStream.close();
                document.save(fileName);
            }
        } catch (IOException e) {
            throw new FileTransferException("Error occurred when generating pdf statement.");
        }

        return fileName;
    }

}
