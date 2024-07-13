package com.example.identityManagement.controller;

import com.example.identityManagement.Util.UtilMethod;
import com.example.identityManagement.payload.LoginPayload;
import com.example.identityManagement.payload.AddUserInfoPayload;
import com.example.identityManagement.payload.UpdateUserInfoPayload;
import com.example.identityManagement.response.APIResponse;
import com.example.identityManagement.security.JwtProvider;
import com.example.identityManagement.service.AuthService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private AuthService authService;

    //Api to login/authenticate the user and generate jwt token and return to the user,
    //which user uses on subsequent requests
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginPayload payload) {
        return authService.login(payload);
    }

    //Api to validate the jwt token sent by user
    @GetMapping("/validate-token")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
        String response = authService.validateToken(token);
        if (response.startsWith("Invalid")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>("Success..JWT Token is Valid!!" + response, HttpStatus.OK);
    }

    //Api to add the user info in UserInfo table in UserInfoDb
    @PostMapping("/add-user-info")
    @Hidden
    public ResponseEntity<String> addUserInfo(@RequestBody AddUserInfoPayload payload) {
        authService.addUserInfo(payload);
        return new ResponseEntity<>("User Info added", HttpStatus.OK);
    }

    //Api to update the user info in UserInfo table in UserInfoDb
    @PutMapping("/update-user-info/{id}")
    @Hidden
    public ResponseEntity<String> updateUserInfo(@PathVariable(value = "id") Integer id, @RequestBody UpdateUserInfoPayload payload) {
        authService.updateUserInfo(id, payload);
        return new ResponseEntity<>("User Info updated", HttpStatus.OK);
    }

    //Api to delete the user info in UserInfo table in UserInfoDb
    @DeleteMapping("/delete-user-info/{id}")
    @Hidden
    public ResponseEntity<String> deleteUserInfo(@PathVariable(value = "id") Integer id) {
        authService.deleteUserInfo(id);
        return new ResponseEntity<>("User Info deleted", HttpStatus.OK);
    }

}
