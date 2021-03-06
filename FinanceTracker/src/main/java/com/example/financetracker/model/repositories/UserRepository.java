package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.User;
import org.jetbrains.annotations.NotNull;
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

    @NotNull List<User> findAll();

    @Query(value = "SELECT *\n" +
            "FROM users\n" +
            "WHERE last_login <= DATE_ADD(CURDATE(), INTERVAL -1 MONTH)\n" +
            "AND last_email_sent_on <= DATE_ADD(CURDATE(), INTERVAL -1 MONTH);", nativeQuery = true)
    List<User> findAllInactiveUsers();

    User findByVerificationToken(String token);

}
