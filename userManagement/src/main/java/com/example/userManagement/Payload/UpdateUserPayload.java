package com.example.userManagement.Payload;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;


//Payload to receive the data from the user to add/update the user
@Getter
@Setter
public class UpdateUserPayload {

    private String phoneNo;

    private String password;

    private AddressPayload address;

    private Set<String> roles;

}
