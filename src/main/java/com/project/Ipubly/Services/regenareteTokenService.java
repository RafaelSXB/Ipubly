package com.project.Ipubly.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.Base64;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;


@Service
public class regenareteTokenService {

    /*@Autowired
    private FindUsersRepository FindUsersRepository;

    public String addNewToken(String token, String regenate, Long expire) {
        Optional<usersEntity> users = FindUsersRepository.findByUsername("ADMIN");
        if (users.isPresent()) {
            usersEntity user = users.get();
            user.setAPITOKEN(token);
            user.setAPIREFRESHTOKEN(regenate);
            user.setEXPIRE(expire);
            FindUsersRepository.save(user);

            return "Insert new Token successfully";
        }
        return "User not found";
    }

    public ArrayList<String> renewActulToken(String client, String clientSecret, String username) {
        Optional<usersEntity> users = FindUsersRepository.findByUsername(username);

        if (users.isPresent() && users.get().getEXPIRE() < System.currentTimeMillis()) {
            System.out.println("Entou aqui");
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.twitter.com/2/oauth2/token";
            HttpHeaders headers = new HttpHeaders();
            String credentials = client + ":" + clientSecret;
            String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Authorization", "Basic " + base64Credentials);
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "refresh_token");
            body.add("refresh_token", users.get().getAPIREFRESHTOKEN());
            body.add("client_id", client);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.println("Response: " + response.getBody());
            Long expire = new Date().getTime();


            try {
                JsonNode tokenNew = JsonMapper.builder().build().readTree(response.getBody());
                Long expireTime = tokenNew.get("expires_in").asLong() * 1000;
                expire += expireTime;
                expire -= 600000L;

                usersEntity user = users.get();
                user.setAPITOKEN(tokenNew.get("access_token").asText());
                user.setAPIREFRESHTOKEN(tokenNew.get("refresh_token").asText());
                user.setEXPIRE(expire);
                FindUsersRepository.save(user);

                ArrayList<String> returnUser = new ArrayList<>();

                returnUser.add(user.getAPITOKEN());
                returnUser.add(user.getAPIREFRESHTOKEN());


                return returnUser;

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }


        }
        return new ArrayList<>();
    } */
}
