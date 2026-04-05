package com.alec.Bud_Cal.repository;

import com.alec.Bud_Cal.model.Income;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IncomeRepository extends MongoRepository<Income, String> {

    List<Income> findByUserId(String userId);
}