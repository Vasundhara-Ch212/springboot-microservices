package com.example.userManagement.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

//Model to store all the users
@Getter
@Setter
@Document(collection = "users")
public class User {

    @Transient
    //I don't want this field to persist to mongodb
    public static final String SEQUENCE_NAME = "user_sequence";

    @Id
    private Integer id;

    private String name;

    private Address address;

    private String email;

    private String phoneNo;

}