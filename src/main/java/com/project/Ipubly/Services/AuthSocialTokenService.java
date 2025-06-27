package com.project.Ipubly.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class AuthSocialTokenService {
        @Autowired
        private TwitterAuthTokenService twitterAuthTokenService;

    public String RedirectAuthToken(String socialAccount, String state) {
        switch (socialAccount.toLowerCase()) {
            case "twitter":
               return twitterAuthTokenService.RedirectAuthToken(state);

            default:
                 throw new IllegalArgumentException ("Não há support há rede social: " + socialAccount);
        }


    }

}

