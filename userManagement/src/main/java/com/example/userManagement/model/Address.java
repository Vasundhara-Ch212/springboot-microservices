package com.example.userManagement.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

//Model to store the addresses of the users
@Getter
@Setter
@Document(collection = "addresses")
public class Address {

    @Transient
    //I don't want this field to persist to mongodb
    public static final String SEQUENCE_NAME = "address_sequence";

    @Id
    private Integer id;

    private String street;

    private String city;

    private String district;

    private String state;

    private Integer pincode;

}
