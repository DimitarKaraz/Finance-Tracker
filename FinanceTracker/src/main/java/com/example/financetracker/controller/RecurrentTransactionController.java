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
    
    @GetMapping("/recurrent_transactions/{rt_id}")
    public ResponseEntity<ResponseWrapper<RecurrentTransactionResponseDTO>> getById(@PathVariable("rt_id") int transactionId){
        return ResponseWrapper.wrap("Retrieved recurrent transaction.", recurrentTransactionService.getById(transactionId), HttpStatus.OK);
    }

    @GetMapping("/recurrent_transactions/{user_id}")
    public ResponseEntity<ResponseWrapper<List<RecurrentTransactionResponseDTO>>> getAllByUserId(@PathVariable("user_id") int userId){
        return ResponseWrapper.wrap("Retrieved recurrent transactions for user.",recurrentTransactionService.getAllByUserId(userId), HttpStatus.OK);
    }

    @PostMapping("/recurrent_transactions/create")
    public ResponseEntity<ResponseWrapper<RecurrentTransactionResponseDTO>> createRecurrentTransaction(@Valid @RequestBody RecurrentTransactionCreateRequestDTO requestDTO){
        return ResponseWrapper.wrap("Recurrent transaction created.", recurrentTransactionService.createRecurrentTransaction(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/recurrent_transactions/edit")
    public ResponseEntity<ResponseWrapper<RecurrentTransactionResponseDTO>> editRecurrentTransaction(@Valid @RequestBody RecurrentTransactionEditRequestDTO requestDTO){
        return ResponseWrapper.wrap("Recurrent transaction successfully edited.",
                recurrentTransactionService.editRecurrentTransaction(requestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/recurrent_transactions/{recurrent_transaction_id}/delete")
    public ResponseEntity<String> deleteTransaction(@PathVariable("recurrent_transaction_id") int id) {
        //TODO: SECURITY -> only for user with same id
        recurrentTransactionService.deleteRecurrentTransaction(id);
        return ResponseEntity.ok().body("Recurrent transaction deleted successfully.");
    }


}
