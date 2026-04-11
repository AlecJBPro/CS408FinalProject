package com.alec.Bud_Cal.service;

import com.alec.Bud_Cal.model.Income;
import com.alec.Bud_Cal.repository.IncomeRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;

    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    public List<Income> getIncomesByUserId(String userId) {
        return incomeRepository.findByUserId(userId);
    }

    public Optional<Income> getIncomeById(String incomeId) {
        return incomeRepository.findById(incomeId);
    }

    public Income saveIncome(Income income) {
        if (income.getCreatedAt() == null) {
            income.setCreatedAt(Instant.now());
        }
        income.setUpdatedAt(Instant.now());
        return incomeRepository.save(income);
    }

    public void deleteIncome(String incomeId) {
        incomeRepository.deleteById(incomeId);
    }

    public void deleteIncome(Income income) {
        incomeRepository.delete(income);
    }

    public boolean deleteIncomeByIdAndUserId(String incomeId, String userId) {
        return incomeRepository.deleteOwnedIncomeById(incomeId, userId) > 0;
    }

    public BigDecimal getTotalIncome(String userId) {
        return getIncomesByUserId(userId).stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
