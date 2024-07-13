package com.example.identityManagement.repository;

import com.example.identityManagement.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<Role, Integer> {
    Optional<Role> findByRoleIgnoreCaseIn(String roleName);

}
