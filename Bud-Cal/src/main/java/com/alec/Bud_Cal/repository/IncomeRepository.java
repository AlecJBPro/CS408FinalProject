package com.alec.Bud_Cal.repository;

import com.alec.Bud_Cal.model.Income;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface IncomeRepository extends MongoRepository<Income, String> {

    List<Income> findByUserId(String userId);

    @Query(value = "{ '_id': ?0, 'user_id': ?1 }", delete = true)
    long deleteOwnedIncomeById(String incomeId, String userId);
}
