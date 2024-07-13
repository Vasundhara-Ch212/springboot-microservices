package com.example.userManagement.Payload;

import lombok.*;

import java.util.Set;


//Payload to receive the data from the user to add/update the user
@Getter
@Setter
public class RegisterUserPayload {

    private String name;

    private String email;

    private String phoneNo;

    private String password;

    private AddressPayload address;

    private Set<String> roles;

}
