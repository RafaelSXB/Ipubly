package com.project.Ipubly.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Ipubly.Model.DTO.SaveSocialAccountDTO;
import com.project.Ipubly.Model.DTO.SaveSocialAccountDTO;
import com.project.Ipubly.Model.DTO.SocialAccountResponseDTO;
import com.project.Ipubly.Model.Enum.Provider;
import com.project.Ipubly.Services.Interfaces.InterfaceSocialAuthTokenProvider;
import com.project.Ipubly.Services.TwitterAuthTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import com.fasterxml.jackson.core.JsonProcessingException;


import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping({"/socialAccount/auth"})
public class SocialTokenAuthController {

    @Autowired
    TwitterAuthTokenService twitterAuthTokenService;

    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("/{socialAccount}/redirect")
    public String authRedirect(@PathVariable("socialAccount") String SocialAccount) {
        try {
            if (SocialAccount == null || SocialAccount.isBlank()) {
                return "forward:/socialAccount/auth/error?message=" + URLEncoder.encode("SocialAccount não pode ser nulo ou vazio.", StandardCharsets.UTF_8);
            }


            return "forward:/socialAccount/auth/" + URLEncoder.encode(SocialAccount, StandardCharsets.UTF_8 + URLEncoder.encode("/redirect", StandardCharsets.UTF_8));


        } catch (Exception e) {
            e.printStackTrace();
            return "forward:/socialAccount/auth/error" + "?message=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @GetMapping("/twitter/redirect")
    public RedirectView twitterAuthRedirect() {
        try {
            String redirectUrl = twitterAuthTokenService.RedirectAuthToken(SecurityContextHolder.getContext().getAuthentication().getName());
            return new RedirectView(redirectUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return new RedirectView("/socialAccount/auth/error?message=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }

    @GetMapping("/socialAccount/auth/error")
    public ResponseEntity<Object> authError(@RequestParam(value = "message", required = false) String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Erro ao redirecionar para a autenticação da rede social.");
        errorResponse.put("message", message);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @GetMapping("/twitter/callback")
    public String twitterCallback(@RequestParam("code") String code) throws JsonProcessingException {
        InterfaceSocialAuthTokenProvider interfaceSocialAuthTokenProvider = twitterAuthTokenService;
        String response = objectMapper.writeValueAsString(interfaceSocialAuthTokenProvider.GeneratorSocialAuthToken(code));

        return "forward:/socialAccount/auth/save" + "?teste=" + URLEncoder.encode(response, StandardCharsets.UTF_8);

    }

    @GetMapping("/save")
    @ResponseBody
    public ResponseEntity<SocialAccountResponseDTO> saveSocialAuthToken(@RequestParam(value = "teste", required = false) String test) {
        try {

            InterfaceSocialAuthTokenProvider socialAuthToken = twitterAuthTokenService;
           SocialAccountResponseDTO SaveSocialAccountDTO = socialAuthToken.returnSocialAuthToken(test);

            return ResponseEntity.ok(SaveSocialAccountDTO);
        } catch (Exception e) {
            e.printStackTrace();
            SocialAccountResponseDTO SaveSocialAccountDTO = new SocialAccountResponseDTO();
            SaveSocialAccountDTO.setMessage("Failed to save social account token:  " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new SocialAccountResponseDTO());
        }
    }


}
