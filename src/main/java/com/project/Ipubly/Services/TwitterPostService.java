package com.project.Ipubly.Services;

import com.project.Ipubly.Repository.UsersRepository;
import com.twitter.clientlib.model.TweetCreateRequest;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.TweetCreateResponse;
import com.twitter.clientlib.ApiException;

import java.util.Optional;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



    
public class TwitterPostService {

    private static final Logger logger = LoggerFactory.getLogger(TwitterPostService.class);
    @Value("${twitter.api.key}")
    private String apiKey;

    @Value("${twitter.api.secret}")
    private String apiSecret;


    @Value("${twitter.access.secret}")
    private String accessSecret;

    @Value("${twitter.acess.client.id}")
    private String client;

    @Value("${twitter.acess.client.secret}")
    private String clientSecret;



    @Autowired
    private regenareteTokenService regenareteTokenService;

    @Autowired
    private valideTokenService valideTokenService;

    @Autowired
    private UsersRepository usersRepository;

 /*    public String postarTweet(String texto) {

       if (!valideTokenService.validateToken("ADMIN")) {
            ArrayList<String> user = regenareteTokenService.renewActulToken(client, clientSecret, "ADMIN");
            if (user.isEmpty()) {

                System.out.println("Erro ao renovar o token");
                return "Erro ao renovar o token";
            }
            accessToken = user.get(0);
            refreString = user.get(1);
        } else {
            Optional<usersEntity> users = FindUsersRepository.findByUsername("ADMIN");
            if (users.isPresent()) {
                usersEntity user = users.get();
                accessToken = user.getAPITOKEN();
                refreString = user.getAPIREFRESHTOKEN();
            } else {
                return "Usuário não encontrado";
            }

        }

        TwitterCredentialsOAuth2 credentials = new TwitterCredentialsOAuth2(
                client, clientSecret, accessToken, refreString);

        TwitterApi twitterApi = new TwitterApi(credentials);
        logger.info("Iniciando postagem no Twitter: " + credentials.getTwitterOAuth2ClientSecret());
        logger.info("Acessando o Twitter com o token: " + accessToken);
        logger.info("Acessando o Twitter com o refresh token: " + refreString);
        logger.info("Texto do tweet: " + texto);
        try {

            TweetCreateRequest request = new TweetCreateRequest().text(texto);

            TweetCreateResponse response = twitterApi.tweets()
                    .createTweet(request)
                    .execute();
            logger.info("Tweet postado com sucesso: " + response.getData().getText());
            return "Tweet postado com sucesso: " + response.getData().getText();

        } catch (ApiException e) {

            throw new RuntimeException("Erro ao postar tweet: " + e.getResponseBody(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado: " + e.getMessage(), e);
        }
    } */
}
