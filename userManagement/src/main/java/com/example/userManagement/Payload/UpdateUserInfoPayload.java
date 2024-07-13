package com.example.userManagement.Payload;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

//payload send the data to identitymanagement service,
//to update the User Info in UserInfoDb related to identitymanagement service
@Getter
@Setter
@Builder
public class UpdateUserInfoPayload {
    private String phoneNo;
    private String password;
    private Set<String> roles;
}
