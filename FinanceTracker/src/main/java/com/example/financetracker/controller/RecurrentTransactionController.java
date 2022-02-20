package com.example.financetracker.controller;

import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionCreateRequestDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionEditRequestDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionResponseDTO;
import com.example.financetracker.service.RecurrentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
public class RecurrentTransactionController {

    @Autowired
    private RecurrentTransactionService recurrentTransactionService;

    @PostMapping("/recurrent_transactions/create")
    public ResponseEntity<ResponseWrapper<RecurrentTransactionResponseDTO>> createRecurrentTransaction(@Valid @RequestBody RecurrentTransactionCreateRequestDTO requestDTO){
        return ResponseWrapper.wrap("Recurrent transaction created.",
                recurrentTransactionService.createRecurrentTransaction(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/recurrent_transactions")
    public ResponseEntity<ResponseWrapper<List<RecurrentTransactionResponseDTO>>> getAllRecurrentTransactionsForCurrentUser(){
        return ResponseWrapper.wrap("Retrieved recurrent transactions for user.",
                recurrentTransactionService.getAllRecurrentTransactionsForCurrentUser(), HttpStatus.OK);
    }

    @GetMapping("/recurrent_transactions/{rt_id}")
    public ResponseEntity<ResponseWrapper<RecurrentTransactionResponseDTO>> getRecurrentTransactionById(@PathVariable("rt_id") int recurrentTransactionId){
        return ResponseWrapper.wrap("Retrieved recurrent transaction.",
                recurrentTransactionService.getRecurrentTransactionById(recurrentTransactionId), HttpStatus.OK);
    }


    @PutMapping("/recurrent_transactions/edit")
    public ResponseEntity<ResponseWrapper<RecurrentTransactionResponseDTO>> editRecurrentTransaction(@Valid @RequestBody RecurrentTransactionEditRequestDTO requestDTO){
        return ResponseWrapper.wrap("Recurrent transaction successfully edited.",
                recurrentTransactionService.editRecurrentTransaction(requestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/recurrent_transactions/{rt_id}/delete")
    public ResponseEntity<String> deleteRecurrentTransactionById(@PathVariable("rt_id") int id) {
        recurrentTransactionService.deleteRecurrentTransaction(id);
        return ResponseEntity.ok().body("Recurrent transaction deleted successfully.");
    }


}
