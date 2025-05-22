package com.hrplatform.hrplatform.controller;

import com.hrplatform.hrplatform.model.LoginRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${KEYCLOAK_CLIENT_SECRET}")
    private String clientSecret;

    @Value("${KEYCLOAK_CLIENT_ID}")
    private String clientId;


    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody LoginRequest loginRequest) {
        String url = "http://host.docker.internal:8080/realms/hrPlatform/protocol/openid-connect/token";

        Map<String, String> bodyParams = new HashMap<>();
        bodyParams.put("grant_type", "password");
        bodyParams.put("client_id", clientId);
        bodyParams.put("client_secret", clientSecret);
        bodyParams.put("username", loginRequest.getUsername());
        bodyParams.put("password", loginRequest.getPassword());

        StringBuilder requestBody = new StringBuilder();
        for (Map.Entry<String, String> entry : bodyParams.entrySet()) {
            if (requestBody.length() > 0) requestBody.append("&");
            requestBody.append(entry.getKey()).append("=").append(entry.getValue());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while fetching token: " + e.getMessage());
        }
    }
}