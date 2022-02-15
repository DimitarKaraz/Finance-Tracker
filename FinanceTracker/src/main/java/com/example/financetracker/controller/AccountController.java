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
import java.util.List;


@RestController
@RequestMapping("/users")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/create_account")
    public ResponseEntity<ResponseWrapper<AccountResponseDTO>> createAccount(@Valid @RequestBody AccountCreateRequestDTO requestDTO){
        //TODO: SECURITY -> only for user with same id
        return ResponseWrapper.wrap("Account created.", accountService.createAccount(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{user_id}/accounts")
    public ResponseEntity<ResponseWrapper<List<AccountResponseDTO>>> getAccountsByUserId(@PathVariable("user_id") int id) {
        //TODO: SECURITY -> only for user with same id
        return ResponseWrapper.wrap("User " + id + " accounts retrieved.",
                accountService.getAllAccountsByUserId(id), HttpStatus.OK);
    }

    @GetMapping("/{user_id}/accounts/{account_id}")
    public ResponseEntity<ResponseWrapper<AccountResponseDTO>> getAccountById(@PathVariable("user_id") int userId,
                                                                              @PathVariable("account_id") int accountId) {
        //TODO: SECURITY -> only for user with same id
        return ResponseWrapper.wrap("Account " + accountId + " retrieved.",
                accountService.getAccountById(userId, accountId), HttpStatus.OK);
    }

    @PutMapping("/accounts/edit_account")
    public ResponseEntity<ResponseWrapper<AccountResponseDTO>> editAccount(@Valid @RequestBody AccountEditRequestDTO requestDTO){
        //TODO: check if request is valid with Interceptor (valid session, valid id input)
        return ResponseWrapper.wrap("Account edited.", accountService.editAccount(requestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/accounts/{acc_id}/delete_account")
    public ResponseEntity<String> deleteAccount(@PathVariable("acc_id") int id) {
        //TODO: SECURITY -> only for user with same id
        accountService.deleteAccount(id);
        return ResponseEntity.ok().body("Account deleted successfully.");
    }

}
