package com.example.identityManagement.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.annotation.Documented;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//Model to store the user info which are used to authenticate the user
@Getter
@Setter
@Document(collection = "users_info")
public class UserInfo {

    @Id
    private Integer id;
    private String email;
    private String phoneNo;
    private String password;
    private Integer userId;
    private Set<Integer> roles;

}

