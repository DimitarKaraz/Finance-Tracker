package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.RecurrentTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RecurrentTransactionRepository extends JpaRepository<RecurrentTransaction, Integer> {

    //List<RecurrentTransaction> findAllByAccount_User_UserId(int userId);

    Page<RecurrentTransaction> findAllByAccount_User_UserId(int userId, Pageable pageable);

    List<RecurrentTransaction> findAllByCategoryCategoryId(int categoryId);

    @Query(value = "SELECT re.*\n" +
            "FROM recurrent_transactions AS re\n" +
            "LEFT JOIN intervals AS i ON re.interval_id = i.interval_id\n" +
            "WHERE re.end_date = CURDATE() OR re.start_date = CURDATE() OR DATE_ADD(re.start_date, INTERVAL i.days*re.interval_count DAY) = CURDATE();", nativeQuery = true)
    List<RecurrentTransaction> findAllThatExpireOrNeedPaymentToday();
}
