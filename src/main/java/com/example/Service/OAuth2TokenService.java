package com.example.Service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class OAuth2TokenService {

    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";

    public String getAccessToken(String authorizationCode, String clientId, String clientSecret, String redirectUri) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("code", authorizationCode);
        requestParams.add("client_id", clientId);
        requestParams.add("client_secret", clientSecret);
        requestParams.add("redirect_uri", redirectUri);
        requestParams.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestParams, headers);

        ResponseEntity<String> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();  // Возвращает JSON с access_token
        } else {
            throw new RuntimeException("Error during token exchange: " + response.getStatusCode());
        }
    }
}

