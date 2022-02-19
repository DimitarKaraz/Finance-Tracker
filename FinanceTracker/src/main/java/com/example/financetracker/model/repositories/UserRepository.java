package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    boolean existsByEmail(String email);

    User findByUserId(int id);

    List<User> findAll();

    Optional<User> findByEmailOptional(String email);



}
