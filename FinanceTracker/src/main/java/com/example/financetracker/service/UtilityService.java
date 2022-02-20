package com.example.financetracker.service;

import com.example.financetracker.model.pojo.*;
import com.example.financetracker.model.repositories.*;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Service
public class UtilityService {
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private AccountTypeRepository accountTypeRepository;
    @Autowired
    private TransactionTypeRepository transactionTypeRepository;
    @Autowired
    private CategoryIconRepository categoryIconRepository;
    @Autowired
    private IntervalRepository intervalRepository;

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    public List<AccountType> getAllAccountTypes() {
        return accountTypeRepository.findAll();
    }

    public List<TransactionType> getAllTransactionTypes(){
        return transactionTypeRepository.findAll();
    }

    public List<CategoryIcon> getAllCategoryIcons(){
        return categoryIconRepository.findAll();
    }

    public List<Interval> getAllIntervals(){
        return intervalRepository.findAll();
    }

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
                int counter = 10;
                while (!transactionsList.isEmpty() && counter > 0){
                    Transaction transaction = transactionsList.removeFirst();
                    text = "\n " + transaction.getDateTime()
                            + "Amount: "+transaction.getAmount()
                            + "Category: "+transaction.getCategory().getName()
                            + "Paid with:"+transaction.getPaymentMethod().getName()
                            + " " + transaction.getTransactionType().getName().toUpperCase();
                    counter--;
                }
                contentStream.showText(text);
                contentStream.endText();
                contentStream.close();
            }
            return document;
    }
}

