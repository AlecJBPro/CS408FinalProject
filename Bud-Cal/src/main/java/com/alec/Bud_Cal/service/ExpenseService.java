package com.alec.Bud_Cal.service;

import com.alec.Bud_Cal.model.Expense;
import com.alec.Bud_Cal.repository.ExpenseRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> getExpensesByUserId(String userId) {
        return expenseRepository.findByUserId(userId);
    }

    public Optional<Expense> getExpenseById(String expenseId) {
        return expenseRepository.findById(expenseId);
    }

    public Expense saveExpense(Expense expense) {
        if (expense.getCreatedAt() == null) {
            expense.setCreatedAt(Instant.now());
        }
        expense.setUpdatedAt(Instant.now());
        return expenseRepository.save(expense);
    }

    public void deleteExpense(String expenseId) {
        expenseRepository.deleteById(expenseId);
    }

    public void deleteExpense(Expense expense) {
        expenseRepository.delete(expense);
    }

    public boolean deleteExpenseByIdAndUserId(String expenseId, String userId) {
        return expenseRepository.deleteOwnedExpenseById(expenseId, userId) > 0;
    }

    public BigDecimal getTotalExpenses(String userId) {
        return getExpensesByUserId(userId).stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
