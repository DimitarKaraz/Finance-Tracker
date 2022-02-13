package com.example.financetracker.controller;

import com.example.financetracker.model.dto.accountDTOs.AccountResponseDTO;
import com.example.financetracker.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.financetracker.model.dto.accountDTOs.AccountCreateRequestDTO;


@RestController
@RequestMapping("/users/{user_id}")
public class AccountController extends AbstractController{

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
    public AccountResponseDTO editAccount(@RequestBody AccountCreateRequestDTO requestDTO, @PathVariable("user_id") int userId, @PathVariable("account_id") int accountId){
        //todo maybe create new DTO or rename AccountCreateRequestDTO
        //TODO: check if request is valid with Interceptor (valid session, valid id input)
        return accountService.editAccount(requestDTO, userId, accountId);
    }

}
