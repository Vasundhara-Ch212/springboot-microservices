package com.example.identityManagement.repository;

import com.example.identityManagement.model.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends MongoRepository<UserInfo, Integer> {
//    @Query("{ 'PhoneNo' : ?username,'email' : ?username }")
//    Optional<UserInfo> findByEmailOrPhoneNo(String username);

    Optional<UserInfo> findByEmail(String username);

    Optional<UserInfo> findByPhoneNo(String username);
}
