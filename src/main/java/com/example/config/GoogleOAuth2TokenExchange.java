//package com.example.config;
//
//import org.springframework.http.*;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//
//public class GoogleOAuth2TokenExchange {
//
//    private static final String TOKEN_URL = "";
//
//    public String getAccessToken(String authorizationCode, String clientId, String clientSecret, String redirectUri) {
//        // Создаем клиент для отправки запроса
//        RestTemplate restTemplate = new RestTemplate();
//
//        // Параметры запроса для обмена кода авторизации на токен
//        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
//        requestParams.add("code", authorizationCode);
//        requestParams.add("client_id", clientId);
//        requestParams.add("client_secret", clientSecret);
//        requestParams.add("redirect_uri", redirectUri);
//        requestParams.add("grant_type", "authorization_code");
//
//        // Настройка HTTP-запроса
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestParams, headers);
//
//        // Отправка POST-запроса
//        ResponseEntity<String> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, entity, String.class);
//
//        // Ответ от Google с токеном
//        if (response.getStatusCode() == HttpStatus.OK) {
//            return response.getBody();  // Ответ будет содержать токен в JSON-формате
//        } else {
//            throw new RuntimeException("Error during token exchange: " + response.getStatusCode());
//        }
//    }
//}
//
