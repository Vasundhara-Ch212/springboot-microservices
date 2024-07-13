package com.example.userManagement.service;

import com.example.userManagement.Payload.*;
import com.example.userManagement.Util.UtilMethod;
import com.example.userManagement.model.Address;
import com.example.userManagement.model.User;
import com.example.userManagement.repository.AddressRepository;
import com.example.userManagement.repository.UserRepository;
import com.example.userManagement.response.APIResponse;
import com.example.userManagement.response.CurrentUserFetchedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    private RestTemplate restTemplate;

    //Method to check if the email exists already in the db or not.
    private Boolean checkIfEmailExists(String email) {
        if (email != null && email != "" && !email.isBlank()) {
            Optional<User> isUser1 = userRepository.findByEmail(email);
            if (isUser1.isPresent()) {
                return true;
            }
        }
        return false;
    }

    //Method to check if the phoneNo exists already in the db or not.
    private Boolean checkIfPhoneNoExists(String phoneNo) {
        if (phoneNo != null && phoneNo != "" && !phoneNo.isBlank()) {
            Optional<User> isUser2 = userRepository.findByPhoneNo(phoneNo);
            if (isUser2.isPresent()) {
                return true;
            }
        }
        return false;
    }

    //Method to check if email is valid
    private Boolean isEmailValid(String email) {
//        The following restrictions are imposed in the email address’ local part by using this regex:
//        1.It allows numeric values from 0 to 9.
//        2.Both uppercase and lowercase letters from a to z are allowed.
//        3.Allowed are underscore “_”, hyphen “-“, and dot “.”
//        4.Dot isn’t allowed at the start and end of the local part.
//        5.Consecutive dots aren’t allowed.
//        6.For the local part, a maximum of 64 characters are allowed.

//        Restrictions for the domain part in this regular expression include:
//        1.It allows numeric values from 0 to 9.
//        2.We allow both uppercase and lowercase letters from a to z.
//        3.Hyphen “-” and dot “.” aren’t allowed at the start and end of the domain part.
//        4.No consecutive dots.
        String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

        //If the email matches the above regular expression,then it returns true
        //else it returns false.
        return Pattern.compile(regex)
                .matcher(email)
                .matches();
    }

    //Method to check if phoneNo is valid
    private Boolean isPhoneNoValid(String phoneNo) {
        //This regular expression checks if the phoneNo has 10 digits or not
        String regex = "^\\d{10}$";

        //If the phoneNo matches the above regular expression,then it returns true
        //else it returns false.
        return Pattern.compile(regex)
                .matcher(phoneNo)
                .matches();
    }

    //Method to check if name is valid
    private Boolean isNameValid(String name) {
        //This regular expression checks if the name has atleast 3 characters or not
        //and only accepts alphabetic characters
        String regex = "^[A-Za-z]{3,}$";

        //If the name matches the above regular expression,then it returns true
        //else it returns false.
        return Pattern.compile(regex)
                .matcher(name)
                .matches();
    }

    //Method to add or update the user address
    private void addOrUpdateAddress(AddressPayload addressPayload, Address address) {
        if (addressPayload.getStreet() != null && !addressPayload.getStreet().isBlank()) {
            address.setStreet(addressPayload.getStreet());
        }
        if (addressPayload.getCity() != null && !addressPayload.getCity().isBlank()) {
            address.setCity(addressPayload.getCity());
        }
        if (addressPayload.getDistrict() != null && !addressPayload.getDistrict().isBlank()) {
            address.setDistrict(addressPayload.getDistrict());
        }
        if (addressPayload.getState() != null && !addressPayload.getState().isBlank()) {
            address.setState(addressPayload.getState());
        }
        if (addressPayload.getPincode() != null) {
            address.setPincode(addressPayload.getPincode());
        }
    }

    //find the User by username
    private User findByUsername(String username) {
        //getting the user from the db by searching by username of the current user
        //If the username contains '@',that means username is email
        //otherwise username is phoneNo
        User user = null;
        if (username.contains("@")) {
            Optional<User> isUser = userRepository.findByEmail(username);
            user = isUser.get();
        } else {
            Optional<User> isUser = userRepository.findByPhoneNo(username);
            user = isUser.get();
        }
        return user;
    }

    //Method to register or add the user to the db
    public APIResponse registerUser(RegisterUserPayload payload) {
        //If email is not provided,
        //then we have to send back the user this response
        if (payload.getEmail() == null || payload.getEmail().isBlank()) {
            return APIResponse.builder()
                    .timestamp(LocalDateTime.now().toString())
                    .message("Please provide email..email cannot be empty or null")
                    .status("Error!!" + HttpStatus.BAD_REQUEST.name() + " " + HttpStatus.BAD_REQUEST.value())
                    .path(UtilMethod.getPath())
                    .build();
        }
        //If password is not provided,
        //then we have to send back the user this response
        if (payload.getPassword() == null || payload.getPassword().isBlank()) {
            return APIResponse.builder()
                    .timestamp(LocalDateTime.now().toString())
                    .message("Please provide the password..Password cannot be empty or null")
                    .status("Error!!" + HttpStatus.BAD_REQUEST.name() + " " + HttpStatus.BAD_REQUEST.value())
                    .path(UtilMethod.getPath())
                    .build();
        }
        //If name is not provided,
        //then we have to send back the user this response
        if (payload.getName() == null || payload.getName().isBlank()) {
            return APIResponse.builder()
                    .timestamp(LocalDateTime.now().toString())
                    .message("Please provide the name..name cannot be empty or null")
                    .status("Error!!" + HttpStatus.BAD_REQUEST.name() + " " + HttpStatus.BAD_REQUEST.value())
                    .path(UtilMethod.getPath())
                    .build();
        }

        //check if email and phoneNo are valid
        Boolean emailValid = true, phoneNoValid = true, nameValid = true;
        emailValid = isEmailValid(payload.getEmail());
        nameValid = isNameValid(payload.getName());
        if (payload.getPhoneNo() != null) {
            phoneNoValid = isPhoneNoValid(payload.getPhoneNo());
        }
        //if email isn't valid,then we send this response
        if (!emailValid) {
            return APIResponse.builder()
                    .timestamp(LocalDateTime.now().toString())
                    .message("Provided email isn't valid.." +
                            "Please Enter a valid email!!")
                    .status("Error!!" + HttpStatus.BAD_REQUEST.name() + " " + HttpStatus.BAD_REQUEST.value())
                    .path(UtilMethod.getPath())
                    .build();
        }
        //if phoneNo isn't valid,then we send this response
        if (!phoneNoValid) {
            return APIResponse.builder()
                    .timestamp(LocalDateTime.now().toString())
                    .message("Provided Phone No isn't valid.." +
                            "Please enter a valid phone No" + "\n" +
                            "Phone No must contain exactly 10 digits")
                    .status("Error!!" + HttpStatus.BAD_REQUEST.name() + " " + HttpStatus.BAD_REQUEST.value())
                    .path(UtilMethod.getPath())
                    .build();
        }
        //if phoneNo isn't valid,then we send this response
        if (!nameValid) {
            return APIResponse.builder()
                    .timestamp(LocalDateTime.now().toString())
                    .message("name must contain atleast 3 characters..Please provide valid name")
                    .status("Error!!" + HttpStatus.BAD_REQUEST.name() + " " + HttpStatus.BAD_REQUEST.value())
                    .path(UtilMethod.getPath())
                    .build();
        }

        //before adding the new user,first check if email or PhoneNo already exists
        Boolean emailExists = checkIfEmailExists(payload.getEmail());
        Boolean phoneNoExists = checkIfPhoneNoExists(payload.getPhoneNo());
        //if email already exists,then we send this response
        if (emailExists) {
            return APIResponse.builder()
                    .timestamp(LocalDateTime.now().toString())
                    .message("Provided email already exists")
                    .status("Error!!" + HttpStatus.CONFLICT.name() + " " + HttpStatus.CONFLICT.value())
                    .path(UtilMethod.getPath())
                    .build();
        }
        //If phoneNo already exists,then we send this response
        if (phoneNoExists) {
            return APIResponse.builder()
                    .timestamp(LocalDateTime.now().toString())
                    .message("PhoneNo is linked with another account.Please provide another PhoneNo")
                    .status("Error!!" + HttpStatus.CONFLICT.name() + " " + HttpStatus.CONFLICT.value())
                    .path(UtilMethod.getPath())
                    .build();
        }

        //adding the new user to db
        User user = new User();
        user.setId(sequenceGeneratorService.getSequneceNo(User.SEQUENCE_NAME));
        user.setName(payload.getName());
        user.setEmail(payload.getEmail());
        user.setPhoneNo(payload.getPhoneNo());

        //adding the user address
        AddressPayload addressPayload = payload.getAddress();
        Address address = null;
        if (addressPayload != null) {
            address = new Address();
            //setting the id for new address document
            address.setId(sequenceGeneratorService.getSequneceNo(Address.SEQUENCE_NAME));
            addOrUpdateAddress(addressPayload, address);
        }
        user.setAddress(address);

        try {
            //calling the addUserInfo rest end point of identitymanagement microservice to add the
            //user details in UserInfoDb which corresponds to identitymanagement microservice
            String response = restTemplate.postForEntity("http://localhost:8765/api/v1/auth/add-user-info",
                    AddUserInfoPayload.builder()
                            .userId(user.getId())
                            .email(user.getEmail())
                            .phoneNo(user.getPhoneNo())
                            .roles(payload.getRoles())
                            .password(payload.getPassword())
                            .build(),
                    String.class).getBody();

            //saving the new user and the address to db only after the rest call is successful,
            //because same user data must be stored in both databases and data must be same.
            //and also we need every user data that's added in userDb to be added in UserInfoDb too.
            //because we need to do all the login related and token generation and token validation logic
            //in identitymanagement service.so,we need all the user login details in UserInfoDb too.
            //if the rest call isn't successful,we don't want the user details and address details to be stored only in userDb
            //but not in UserInfoDb.so,saving the new user and address info into userDb only after the rest call is successful.
            //and also only if the addressPayload is not null,then address is not null,then only we save it to db
            if (address != null) {
                addressRepository.save(address);
            }
            userRepository.save(user);

        } catch (Exception ex) {
            //returning the response if the rest call isn't successful
            return APIResponse.builder()
                    .timestamp(LocalDateTime.now().toString())
                    .message("Failed to add the user details to the db!!!" + ex.getMessage())
                    .status("Error!!" + HttpStatus.BAD_GATEWAY.name() + " " + HttpStatus.BAD_GATEWAY.value())
                    .path(UtilMethod.getPath())
                    .build();
        }

        //returning the response,once adding the user is successful
        return APIResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .message("User Added!!")
                .status("Success!!" + HttpStatus.OK.name() + " " + HttpStatus.OK.value())
                .path(UtilMethod.getPath())
                .build();
    }

    //Method to update the existing user details.
    public APIResponse updateUser(UpdateUserPayload payload, String email) {
        //getting the User by email from db
        Optional<User> isUser = userRepository.findByEmail(email);
        User user = isUser.get();

        //updating the phone no
        //but before updating,first check if phoneNo is valid
        if (payload.getPhoneNo() != null) {
            Boolean phoneNoValid = isPhoneNoValid(payload.getPhoneNo());
            if (!phoneNoValid) {
                return APIResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .message("Provided Phone No isn't valid.." +
                                "Please enter a valid phone No" + "\n" +
                                "Phone No must contain exactly 10 digits")
                        .status("Error!!" + HttpStatus.BAD_REQUEST.name() + " " + HttpStatus.BAD_REQUEST.value())
                        .path(UtilMethod.getPath())
                        .build();
            }
            //Now,check if phoneNo already exists in db
            Boolean phoneNoExists = checkIfPhoneNoExists(payload.getPhoneNo());
            if (phoneNoExists) {
                return APIResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .message("PhoneNo is linked with another account.Please provide another PhoneNo")
                        .status("Error!!" + HttpStatus.CONFLICT.name() + " " + HttpStatus.CONFLICT.value())
                        .path(UtilMethod.getPath())
                        .build();
            }
            user.setPhoneNo(payload.getPhoneNo());
        }

        //updating the address
        AddressPayload addressPayload = payload.getAddress();
        Address address = null;
        if (addressPayload != null) {
            //if the current user doesn't have any address,we create new address obj
            //and store the details in that.else,we store them in the existing address
            //object corresponding to this user.
            address = user.getAddress();
            if (address == null) {
                address = new Address();
                address.setId(sequenceGeneratorService.getSequneceNo(Address.SEQUENCE_NAME));
                addOrUpdateAddress(addressPayload, address);
            } else {
                addOrUpdateAddress(addressPayload, address);
            }
            user.setAddress(address);
        }

        try {
            //rest call to updateUserInfo of identitymanagement service
            //to update the user details in userInfoDb too.
            Map<String, Integer> params = new HashMap<>();
            params.put("id", user.getId());
            restTemplate.put("http://localhost:8765/api/v1/auth/update-user-info/{id}",
                    UpdateUserInfoPayload.builder()
                            .phoneNo(user.getPhoneNo())
                            .roles(payload.getRoles())
                            .password(payload.getPassword())
                            .build(),
                    params
            );
            //saving the user and address with updated details to db only after the rest call is successful,
            //because same user data must be stored in both databases and data must be same.
            //if the rest call isn't successful,we don't want the user details to be updated only in userDb
            //but not in UserInfoDb.so,saving the updated user and address into UserDb only after the rest call is successful.
            //if the rest call isn't successful,we don't want the user details to be updated only in userDb
            //but not in UserInfoDb.so,saving the updated user and address info into userDb only after the rest call is successful.
            //and also,only if addressPayload is not null,then only address is not null,
            //so then only we save it db
            if (address != null) {
                addressRepository.save(address);
            }
            userRepository.save(user);

        } catch (Exception ex) {
            //returning the response if the rest call isn't successful
            return APIResponse.builder()
                    .timestamp(LocalDateTime.now().toString())
                    .message("Error Updating the user details to the db!!\r\n" + ex.getMessage())
                    .status("Error!!" + HttpStatus.BAD_GATEWAY.name() + " " + HttpStatus.BAD_GATEWAY.value())
                    .path(UtilMethod.getPath())
                    .build();
        }
        //returning the success response,once updating the user is successful
        return APIResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .message("User Updated!!")
                .status("Success!!" + HttpStatus.OK.name() + " " + HttpStatus.OK.value())
                .path(UtilMethod.getPath())
                .build();
    }

    //Method to delete the user from db
    public APIResponse deleteUser(String email) {
        //getting the User by email from db
        Optional<User> isUser = userRepository.findByEmail(email);
        User user = isUser.get();
        try {
            //rest call to updateUserInfo of identitymanagement service
            //to update the user details in userInfoDb too.
            Map<String, Integer> params = new HashMap<>();
            params.put("id", user.getId());
            restTemplate.delete("http://localhost:8765/api/v1/auth/delete-user-info/{id}",
                    params);

            //deleting the user from db only after the rest call is successful,
            //because same user data must be stored in both databases and data must be same.
            //if the rest call isn't successful,we don't want the user to be deleted only in userDb
            //but not in UserInfoDb.so,deleting the user from userDb only after the rest call is successful.
            //we have to also delete the address when we delete user
            if (user.getAddress() != null) {
                addressRepository.delete(user.getAddress());
            }
            userRepository.delete(user);
        } catch (Exception ex) {
            //if the rest call fails,
            //this response is sent back to the user.
            return APIResponse.builder()
                    .timestamp(LocalDateTime.now().toString())
                    .message("Error deleting the user from db!!\n" + ex.getMessage())
                    .status("Error!!" + HttpStatus.BAD_GATEWAY.name() + " " + HttpStatus.BAD_GATEWAY.value())
                    .path(UtilMethod.getPath())
                    .build();
        }

        //sending the success response,once deleting the user is successful
        return APIResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .message("User Deleted!!")
                .status("Success!!" + HttpStatus.OK.name() + " " + HttpStatus.OK.value())
                .path(UtilMethod.getPath())
                .build();
    }

    //Method to fetch all the user details of the current user
    public CurrentUserFetchedResponse fetchCurrentUser(String email, String[] roles) {
        //getting the User by email from db
        Optional<User> isUser = userRepository.findByEmail(email);
        User user = isUser.get();

        //return the response
        return CurrentUserFetchedResponse.builder()
                .address(user.getAddress())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .roles(roles)
                .build();
    }


}
