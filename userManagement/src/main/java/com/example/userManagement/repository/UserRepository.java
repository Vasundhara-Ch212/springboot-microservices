package com.example.userManagement.repository;

import com.example.userManagement.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNo(String phoneNo);

}
