package com.example.financetracker.controller;

import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.accountDTOs.AccountCreateRequestDTO;
import com.example.financetracker.model.dto.accountDTOs.AccountEditRequestDTO;
import com.example.financetracker.model.dto.accountDTOs.AccountResponseDTO;
import com.example.financetracker.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;


@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/accounts/create")
    public ResponseEntity<ResponseWrapper<AccountResponseDTO>> createAccount(@Valid @RequestBody AccountCreateRequestDTO requestDTO){
        return ResponseWrapper.wrap("Account created.",
                accountService.createAccount(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/accounts")
    public ResponseEntity<ResponseWrapper<Map<String, Object>>> getAllAccountsOfCurrentUser(
            @RequestParam(name = "page", defaultValue = "0") int pageNo) {
        return ResponseWrapper.wrap("User accounts retrieved.",
                accountService.getAllAccountsOfCurrentUser(pageNo), HttpStatus.OK);
    }

    @GetMapping("/accounts/{account_id}")
    public ResponseEntity<ResponseWrapper<AccountResponseDTO>> getAccountById(@PathVariable("account_id") int accountId) {
        return ResponseWrapper.wrap("Account " + accountId + " retrieved.",
                accountService.getAccountById(accountId), HttpStatus.OK);
    }

    @PutMapping("/accounts/edit")
    public ResponseEntity<ResponseWrapper<AccountResponseDTO>> editAccount(@Valid @RequestBody AccountEditRequestDTO requestDTO){
        return ResponseWrapper.wrap("Account edited.",
                accountService.editAccount(requestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/accounts/{acc_id}/delete")
    public ResponseEntity<String> deleteAccountById(@PathVariable("acc_id") int id) {
        accountService.deleteAccountById(id);
        return ResponseEntity.ok().body("Account deleted successfully.");
    }

}
