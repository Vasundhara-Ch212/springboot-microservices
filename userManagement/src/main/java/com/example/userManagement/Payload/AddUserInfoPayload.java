package com.example.userManagement.Payload;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


//Payload to send the data to identitymanagement service,
//to add the User Info in UserInfoDb related to identitymanagement service
@Getter
@Setter
@Builder
public class AddUserInfoPayload {
    private String email;
    private String phoneNo;
    private String password;
    private Integer userId;
    private Set<String> roles;
}
