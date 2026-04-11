package com.alec.Bud_Cal.repository;

import com.alec.Bud_Cal.model.Expense;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ExpenseRepository extends MongoRepository<Expense, String> {

    List<Expense> findByUserId(String userId);

    @Query(value = "{ '_id': ?0, 'user_id': ?1 }", delete = true)
    long deleteOwnedExpenseById(String expenseId, String userId);
}
