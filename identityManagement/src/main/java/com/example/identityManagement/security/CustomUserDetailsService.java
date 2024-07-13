package com.example.identityManagement.security;

import com.example.identityManagement.model.Role;
import com.example.identityManagement.model.UserInfo;
import com.example.identityManagement.repository.RoleRepository;
import com.example.identityManagement.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private RoleRepository roleRepository;

    //this method loads the user from db by checking the username
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //since the user can login either by email or phoneNo,
        //so the username can be either of them.so we need to
        //compare the username with both of these.
        //if we don't find the user in db with the username passed,we throw exception
        Optional<UserInfo> userInfo1 = userInfoRepository.findByEmail(username);
        Optional<UserInfo> userInfo2 = userInfoRepository.findByPhoneNo(username);
        if (userInfo1.isEmpty() && userInfo2.isEmpty()) {
            throw new UsernameNotFoundException("No user found with provided username : " + username);
        }
        UserInfo userInfo = null;
        if (userInfo1.isPresent()) {
            userInfo = userInfo1.get();
        }
        if (userInfo2.isPresent()) {
            userInfo = userInfo2.get();
        }
        //mapping the role ids to role names
        List<String> roles = userInfo.getRoles().stream()
                .map(roleId -> {
                    Optional<Role> role = roleRepository.findById(roleId);
                    return role.get().getRole();
                }).collect(Collectors.toList());

        //we convert the UserInfo type object to type UserDetails
        //which is needed for authentication provider
        return new CustomUserDetails(userInfo, roles);
    }
}
