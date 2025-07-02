package com.project.Ipubly.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import com.project.Ipubly.Model.AuthTokenEntity;
import com.project.Ipubly.Model.DTO.AuthTokenResponseDTO;
import com.project.Ipubly.Model.DTO.AuthTokenSaveDTO;

import com.project.Ipubly.Model.Enum.Provider;
import com.project.Ipubly.Model.SocialAccountEntity;
import com.project.Ipubly.Model.UserEntity;
import com.project.Ipubly.Repository.AuthTokenRepository;
import com.project.Ipubly.Repository.UsersRepository;
import com.project.Ipubly.Services.Interfaces.InterfaceSocialAuthTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.Map;
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

    @Autowired
    private UsersRepository usersRepository;

    private final String codeVerifier = "teste";

    @Override
    public String RedirectAuthToken(String state) {
        String scopes = "tweet.read tweet.write users.read offline.access";
        String authUrl = "https://twitter.com/i/oauth2/authorize?" +
                "response_type=code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(scopes, StandardCharsets.UTF_8) +
                "&state=" + state +
                "&code_challenge=" + codeVerifier +
                "&code_challenge_method=plain";
        ;
        return authUrl;
    }


    @Override
    public AuthTokenSaveDTO GeneratorSocialAuthToken(String code) {
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
            AuthTokenSaveDTO authTokenSaveDTO = new AuthTokenSaveDTO();
            authTokenSaveDTO.setAccessToken(tokenNew.get("access_token").asText());
            authTokenSaveDTO.setRefreshToken(tokenNew.get("refresh_token").asText());
            authTokenSaveDTO.setScope(tokenNew.get("scope").asText());
            authTokenSaveDTO.setProvider(Provider.TWITTER);
            authTokenSaveDTO.setExpiresAt(expireDate);
            authTokenSaveDTO.setUser(UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName()));

            System.out.println("AuthTokenSaveDTO: " + authTokenSaveDTO.toString());
            return authTokenSaveDTO;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new AuthTokenSaveDTO();
    }

    @Override
    public AuthTokenResponseDTO saveSocialAuthToken(String saveDTO) throws JsonProcessingException {

        JsonNode JsonSaveDTO = new JsonMapper().readTree(saveDTO);

        AuthTokenSaveDTO authTokenSaveDTO = new AuthTokenSaveDTO();
        authTokenSaveDTO.setAccessToken(JsonSaveDTO.get("accessToken").asText());
        authTokenSaveDTO.setRefreshToken(JsonSaveDTO.get("refreshToken").asText());
        authTokenSaveDTO.setScope(JsonSaveDTO.get("scope").asText());
        authTokenSaveDTO.setProvider(Provider.valueOf(JsonSaveDTO.get("provider").asText()));
        authTokenSaveDTO.setExpiresAt(OffsetDateTime.parse(JsonSaveDTO.get("expiresAt").asText()));
        authTokenSaveDTO.setUser(UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName()));

        Optional<UserEntity> userOptional = usersRepository.findById(authTokenSaveDTO.getUser());
        AuthTokenResponseDTO authTokenResponseDTO;
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            AuthTokenEntity authTokenEntity = new AuthTokenEntity();
            authTokenEntity.setAccessToken(authTokenSaveDTO.getAccessToken());
            authTokenEntity.setRefreshToken(authTokenSaveDTO.getRefreshToken());
            authTokenEntity.setScope(authTokenSaveDTO.getScope());
            authTokenEntity.setProvider(authTokenSaveDTO.getProvider());
            authTokenEntity.setUser(user);
            authTokenEntity.setExpiresAt(authTokenSaveDTO.getExpiresAt());
            authTokenRepository.save(authTokenEntity);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            System.out.println(authTokenEntity.getAccessToken());
            headers.setBearerAuth(authTokenEntity.getAccessToken());

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);

            System.out.println(entity);
            System.out.println(headers);
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    "https://api.twitter.com/2/users/me",
                    HttpMethod.GET,
                    entity,
                    JsonNode.class
            );


            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode userInfo = response.getBody();
                if (userInfo != null) {
                    String twitterUsername = userInfo.get("data").get("username").asText();
                    if (saveSocialAccount(authTokenEntity, user, twitterUsername)) {
                        authTokenResponseDTO = new AuthTokenResponseDTO();
                        authTokenResponseDTO.setSocialName(twitterUsername);
                        authTokenResponseDTO.setUsername(user.getUsername());
                        authTokenResponseDTO.setScope(authTokenEntity.getScope());
                        authTokenResponseDTO.setProvider(authTokenEntity.getProvider().name());
                        authTokenResponseDTO.setMessage("Auth token saved successfully");

                    } else {
                        throw new IllegalArgumentException("Failed to save social account");
                    }

                } else {
                    throw new IllegalArgumentException("Failed to retrieve user information from Twitter");
                }
            } else {
                throw new IllegalArgumentException("Failed to connect to Twitter API: " + response.getStatusCode());
            }

        } else {
            throw new IllegalArgumentException("User not found for the provided userId");
        }
        return authTokenResponseDTO;
    }

    @Override
    public Boolean saveSocialAccount(AuthTokenEntity authTokenEntity, UserEntity userEntity, String name) {
        if (authTokenEntity != null && userEntity != null) {
            SocialAccountEntity socialAccountEntity = new SocialAccountEntity();
            socialAccountEntity.setUser(userEntity);
            socialAccountEntity.setProvider(authTokenEntity.getProvider());
            socialAccountEntity.setOauthToken(authTokenEntity);
            socialAccountEntity.setName(name);
            authTokenRepository.save(authTokenEntity);
            return true;
        } else {
           return false;
        }
    }

}

