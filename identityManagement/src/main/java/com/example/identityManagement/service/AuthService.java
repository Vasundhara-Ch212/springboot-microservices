package com.example.identityManagement.service;

import com.example.identityManagement.Util.UtilMethod;
import com.example.identityManagement.model.Role;
import com.example.identityManagement.model.UserInfo;
import com.example.identityManagement.payload.AddUserInfoPayload;
import com.example.identityManagement.payload.UpdateUserInfoPayload;
import com.example.identityManagement.repository.RoleRepository;
import com.example.identityManagement.repository.UserInfoRepository;
import com.example.identityManagement.payload.LoginPayload;
import com.example.identityManagement.response.APIResponse;
import com.example.identityManagement.response.AuthenticationResponse;
import com.example.identityManagement.security.JwtProvider;
import com.example.identityManagement.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtProvider jwtProvider;

    //method to get the current user
    public List<String> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        Optional<UserInfo> isUserInfo = userInfoRepository.findByEmail(principal.getUsername());
        UserInfo userInfo = isUserInfo.get();
        List<String> roles = userInfo.getRoles().stream().map(
                roleId -> {
                    Optional<Role> role = roleRepository.findById(roleId);
                    return role.get().getRole();
                }
        ).collect(Collectors.toList());
        return roles;
    }

    //Method to login/authenticate the user
    public ResponseEntity<?> login(LoginPayload payload) {
        if((payload.getUsername()==null || payload.getUsername().isBlank())
                || (payload.getPassword()==null || payload.getPassword().isBlank())){
            return new ResponseEntity<>(APIResponse.builder()
                    .timestamp(LocalDateTime.now().toString())
                    .message("Username and Password cannot be empty")
                    .status("Error!!" + HttpStatus.BAD_REQUEST.name() + " " + HttpStatus.BAD_REQUEST.value())
                    .path(UtilMethod.getPath())
                    .build(),HttpStatus.BAD_REQUEST);
        }
        Authentication authentication = null;
        try {
            //check if the username and password sent by user are valid or not
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(payload.getUsername(),
                            payload.getPassword()));
        } catch (Exception e) {
            return new ResponseEntity<>(APIResponse.builder()
                    .message(e.getMessage())
                    .path(UtilMethod.getPath())
                    .status("Error!!" + HttpStatus.UNAUTHORIZED.name() + " " + HttpStatus.UNAUTHORIZED.value())
                    .timestamp(LocalDateTime.now().toString())
                    .build(), HttpStatus.UNAUTHORIZED);
        }
        System.out.println("auth::"+authentication);
        //setting the authentication object
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //getting the userDetails of current loggedin user from authentication object
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //find the UserInfo of the current user by email
        UserInfo userInfo = userInfoRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        //generating the jwt token since we know the user is authorized user
        String authenticationToken = jwtProvider.generateToken(userInfo.getEmail());

        //Once the token is genertaed,sending the response back to the user
        //and including jwt token that's just generated and the roles of the user
        //in the response
        AuthenticationResponse response = new AuthenticationResponse(authenticationToken, payload.getUsername());
        //mapping the roleIds to the roleNames,since we are storing the
        //roleIds only in UserInfo table
        response.setRoles(userInfo.getRoles().stream().map(roleId -> {
            Optional<Role> role = roleRepository.findById(roleId);
            return role.get().getRole();
        }).collect(Collectors.toList()));
        //returning the response
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Method to get all the roles present in Role table in db
    public List<String> getAllRoles() {
        return roleRepository.findAll()
                .stream().map(Role::getRole).toList();
    }

    //method to validate the jwt token
    public String validateToken(String token) {
        //check if token is valid
        String str = jwtProvider.validateToken(token);
        //if token is Invalid,we return str
        if (str.startsWith("Invalid")) {
            return str;
        } else {
            //If token is valid
            //get the username from token
            String email = jwtProvider.extractUsername(token);
            //Find the UserInfo by username
            Optional<UserInfo> isUserInfo = userInfoRepository.findByEmail(email);
            UserInfo userInfo = isUserInfo.get();
            //get the roles of user
            Set<Integer> roleIds = userInfo.getRoles();
            //map the role ids into role names
            List<String> roles = roleIds.stream().map(roleId -> {
                Optional<Role> isRole = roleRepository.findById(roleId);
                Role role = isRole.get();
                return role.getRole();
            }).collect(Collectors.toList());
            //convert roles into a string by seperating each role by ':"
            String rolesString = "";
            for (String st : roles) {
                rolesString += (st + "-");
            }
            //Add the email and the roles of the current user
            //to the string we return
            str = "Email:" + email + "::" + "Roles:" + rolesString;
            return str;
        }
    }

    //Method to add the UserInfo of current user
    public void addUserInfo(AddUserInfoPayload payload) {
        //creating new UserInfo object and setting the data
        UserInfo userInfo = new UserInfo();
        userInfo.setId(payload.getUserId());
        userInfo.setEmail(payload.getEmail());
        userInfo.setPhoneNo(payload.getPhoneNo());
        userInfo.setPassword(encodePassword(payload.getPassword()));

        //mapping roles to roleIds
        List<Integer> rolesList = payload.getRoles().stream().map(
                roleName -> {
                    Optional<Role> role = roleRepository.findByRoleIgnoreCaseIn(roleName);
                    return role.get().getId();
                }
        ).collect(Collectors.toList());
        Set<Integer> roles = new HashSet<>(rolesList);
        userInfo.setRoles(roles);

        //This userId is the foreignkey in this UserInfo table,
        //which represents the user in User table in UserDb of usermanagement service
        userInfo.setUserId(payload.getUserId());

        //saving new UserInfo to db
        userInfoRepository.save(userInfo);
    }

    //Method to update the UserInfo of current user
    public void updateUserInfo(Integer id, UpdateUserInfoPayload payload) {
        //getting the UserInfo from db by id
        //because we want to update the UserInfo which has id equal to id
        //passed,anyway id and userId have same value for every document
        Optional<UserInfo> isUserInfo = userInfoRepository.findById(id);
        UserInfo userInfo = isUserInfo.get();

        //updating the user roles
        if (payload.getRoles() != null && !payload.getRoles().isEmpty()) {
            //mapping the roles to roleIds
            List<Integer> rolesList = payload.getRoles().stream().map(
                    roleName -> {
                        Optional<Role> role = roleRepository.findByRoleIgnoreCaseIn(roleName);
                        return role.get().getId();
                    }
            ).collect(Collectors.toList());
            Set<Integer> roles = new HashSet<>(rolesList);
            userInfo.setRoles(roles);
        }

        //updating the password
        if (payload.getPassword() != null && !payload.getPassword().isBlank()) {
            userInfo.setPassword(encodePassword(payload.getPassword()));
        }

        //updating the phone no
        if (payload.getPhoneNo() != null && !payload.getPhoneNo().isBlank()) {
            userInfo.setPhoneNo(payload.getPhoneNo());
        }

        //save the updated userInfo to db
        userInfoRepository.save(userInfo);
    }

    //Method to delete the UserInfo of current user
    public void deleteUserInfo(Integer id) {
        //getting the UserInfo by id
        Optional<UserInfo> isUserInfo = userInfoRepository.findById(id);
        UserInfo userInfo = isUserInfo.get();
        //deleting the UserInfo
        userInfoRepository.delete(userInfo);
    }

    //Method to encode the password because we need to
    //store the encoded password in db for security reasons
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
