package com.alec.Bud_Cal.repository;

import com.alec.Bud_Cal.model.Expense;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExpenseRepository extends MongoRepository<Expense, String> {

    List<Expense> findByUserId(String userId);
}