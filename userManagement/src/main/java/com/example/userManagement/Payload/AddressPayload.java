package com.example.userManagement.Payload;

import lombok.Getter;
import lombok.Setter;

//Payload to receive the data from the user to add/update the User's address
@Getter
@Setter
public class AddressPayload {

    private String street;

    private String city;

    private String district;

    private String state;

    private Integer pincode;

}
