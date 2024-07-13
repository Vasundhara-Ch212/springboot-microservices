package com.example.identityManagement.security;

import com.example.identityManagement.model.Role;
import com.example.identityManagement.model.UserInfo;
import com.example.identityManagement.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

//This class implements UserDetails which is needed
//for authentication provider
public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private List<GrantedAuthority> authorities;

    public CustomUserDetails(UserInfo userInfo, List<String> roles) {
        //setting the username and password
        username = userInfo.getEmail();
        password = userInfo.getPassword();
        //we grant the permission for the roles assigned to the user
        authorities = roles.stream()
                .map(role -> {
                    return new SimpleGrantedAuthority(role);
                }).collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
