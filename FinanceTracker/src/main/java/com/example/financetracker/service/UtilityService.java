package com.example.financetracker.service;

import com.example.financetracker.model.pojo.*;
import com.example.financetracker.model.repositories.*;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@PreAuthorize("hasRole('ROLE_USER')")
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

