package com.example.financetracker.controller;

import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.transactionDTOs.TransactionCreateRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionEditRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionResponseDTO;
import com.example.financetracker.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transactions/create")
    public ResponseEntity<ResponseWrapper<TransactionResponseDTO>> createTransaction(@Valid @RequestBody TransactionCreateRequestDTO requestDTO){
        return ResponseWrapper.wrap("Transaction created.",
                transactionService.createTransaction(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/transactions")
    public ResponseEntity<ResponseWrapper<LinkedHashMap<String, Object>>> getAllTransactionsOfCurrentUser
            (@RequestParam(name = "page", defaultValue = "0") int pageNumber){
        return ResponseWrapper.wrap("Retrieved transactions for user.",
                transactionService.getAllTransactionsByCurrentUser(pageNumber), HttpStatus.OK);
    }
    
    @GetMapping("/transactions/{transaction_id}")
    public ResponseEntity<ResponseWrapper<TransactionResponseDTO>> getTransactionById(@PathVariable("transaction_id") int transactionId){
        return ResponseWrapper.wrap("Retrieved transaction.",
                transactionService.getTransactionsById(transactionId), HttpStatus.OK);
    }

    @GetMapping("/transactions/for_account/{account_id}")
    public ResponseEntity<ResponseWrapper<LinkedHashMap<String, Object>>> getAllTransactionsByAccountId
            (@PathVariable("account_id") int accountId, @RequestParam(name = "page", defaultValue = "0") int pageNumber){
        return ResponseWrapper.wrap("Retrieved transactions for account.",
                transactionService.getAllTransactionsByAccountId(accountId, pageNumber), HttpStatus.OK);
    }

    @PutMapping("/transactions/edit")
    public ResponseEntity<ResponseWrapper<TransactionResponseDTO>> editTransaction(@Valid @RequestBody TransactionEditRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Transaction edited.",
                transactionService.editTransaction(requestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/transactions/{trans_id}/delete")
    public ResponseEntity<String> deleteTransaction(@PathVariable("trans_id") int transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.ok().body("Transaction deleted successfully.");
    }



}
