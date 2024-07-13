package com.example.apiGateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;


//This is the class that filters out the unauthorized requests
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;
    @Autowired
    private RestTemplate restTemplate;

    public AuthenticationFilter() {
        super(Config.class);
    }

    //This method takes the token from header and validates it
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            //ServerHttpRequest request = null;
            ServerHttpRequest request = null;
            if (validator.isSecured.test(exchange.getRequest())) {
                //checking if the request contains Authorization header or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    //throwing exception if the Authorization header is missing
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap("{\"message\": \"Authorization header is missing\"}".getBytes());
                    return exchange.getResponse().writeWith(Flux.just(buffer));
                }

                //getting the authorization header
                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                String token = null;
                //extracting the jwt token from the authorization header
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }

                //REST call to identitymanagement service to check if the jwt token is valid
                ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8765/api/v1/auth/validate-token?token=" + token, String.class);
                String response = responseEntity.getBody();
                System.out.println("msg:" + response);
                //if this is true,then token is valid
                if (response.startsWith("Success")) {
                    //because after '!!',we have the email in response that we receive
                    String userInfo = response.split("!!")[1];

                    //email and roles are seperated by '::'
                    String[] userDetails = userInfo.split("::");
                    //userDetails[0] string starts with the Email:
                    //Email and it's value are seperated by ':'
                    String email = userDetails[0].split(":")[1];
                    //userDetails[1] string starts with the Roles:,
                    //Roles and it's value are seperated by ':'
                    String roleString = userDetails[1].split(":")[1];
                    System.out.println("email::" + email + "roles" + roleString);

                    //get roles from roleString
                    //roles are seperated  by '-' in roleString
                    String[] roles = roleString.split("-");
                    System.out.println("hare::"+roles.length);
                    for(String s:roles){
                        System.out.println("hoo:"+s);
                    }
                    //add in the request header email and roles
                    request = (ServerHttpRequest) exchange.getRequest()
                            .mutate()
                            //we are passing the email of the current user who sent the
                            //request in the header of the request before forwarding the request,
                            //to the destined microservice,
                            //so that the other microservices know who it is that sent the request,
                            // so that microservices can perform operations according to that
                            .header("CurrentUser", email)
                            .header("CurrentUserRoles", roles)
                            .build();
                } else {
                    System.out.println("unauthorized exception");
                    //if the token isn't valid or expired,throwing exception
                    //throw new RuntimeException("Access Denied Or Unauthorized Access!!" + response.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    // Create JSON string with the response message
                    String jsonResponse = String.format("{\"message\": \"%s\"}", response);
                    // Convert the JSON string to bytes and wrap it in a DataBuffer
                    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(jsonResponse.getBytes());
                    return exchange.getResponse().writeWith(Flux.just(buffer));
                }
            }
            return chain.filter(exchange.mutate().request(request).build());
        });
    }

    public static class Config {

    }
}
