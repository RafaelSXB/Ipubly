package com.project.Ipubly.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Ipubly.Model.UserEntity;
import com.project.Ipubly.Repository.UsersRepository;
import com.project.Ipubly.Services.AuthSocialTokenService;
import com.project.Ipubly.Services.Interfaces.InterfaceSocialAuthTokenProvider;
import com.project.Ipubly.Services.TwitterAuthTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.project.Ipubly.Services.regenareteTokenService;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.swing.plaf.synth.SynthTabbedPaneUI;
import javax.ws.rs.Path;
import java.net.URLEncoder;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping({"/socialAccount/auth"})
public class SocialTokenAuthController {
    @Autowired
    private mainController mainController;

    @Autowired
    AuthSocialTokenService authSocialTokenService;

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
            System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
            String redirectUrl = authSocialTokenService.RedirectAuthToken("twitter", SecurityContextHolder.getContext().getAuthentication().getName());
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
    public ResponseEntity<Object> saveSocialAuthToken(@RequestParam(value = "teste", required = false) String test) {
        try {
            System.out.println("Entrou no saveSocialAuthToken" + test);

            JsonNode jsonNode = new JsonMapper().readTree(test);

            System.out.println("JsonNode: " + jsonNode);
            return ResponseEntity.ok("Social auth token saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving social auth token: " + e.getMessage());
        }
    }


}
