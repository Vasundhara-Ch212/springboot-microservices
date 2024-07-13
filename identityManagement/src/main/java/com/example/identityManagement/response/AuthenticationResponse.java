package com.example.identityManagement.response;

import lombok.*;

import java.util.List;

//This is the response we send back to the user
//once the user is successfully authenticated.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String authenticationToken;
    private String username;
    private String tokenType = "Bearer";
    private List<String> roles;

    public AuthenticationResponse(String authenticationToken, String username) {
        this.authenticationToken = authenticationToken;
        this.username = username;
    }
}