package com.project.Ipubly.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import com.project.Ipubly.Model.AuthTokenEntity;
import com.project.Ipubly.Model.DTO.AuthTokenSaveDTO;

import com.project.Ipubly.Model.Enum.Provider;
import com.project.Ipubly.Repository.AuthTokenRepository;
import com.project.Ipubly.Services.Interfaces.InterfaceSocialAuthTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class TwitterAuthTokenService implements InterfaceSocialAuthTokenProvider {

    @Autowired
    AuthTokenRepository authTokenRepository;

    @Value("${twitter.access.client.secret}")
    private String clientSecret;

    @Value("${twitter.redirect.uri}")
    private String redirectUri;

    @Value("${twitter.access.client.id}")
    private String clientId;

    private final String codeVerifier = "teste";

    @Override
    public String RedirectAuthToken() {
        String scopes = "tweet.read tweet.write users.read offline.access";
        String authUrl = "https://twitter.com/i/oauth2/authorize?" +
                "response_type=code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(scopes, StandardCharsets.UTF_8) +
                "&state=ipubly125" +
                "&code_challenge=" + codeVerifier +
                "&code_challenge_method=plain";
        ;
        return authUrl;
    }


    @Override
    public String GeneratorSocialAuthToken(String code) {
        String tokenUrl = "https://api.twitter.com/2/oauth2/token";

        RestTemplate restTemplate = new RestTemplate();

        String credentials = clientId + ":" + clientSecret;
        String base64Credentials = java.util.Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + base64Credentials);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", String.valueOf(code));
        body.add("redirect_uri", redirectUri);
        body.add("client_secret", clientSecret);
        body.add("code_verifier", codeVerifier);


        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        System.out.println("Request: " + request);
        System.out.println("Headers: " + headers);
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);
        Long expire = new Date().getTime();
        try {
            JsonNode tokenNew = JsonMapper.builder().build().readTree(response.getBody());
            Long expireTime = tokenNew.get("expires_in").asLong() * 1000;
            expire += expireTime;
            expire -= 600000L;
            OffsetDateTime expireDate = OffsetDateTime.ofInstant(new Date(expire).toInstant(), ZoneId.of("America/Sao_Paulo"));
            System.out.println("Token expires at: " + expireDate);
            System.out.println(tokenNew.get("access_token"));
            System.out.println(tokenNew.get("refresh_token"));
            System.out.println(tokenNew.get("expires_in"));
            AuthTokenSaveDTO authTokenSaveDTO = new AuthTokenSaveDTO();
            authTokenSaveDTO.setAccessToken(tokenNew.get("access_token").asText());
            authTokenSaveDTO.setRefreshToken(tokenNew.get("refresh_token").asText());
            authTokenSaveDTO.setScope(tokenNew.get("scope").asText());
            authTokenSaveDTO.setProvider(Provider.TWITTER);
            authTokenSaveDTO.setExpiresAt(expireDate);


        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to get access token: " + response.getStatusCode());
        }
    }

    @Override
    public String saveSocialAuthToken(AuthTokenSaveDTO authTokenSaveDTO) {
        if (!(authTokenSaveDTO == null)) {
            AuthTokenEntity authTokenEntity = new AuthTokenEntity();
            authTokenEntity.setAccessToken(authTokenSaveDTO.getAccessToken());
            authTokenEntity.setRefreshToken(authTokenSaveDTO.getRefreshToken());
            authTokenEntity.setScope(authTokenSaveDTO.getScope());
            authTokenEntity.setProvider(authTokenSaveDTO.getProvider());
            authTokenEntity.setUser(authTokenSaveDTO.getUser());

            authTokenRepository.save(authTokenEntity);
            throw new IllegalArgumentException("AuthTokenSaveDTO or userId cannot be null");
        }
        return "Auth token saved successfully";
    }

}
