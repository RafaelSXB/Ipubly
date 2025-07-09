package com.project.Ipubly.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.project.Ipubly.Model.DTO.ProfileSocialAccountDTO;
import com.project.Ipubly.Model.DTO.SocialAccountResponseDTO;
import com.project.Ipubly.Model.SocialAccountEntity;
import com.project.Ipubly.Model.DTO.SaveSocialAccountDTO;
import com.project.Ipubly.Model.Enum.Provider;
import com.project.Ipubly.Model.UserEntity;
import com.project.Ipubly.Repository.SocialAccountRepository;
import com.project.Ipubly.Repository.UsersRepository;
import com.project.Ipubly.Services.Interfaces.InterfaceSocialAuthTokenProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Service
public class TwitterAuthTokenService implements InterfaceSocialAuthTokenProvider {

    @Autowired
    private SocialAccountRepository SocialAccountRepository;

    @Autowired
    ObjectMapper objectMapper;

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
        return authUrl;
    }


    @Override
    public SaveSocialAccountDTO GeneratorSocialAuthToken(String code) {
        String tokenUrl = "https://api.twitter.com/2/oauth2/token";
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
        body.add("client_secret", clientSecret);
        body.add("code_verifier", codeVerifier);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalArgumentException("Failed to connect to the Twitter API: " + response.getStatusCode());
        }
        try {
            JsonNode tokenNew = JsonMapper.builder().build().readTree(response.getBody());
            long expireInSeconds = tokenNew.get("expires_in").asLong();
            OffsetDateTime expireDate = OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")).plusSeconds(expireInSeconds - 600);

            SaveSocialAccountDTO SaveSocialAccountDTO = new SaveSocialAccountDTO();
            SaveSocialAccountDTO.setAccessToken(tokenNew.get("access_token").asText());
            SaveSocialAccountDTO.setRefreshToken(tokenNew.get("refresh_token").asText());
            SaveSocialAccountDTO.setScope(tokenNew.get("scope").asText());
            SaveSocialAccountDTO.setProvider(Provider.TWITTER);
            SaveSocialAccountDTO.setExpiresAt(expireDate);
            SaveSocialAccountDTO.setUser(UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName()));

            return SaveSocialAccountDTO;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing Twitter token response", e);
        }
    }

    @Override
    @Transactional 
    public SocialAccountResponseDTO returnSocialAuthToken(String saveDTO) throws JsonProcessingException {

        SaveSocialAccountDTO saveSocialAccountDTO = objectMapper.readValue(saveDTO, SaveSocialAccountDTO.class);

       Optional<SocialAccountEntity> existingAccount = SocialAccountRepository.findByUserId_IdAndProvider(saveSocialAccountDTO.getUser(), saveSocialAccountDTO.getProvider());
       Optional<UserEntity> userEntity = usersRepository.findById(saveSocialAccountDTO.getUser());
       ProfileSocialAccountDTO profileSocialAccountDTO = getSocialAccountProfile(saveSocialAccountDTO.getAccessToken());

       SocialAccountEntity socialAccountEntity = new SocialAccountEntity();
         if (existingAccount.isPresent()) {
             socialAccountEntity = existingAccount.get();
             saveSocialAccountDTO.setId(socialAccountEntity.getId());

             return saveSocialAccount(userEntity.get(), saveSocialAccountDTO, profileSocialAccountDTO);
         }

        return saveSocialAccount(userEntity.get(), saveSocialAccountDTO, profileSocialAccountDTO);
    }



   @Override
    public SocialAccountResponseDTO saveSocialAccount(UserEntity userEntity, SaveSocialAccountDTO saveSocialAccountDTO, ProfileSocialAccountDTO profileSocialAccountDTO) {

       if (saveSocialAccountDTO.getId() == null) {
           SocialAccountEntity socialAccountEntity = new SocialAccountEntity();

           socialAccountEntity.setUserId(userEntity);
           socialAccountEntity.setProvider(saveSocialAccountDTO.getProvider());
           socialAccountEntity.setAccessToken(saveSocialAccountDTO.getAccessToken());
           socialAccountEntity.setRefreshToken(saveSocialAccountDTO.getRefreshToken());
           socialAccountEntity.setScope(saveSocialAccountDTO.getScope());
           socialAccountEntity.setExpiresAt(saveSocialAccountDTO.getExpiresAt());
           socialAccountEntity.setIsActive(true);
           socialAccountEntity.setName(profileSocialAccountDTO.getSocialName());
           socialAccountEntity.setProviderAccountId(profileSocialAccountDTO.getSocialId());

           SocialAccountRepository.save(socialAccountEntity);

           SocialAccountResponseDTO socialAccountResponseDTO = new SocialAccountResponseDTO();
           socialAccountResponseDTO.setMessage("Social account saved successfully.");
           socialAccountResponseDTO.setUsername(userEntity.getUsername());
           socialAccountResponseDTO.setProvider(socialAccountEntity.getProvider().toString());
           socialAccountResponseDTO.setScope(socialAccountEntity.getScope());

           return socialAccountResponseDTO;
       }

       SocialAccountEntity socialAccountEntity = new SocialAccountEntity();
       socialAccountEntity.setId(saveSocialAccountDTO.getId());
       socialAccountEntity.setUserId(userEntity);
       socialAccountEntity.setAccessToken(saveSocialAccountDTO.getAccessToken());
       socialAccountEntity.setRefreshToken(saveSocialAccountDTO.getRefreshToken());
       socialAccountEntity.setScope(saveSocialAccountDTO.getScope());
       socialAccountEntity.setExpiresAt(saveSocialAccountDTO.getExpiresAt());
       socialAccountEntity.setIsActive(true);
       socialAccountEntity.setProvider(saveSocialAccountDTO.getProvider());
       socialAccountEntity.setName(profileSocialAccountDTO.getSocialName());
       socialAccountEntity.setProviderAccountId(profileSocialAccountDTO.getSocialId());

       SocialAccountRepository.save(socialAccountEntity);

       SocialAccountResponseDTO SaveSocialAccountDTO = new SocialAccountResponseDTO();
       SaveSocialAccountDTO.setMessage("Social account updated successfully.");
       SaveSocialAccountDTO.setUsername(userEntity.getUsername());
       SaveSocialAccountDTO.setProvider(socialAccountEntity.getProvider().toString());
       SaveSocialAccountDTO.setScope(socialAccountEntity.getScope());
       SaveSocialAccountDTO.setSocialName(socialAccountEntity.getName());

         return SaveSocialAccountDTO;
   }

    @Override
    public ProfileSocialAccountDTO getSocialAccountProfile(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                "https://api.twitter.com/2/users/me",
                HttpMethod.GET,
                entity,
                JsonNode.class
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalArgumentException("Failed to connect to the Twitter API or retrieve user information: " + response.getStatusCode() + response.getBody());
        }
        ProfileSocialAccountDTO profile = new ProfileSocialAccountDTO();
        JsonNode userInfo = response.getBody().get("data");
        profile.setSocialId(userInfo.get("id").asText());
        profile.setSocialName(userInfo.get("username").asText());

        return profile;
    }
}