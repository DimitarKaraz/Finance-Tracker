package com.example.financetracker.controller;


import com.example.financetracker.model.pojo.Account;
import com.example.financetracker.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/{id}/accounts")
public class AccountController extends AbstractController{

    @Autowired
    private AccountService accountService;
/*
    @PostMapping()
    public Account createAccount(){

    }
    */
}
