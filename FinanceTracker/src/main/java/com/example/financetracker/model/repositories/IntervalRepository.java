package com.example.financetracker.model.repositories;

import com.example.financetracker.model.pojo.Interval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntervalRepository extends JpaRepository<Interval, Integer> {


}
