package com.example.financetracker.controller;

import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionEditRequestDTO;
import com.example.financetracker.model.dto.recurrentTransactionDTOs.RecurrentTransactionResponseDTO;
import com.example.financetracker.service.RecurrentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class RecurrentTransactionController {

    @Autowired
    private RecurrentTransactionService recurrentTransactionService;

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
