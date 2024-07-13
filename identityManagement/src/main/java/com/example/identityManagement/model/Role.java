package com.example.identityManagement.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.lang.annotation.Documented;
import java.util.HashSet;
import java.util.Set;

//Model to store different types of user roles
@Getter
@Setter
@Document(collection = "roles")
public class Role {

    @Id
    private Integer id;
    private String role;

}

