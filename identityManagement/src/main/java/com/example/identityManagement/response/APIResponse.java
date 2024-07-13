package com.example.identityManagement.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//response to sent back to the user
@Getter
@Setter
@Builder
public class APIResponse {
    private String status;
    private String message;
    private String path;
    private String timestamp;
}
