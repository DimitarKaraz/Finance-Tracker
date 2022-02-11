package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {


    User findByEmail(String email);
}
