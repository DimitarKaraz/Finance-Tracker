package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    User findByResetPasswordToken(String resetPasswordToken);

    boolean existsByEmail(String email);

    User findByUserId(int id);

    List<User> findAll();

    //todo verify if working properly
    @Modifying
    @Query(value = "SELECT * FROM users\n" +
            "WHERE last_login <= DATE_ADD(CURDATE(), INTERVAL -1 MONTH);", nativeQuery = true)
    List<User> findAllInactiveUsers();



}
