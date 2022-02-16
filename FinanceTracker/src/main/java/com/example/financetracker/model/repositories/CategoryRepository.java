package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {


    List<Category> findAllByUser_UserIdOrUser_UserIdIsNull(int userId);

    //Category findByUser_UserIdAndNameOrUser_UserIdIsNullAndName(int userId, String name);

    Category findByUser_UserIdAndName(int userId, String name);
    Category findByNameAndUser_UserIdIsNull(String name);

    Category findByCategoryId(int categoryId);


//    @Query(value = "SELECT u FROM User u")
    Set<Category> findCategoriesByCategoryIdIn(Set<Integer> categoryIds);

    @Modifying
    @Query(value = "SELECT c.category_id, c.name, c.category_icon_id, c.transaction_type_id, c.user_id\n" +
                    "FROM categories AS c\n" +
                    "JOIN budgets_have_categories AS bhc ON c.category_id = bhc.category_id\n" +
                    "WHERE bhc.budget_id = ?;", nativeQuery = true)
    Set<Category> findAllByBudgetId(int budgetId);


}
