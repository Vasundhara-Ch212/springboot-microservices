package com.example.identityManagement.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


//Payload to receive the username and password data from the user
//who wants to login/authenticate.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginPayload {

    private String username;
    private String password;

}
