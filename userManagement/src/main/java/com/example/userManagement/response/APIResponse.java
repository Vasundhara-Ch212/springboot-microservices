package com.example.userManagement.response;

import lombok.Builder;
import lombok.Data;

//This is used to send the response to the user
@Data
@Builder
public class APIResponse {
    private String status;
    private String message;
    private String path;
    private String timestamp;
}
