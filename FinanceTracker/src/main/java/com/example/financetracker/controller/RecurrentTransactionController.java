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
@RequestMapping("/recurrent_transactions")
public class RecurrentTransactionController {

    @Autowired
    private RecurrentTransactionService recurrentTransactionService;
    
    @GetMapping("/{rec_trans_id}")
    public ResponseEntity<ResponseWrapper<RecurrentTransactionResponseDTO>> getRecurrentTransactionById(@PathVariable("rec_trans_id") int recurrentTransactionId){
        //TODO: SECURITY -> only for user with same id
        return ResponseWrapper.wrap("Retrieved recurrent transaction.",
                recurrentTransactionService.getRecurrentTransactionById(recurrentTransactionId), HttpStatus.OK);
    }

    @GetMapping("users/{user_id}")
    public ResponseEntity<ResponseWrapper<List<RecurrentTransactionResponseDTO>>> getAllRecurrentTransactionsByUserId(@PathVariable("user_id") int userId){
        //TODO: SECURITY -> only for user with same id
        return ResponseWrapper.wrap("Retrieved recurrent transactions for user.",
                recurrentTransactionService.getAllRecurrentTransactionsByUserId(userId), HttpStatus.OK);
    }

    @PostMapping("/create_recurrent_transaction")
    public ResponseEntity<ResponseWrapper<RecurrentTransactionResponseDTO>> createRecurrentTransaction(@Valid @RequestBody RecurrentTransactionCreateRequestDTO requestDTO){
        //TODO: SECURITY -> only for user with same id
        return ResponseWrapper.wrap("Recurrent transaction created.",
                recurrentTransactionService.createRecurrentTransaction(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/edit_recurrent_transaction")
    public ResponseEntity<ResponseWrapper<RecurrentTransactionResponseDTO>> editRecurrentTransaction(@Valid @RequestBody RecurrentTransactionEditRequestDTO requestDTO){
        //TODO: SECURITY -> only for user with same id
        return ResponseWrapper.wrap("Recurrent transaction successfully edited.",
                recurrentTransactionService.editRecurrentTransaction(requestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{recurrent_transaction_id}/delete_recurrent_transaction")
    public ResponseEntity<String> deleteRecurrentTransactionById(@PathVariable("recurrent_transaction_id") int id) {
        //TODO: SECURITY -> only for user with same id
        recurrentTransactionService.deleteRecurrentTransaction(id);
        return ResponseEntity.ok().body("Recurrent transaction deleted successfully.");
    }


}
