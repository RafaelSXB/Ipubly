package com.project.Ipubly.Services;

import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.Get2TweetsSearchRecentResponse;
import com.twitter.clientlib.model.Tweet;
import com.twitter.clientlib.ApiClient;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class TwitterSearchService {

    @Value("${twitter.bearer.token}")
    private String bearerToken;
    ApiResponse<Get2TweetsSearchRecentResponse> result = null;

    @SuppressWarnings("null")
    public List<String> buscarPorHashtag(String hashtag) throws ApiException {
        Thread.currentThread().setName("TwitterSearchService");
        ApiClient apiClient = new ApiClient();
        TwitterApi api = new TwitterApi(apiClient);
        api.getApiClient().setBearerToken(bearerToken);
        try {

            getHeaders(result = api.tweets().tweetsRecentSearch("#" + hashtag).maxResults(10).executeWithHttpInfo());

            System.out.println("Total de tweets encontrados: " + result.getData().getData().size());
            if (result.getData() == null || result.getData().toJson().isEmpty()) {
                System.out.println("⚠️ Nenhum tweet encontrado com a hashtag: " + hashtag);
                return java.util.Collections.emptyList();
            }

            List<String> tweetTexts = new java.util.ArrayList<>();
            if (result.getData() != null && result.getData().getData() != null) {
                for (Tweet tweet : result.getData().getData()) {
                    tweetTexts.add(tweet.getText());
                }
            }
            System.out.println("Tweets encontrados: " + tweetTexts);
            return tweetTexts;

        } catch (ApiException e) {
            if (e.getCode() == 429) {
                System.out.println("❌ Erro ao buscar tweets: ");
                System.out.println("Limite de requisições atingido. Aguardando 15 minutos.");
                Map<String, List<String>> headers = e.getResponseHeaders();
                if (headers != null && headers.containsKey("x-rate-limit-reset")) {
                    List<String> resetList = headers.get("x-rate-limit-reset");
                    if (resetList != null && !resetList.isEmpty()) {
                        long resetEpoch = Long.parseLong(resetList.get(0));
                        System.out.println("⏱ Aguardando até: " + new java.util.Date(resetEpoch * 1000));
                    }
                }

            } else {
                System.out.println("❌ Erro ao buscar tweets: " + e.getResponseBody());

            }
        }

        return java.util.Collections.emptyList();
    }

    public void getHeaders(ApiResponse<Get2TweetsSearchRecentResponse> results) {
        Map<String, List<String>> headers = results.getHeaders();
        String resetStr = null;
        if (headers.containsKey("x-rate-limit-remaining")) {
            List<String> remainingList = headers.get("x-rate-limit-remaining");
            System.out.println("⚠️ Requisições restantes: " + remainingList.get(0));
        }
        if (headers != null && headers.containsKey("x-rate-limit-reset")) {
            List<String> resetList = headers.get("x-rate-limit-reset");
            if (resetList != null && !resetList.isEmpty()) {
                resetStr = resetList.get(0);
            }
        }

        if (resetStr != null) {
            long resetEpoch = Long.parseLong(resetStr);

            System.out.println("⏱ Aguardando até: " + new java.util.Date(resetEpoch * 1000));
        } else {
            System.out.println("⏱ Esperando 15 minutos por padrão.");
        }
    }

}
