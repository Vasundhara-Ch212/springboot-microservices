package com.example.userManagement.Payload;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

//Payload to send the data to identitymanagement service,
//to delete the User Info in UserInfoDb.
@Getter
@Setter
@Builder
public class DeleteUserInfoPayload {
    private Integer userId;
}
