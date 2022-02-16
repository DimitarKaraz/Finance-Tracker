package com.example.financetracker.controller;

import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.transactionDTOs.TransactionEditRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionCreateRequestDTO;
import com.example.financetracker.model.dto.transactionDTOs.TransactionResponseDTO;
import com.example.financetracker.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transactions/create")
    private ResponseEntity<ResponseWrapper<TransactionResponseDTO>> createTransaction(@Valid @RequestBody TransactionCreateRequestDTO requestDTO){
        return ResponseWrapper.wrap("Transaction created.", transactionService.createTransaction(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/transactions/edit_transaction")
    public ResponseEntity<ResponseWrapper<TransactionResponseDTO>> editTransaction
                                            (@Valid @RequestBody TransactionEditRequestDTO requestDTO) {
        //TODO: SECURITY -> only for users with the same id
        return ResponseWrapper.wrap("Transaction edited.", transactionService.editTransaction(requestDTO), HttpStatus.OK);
    }



}
