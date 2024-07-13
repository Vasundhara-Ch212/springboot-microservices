package com.example.identityManagement.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

//Payload to receive the data from usermanagement service to update the user info
//to UserInfo table of UserInfoDb,everytime the user is updated in usermanagement service.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserInfoPayload {

    private String phoneNo;
    private String password;
    private Set<String> roles;

}
