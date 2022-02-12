package com.example.financetracker.controller;

import com.example.financetracker.model.dto.accountDTOs.AccountDTO;
import com.example.financetracker.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.financetracker.model.dto.accountDTOs.AccountCreateRequestDTO;
import com.example.financetracker.model.pojo.Account;


@RestController
@RequestMapping("/users/{user_id}")
public class AccountController extends AbstractController{

    @Autowired
    private AccountService accountService;

    @PostMapping("/create_account")
    public Account createAccount(@RequestBody AccountCreateRequestDTO requestDTO, @PathVariable("user_id") int userId){
        return accountService.createAccount(requestDTO, userId);
    }

    @GetMapping("/accounts")
    public List<AccountDTO> getAccountsByUserId(@PathVariable("user_id") int id) {
        return accountService.getAllAccountsByUserId(id);
    }

    @GetMapping("/accounts/{account_id}")
    public AccountDTO getAccountById(@PathVariable("user_id") int userId, @PathVariable("account_id") int accountId) {
        return accountService.getAccountById(userId, accountId);
    }

}
