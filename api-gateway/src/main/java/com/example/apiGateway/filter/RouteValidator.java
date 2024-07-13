package com.example.apiGateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    //These api end points do not need to be authenticated,so
    //these don't need to pass the AuthenticationFilter check
    public static final List<String> openApiEndpoints = List.of(
            "/api/v1/user/register",
            "/api/v1/auth/login",
            "/api/v1/auth/validate-token"
    );

    //If any request or Api end point is not in the above list,then they must be authenticated
    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
