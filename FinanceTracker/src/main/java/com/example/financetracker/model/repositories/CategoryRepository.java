package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {


    List<Category> findAllByUser_UserIdOrUser_UserIdIsNull(int userId);

    //Category findByUser_UserIdAndNameOrUser_UserIdIsNullAndName(int userId, String name);

    Category findByUser_UserIdAndName(int userId, String name);
    Category findByNameAndUser_UserIdIsNull(String name);
}
