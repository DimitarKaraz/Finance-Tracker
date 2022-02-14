package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.CategoryIcon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryIconRepository extends JpaRepository<CategoryIcon, Integer> {

}
