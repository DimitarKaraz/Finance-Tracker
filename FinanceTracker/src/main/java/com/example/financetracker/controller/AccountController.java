package com.example.financetracker.controller;


import com.example.financetracker.model.dto.AccountCreateRequestDTO;
import com.example.financetracker.model.pojo.Account;
import com.example.financetracker.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{user_id}")
public class AccountController extends AbstractController{

    @Autowired
    private AccountService accountService;

    @PostMapping("/create_account")
    public Account createAccount(@RequestBody AccountCreateRequestDTO requestDTO, @PathVariable("user_id") int userId){
        return accountService.createAccount(requestDTO, userId);
    }

}
