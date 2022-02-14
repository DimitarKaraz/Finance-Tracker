package com.example.financetracker.controller;

import com.example.financetracker.model.dto.accountDTOs.AccountEditRequestDTO;
import com.example.financetracker.model.dto.accountDTOs.AccountResponseDTO;
import com.example.financetracker.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.financetracker.model.dto.accountDTOs.AccountCreateRequestDTO;

import javax.validation.Valid;


@RestController
@RequestMapping("/users/{user_id}")
public class AccountController {
    //TODO: make url above "/users/{user_id}/accounts" and fix the ones below
    //TODO: move {account_id} to request body?

    @Autowired
    private AccountService accountService;

    @PostMapping("/create_account")
    public AccountResponseDTO createAccount(@RequestBody AccountCreateRequestDTO requestDTO, @PathVariable("user_id") int userId){
        return accountService.createAccount(requestDTO, userId);
    }

    @GetMapping("/accounts")
    public List<AccountResponseDTO> getAccountsByUserId(@PathVariable("user_id") int id) {
        return accountService.getAllAccountsByUserId(id);
    }

    @GetMapping("/accounts/{account_id}")
    public AccountResponseDTO getAccountById(@PathVariable("user_id") int userId, @PathVariable("account_id") int accountId) {
        return accountService.getAccountById(userId, accountId);
    }

    @PutMapping("/accounts/{account_id}/edit_account")
    public AccountResponseDTO editAccount(@Valid @RequestBody AccountEditRequestDTO requestDTO){
        //TODO: check if request is valid with Interceptor (valid session, valid id input)
        return accountService.editAccount(requestDTO);
    }

    @DeleteMapping("/accounts/delete")
    public ResponseEntity<String> deleteAccount(@RequestParam("account_id") int accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.ok().body("Account deleted successfully.");
    }

}
