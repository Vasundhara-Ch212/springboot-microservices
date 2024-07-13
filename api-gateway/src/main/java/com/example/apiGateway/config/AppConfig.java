//package com.example.apiGateway.feign;
//
//import com.example.apiGateway.dto.CurrentUserInfoResponse;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@FeignClient("AUTH-SERVICE")
//public interface GatewayInterface {
//
//    @GetMapping("/validate-token")
//    public String validateToken(@RequestParam("token") String token);
//
//    @GetMapping("/getCurrentUser")
//    public ResponseEntity<CurrentUserInfoResponse> getCurrentUser(String token);
//
//}
package com.example.apiGateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig{
    @Bean
    public RestTemplate template(){
        return new RestTemplate();
    }
}