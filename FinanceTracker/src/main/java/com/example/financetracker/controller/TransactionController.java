package com.example.financetracker.controller;

import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.transactionDTOs.TransactionCreateRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionEditRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionResponseDTO;
import com.example.financetracker.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transactions/create")
    public ResponseEntity<ResponseWrapper<TransactionResponseDTO>> createTransaction(@Valid @RequestBody TransactionCreateRequestDTO requestDTO){
        return ResponseWrapper.wrap("Transaction created.", transactionService.createTransaction(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("budgets/{bud_id}/transactions/")
    public ResponseEntity<ResponseWrapper<List<TransactionResponseDTO>>> getAllTransactionsByBudgetId(@PathVariable("bud_id") int id) {
        //TODO: SECURITY
        return ResponseWrapper.wrap("Transactions for budget " + id + " retrieved.", transactionService.getAllByBudgetId(id), HttpStatus.OK);
    }

    @PutMapping("/transactions/edit")
    public ResponseEntity<ResponseWrapper<TransactionResponseDTO>> editTransaction
                                            (@Valid @RequestBody TransactionEditRequestDTO requestDTO) {
        //TODO: SECURITY -> only for users with the same id
        return ResponseWrapper.wrap("Transaction edited.", transactionService.editTransaction(requestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/transactions/{tr_id}/delete")
    public ResponseEntity<String> deleteTransaction(@PathVariable("tr_id") int id) {
        //TODO: SECURITY -> only for user with same id
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok().body("Budget deleted successfully.");
    }

    @GetMapping("/transactions/{transaction_id}")
    public ResponseEntity<ResponseWrapper<TransactionResponseDTO>> getById(@PathVariable("transaction_id") int transactionId){
        return ResponseWrapper.wrap("Retrieved transaction.", transactionService.getById(transactionId), HttpStatus.OK);
    }

    @GetMapping("/transactions/{user_id}")
    public ResponseEntity<ResponseWrapper<List<TransactionResponseDTO>>> getAllForUser(@PathVariable("user_id") int userId){
        return ResponseWrapper.wrap("Retrieved transactions for user.", transactionService.getAllByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/transactions/{account_id}")
    public ResponseEntity<ResponseWrapper<List<TransactionResponseDTO>>> getAllForAccount(@PathVariable("account_id") int accountId){
        return ResponseWrapper.wrap("Retrieved transactions for user.", transactionService.getAllByAccountId(accountId), HttpStatus.OK);
    }

}
