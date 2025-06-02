package com.project.Ipubly.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.project.Ipubly.Repository.FindUsersRepository;
import com.project.Ipubly.Services.regenareteTokenService;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.URLEncoder;
import java.util.Date;
import java.nio.charset.StandardCharsets;

@RestController
public class TwitterOAuthController {

    @Value("${twitter.acess.client.id}")
    private String clientId;

    @Value("${twitter.acess.client.secret}")
    private String clientSecret;

    @Value("${twitter.redirect.uri}")
    private String redirectUri;

    @Autowired
    private  FindUsersRepository FindUsersRepository;

    @Autowired
    private regenareteTokenService regenareteTokenService;

    private final String codeVerifier = "challenge123"; // simples para testes

    @GetMapping("/twitter/auth")
    public RedirectView authRedirect() {

        String scopes = "tweet.read tweet.write users.read offline.access";
        String authUrl = "https://twitter.com/i/oauth2/authorize?" +
                "response_type=code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(scopes, StandardCharsets.UTF_8) +
                "&state=ipubly123" +
                "&code_challenge=" + codeVerifier +
                "&code_challenge_method=plain";

        return new RedirectView(authUrl);
    }

    @GetMapping("/twitter/callback")
    public ResponseEntity<String> twitterCallback(@RequestParam("code") String code) {
        String tokenUrl = "https://api.twitter.com/2/oauth2/token";
        usersEntity user = new usersEntity();
        

        RestTemplate restTemplate = new RestTemplate();

        String credentials = clientId + ":" + clientSecret;
        String base64Credentials = java.util.Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + base64Credentials);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret); // ESSENCIAL
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
            Date expireDate = new Date(expire);
            System.out.println("Token expires at: " + expireDate);
            System.out.println(tokenNew.get("access_token"));
            System.out.println(tokenNew.get("refresh_token"));
            System.out.println(tokenNew.get("expires_in"));

        
            regenareteTokenService.addNewToken(tokenNew.get("access_token").asText(), tokenNew.get("refresh_token").asText(), expire);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(response.getBody());
    }

}
