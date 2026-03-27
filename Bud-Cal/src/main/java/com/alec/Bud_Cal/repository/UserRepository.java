package com.alec.Bud_Cal.repository;

import com.alec.Bud_Cal.model.User;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);
}
