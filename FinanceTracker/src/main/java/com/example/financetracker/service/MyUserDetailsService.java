package com.example.financetracker.service;

import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.model.dto.userDTOs.MyUserDetails;
import com.example.financetracker.model.pojo.User;
import com.example.financetracker.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service("myUserDetailsService")
public class MyUserDetailsService implements UserDetailsService{

    @Resource
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null){
            throw new NotFoundException("User not found");
        }

        List<GrantedAuthority> authorities = Arrays.stream(user.getAuthorities().split(",")).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
 /*       return MyUserDetails.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();*/
        return new MyUserDetails(user.getEmail(), user.getPassword(), authorities);
    }



}
