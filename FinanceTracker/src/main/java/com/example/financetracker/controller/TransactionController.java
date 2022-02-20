package com.example.financetracker.controller;

import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.transactionDTOs.TransactionByDateAndFiltersRequestDTO;
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
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/{transaction_id}")
    public ResponseEntity<ResponseWrapper<TransactionResponseDTO>> getTransactionById(@PathVariable("transaction_id") int transactionId){
        //TODO: SECURITY
        return ResponseWrapper.wrap("Retrieved transaction.", transactionService.getTransactionsById(transactionId), HttpStatus.OK);
    }

    //todo pass user id into service method!!!
    @PutMapping("/byDatesAndFilters")
    public ResponseEntity<ResponseWrapper<List<TransactionResponseDTO>>> getTransactionsByDatesAndFilters(@Valid @RequestBody TransactionByDateAndFiltersRequestDTO requestDTO){
        //TODO: SECURITY
        return ResponseWrapper.wrap("Retrieved transactions for given dates.", transactionService.getTransactionsByDates(requestDTO), HttpStatus.OK);
    }


    @GetMapping("/user/{user_id}")
    public ResponseEntity<ResponseWrapper<List<TransactionResponseDTO>>> getAllTransactionsByUserId(@PathVariable("user_id") int userId){
        //TODO: SECURITY
        return ResponseWrapper.wrap("Retrieved transactions for user.", transactionService.getAllTransactionsByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/account/{account_id}")
    public ResponseEntity<ResponseWrapper<List<TransactionResponseDTO>>> getAllTransactionsByAccountId(@PathVariable("account_id") int accountId){
        //TODO: SECURITY
        return ResponseWrapper.wrap("Retrieved transactions for user.", transactionService.getAllTransactionsByAccountId(accountId), HttpStatus.OK);
    }

    @GetMapping("/budgets/{budget_id}")
    public ResponseEntity<ResponseWrapper<List<TransactionResponseDTO>>> getAllTransactionsByBudgetId(@PathVariable("budget_id") int budgetId) {
        //TODO: SECURITY
        return ResponseWrapper.wrap("Transactions for budget " + budgetId + " retrieved.", transactionService.getAllTransactionsByBudgetId(budgetId), HttpStatus.OK);
    }

    @PostMapping("/create_transaction")
    public ResponseEntity<ResponseWrapper<TransactionResponseDTO>> createTransaction(@Valid @RequestBody TransactionCreateRequestDTO requestDTO){
        //TODO: SECURITY
        return ResponseWrapper.wrap("Transaction created.", transactionService.createTransaction(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/edit_transaction")
    public ResponseEntity<ResponseWrapper<TransactionResponseDTO>> editTransaction(@Valid @RequestBody TransactionEditRequestDTO requestDTO) {
        //TODO: SECURITY -> only for users with the same id
        return ResponseWrapper.wrap("Transaction edited.", transactionService.editTransaction(requestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{trans_id}/delete_transaction")
    public ResponseEntity<String> deleteTransaction(@PathVariable("trans_id") int transactionId) {
        //TODO: SECURITY -> only for user with same id
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.ok().body("Transaction deleted successfully.");
    }



}
