package com.example.identityManagement.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

//Payload to receive the data from usermanagement service to add the user info
//to UserInfo table of UserInfoDb,everytime a new user is registered in usermanagement service.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddUserInfoPayload {

    private Integer userId;
    private String email;
    private String phoneNo;
    private String password;
    private Set<String> roles;

}
