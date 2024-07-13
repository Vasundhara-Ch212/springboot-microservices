package com.example.userManagement.response;

import com.example.userManagement.model.Address;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class CurrentUserFetchedResponse {

    private Integer id;

    private String name;

    private Address address;

    private String email;

    private String phoneNo;

    private String[] roles;

}
