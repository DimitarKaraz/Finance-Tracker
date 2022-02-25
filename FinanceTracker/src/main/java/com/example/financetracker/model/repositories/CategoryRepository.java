package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByName(String name);

    List<Category> findAllByUser_UserIdOrUser_UserIdIsNull(int userId);

    Page<Category> findAllByUser_UserIdOrUser_UserIdIsNull(int userId, Pageable pageable);

    Set<Category> findCategoriesByCategoryIdIn(Set<Integer> categoryIds);

    @Query(value = "SELECT c.category_id, c.name, c.category_icon_id, c.transaction_type_id, c.user_id\n" +
            "FROM categories AS c\n" +
            "WHERE (c.user_id IS NULL OR c.user_id = ?) AND c.transaction_type_id = ?;", nativeQuery = true)
    List<Category> findCategoriesByUserIdAndTransactionTypeId(int userId, int transactionTypeId);



}
