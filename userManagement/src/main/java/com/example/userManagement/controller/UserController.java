package com.example.userManagement.controller;

import com.example.userManagement.Payload.RegisterUserPayload;
import com.example.userManagement.Payload.UpdateUserPayload;
import com.example.userManagement.model.User;
import com.example.userManagement.response.APIResponse;
import com.example.userManagement.response.CurrentUserFetchedResponse;
import com.example.userManagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    //api to register the user or add a new user
    @PostMapping("/register")
    public ResponseEntity<APIResponse> registerUser(@RequestBody RegisterUserPayload payload) {
        APIResponse response = userService.registerUser(payload);
        int statusCode = Integer.parseInt(response.getStatus().split(" ")[1]);
        return new ResponseEntity<>(response, HttpStatus.valueOf(statusCode));
    }

    //api to update the current user,who sent the request
    @PutMapping("/update-me")
    public ResponseEntity<APIResponse> updateUser(@RequestBody UpdateUserPayload payload, @RequestHeader("CurrentUser") String email, @RequestHeader("CurrentUserRoles") String[] roles) {
        System.out.println("current::"+email);
        APIResponse response = userService.updateUser(payload, email);
        int statusCode = Integer.parseInt(response.getStatus().split(" ")[1]);
        return new ResponseEntity<>(response, HttpStatus.valueOf(statusCode));
    }

    //api to delete the current user,who sent the request
    @DeleteMapping("/delete-me")
    public ResponseEntity<APIResponse> deleteUser(@RequestHeader("CurrentUser") String email, @RequestHeader("CurrentUserRoles") String[] roles) {
        APIResponse response = userService.deleteUser(email);
        int statusCode = Integer.parseInt(response.getStatus().split(" ")[1]);
        return new ResponseEntity<>(response, HttpStatus.valueOf(statusCode));
    }

    //api to fetch the user,who sent the request
    @GetMapping("/fetch-me")
    public CurrentUserFetchedResponse fetchCurrentUser(@RequestHeader("CurrentUser") String email, @RequestHeader("CurrentUserRoles") String[] roles) {
        return userService.fetchCurrentUser(email, roles);
    }

}
