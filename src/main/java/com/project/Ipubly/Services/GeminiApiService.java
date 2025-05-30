package com.project.Ipubly.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.client.RestTemplate;

@Service
public class GeminiApiService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GeminiApiService.class);
    private final String apikey;
    private final String url;
    private final RestTemplate restTemplate;

    public GeminiApiService(@Value("${deepseek.apikey}") String apikey,
            @Value("${deepseek.base-url}") String url, RestTemplate restTemplate) {
        this.restTemplate = new RestTemplate();
        this.apikey = apikey;
        this.url = url;

    }

    protected String generatePostGeminini() {
        Thread.currentThread().setName("GeminiApiService");
        logger.info("Iniciando a geração de post com Gemini API");
        try {
           
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-chat");
            requestBody.put("max_tokens", 1000);
            requestBody.put("reset_context", true);
            requestBody.put("messages", promptPostGemini());
            requestBody.put("temperature", 0.4);
            requestBody.put("max_tokens", 200);

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.setBearerAuth(apikey);

            HttpEntity<Map<String, Object>> requestPostEntity = new HttpEntity<>(requestBody, requestHeaders);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestPostEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                Map<String, Object> firstChoice = choices.get(0);
                Map<String, String> message = (Map<String, String>) firstChoice.get("message");

                String content = message.get("content");
                if (content == null || content.isEmpty()) {
                    logger.warn("Conteúdo vazio retornado pelo Gemini API.");
                    return "";
                }

                logger.info("Texto gerado pelo Gemini: " + content);
                return content;

            }

        } catch (Exception e) {
            logger.error("Erro ao gerar o texto do Gemini: " + e.getMessage(), e);
            return "";
        }
      logger.warn("Resposta do Gemini não foi OK");
        return "";
    }

    protected List<Map<String, String>> promptPostGemini() {

        logger.info("Criando prompt para o Gemini API");
        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", "Crie o tweet."),
                Map.of("role", "user", "content",
                        "Gere um post criativo para redes sociais (Twitter/X) sem carater especial, sem \\\"\\\"\\\",  tema FITNESS com as propriedades abaixo:\n"
                                +
                                "1. Texto de 280 caracteres com chamada para ação.  \n" +
                                "2. Hashtags misturando tendências e nicho\n" +
                                "3. Tom e Estilo STORYTELLING e OPINIÃO POLÊMICA para gerar engajamento.\n" +
                                "4. Evite clichês genéricos;\n" +
                                "5. 100% HUMANO\n\n" +
                                "Me retorne apenas o TEXTO do tweet, sem formatação especial, sem aspas ou caracteres especiais, apenas o conteúdo do tweet. "
                                +
                                "Não inclua emojis ou links. Apenas o texto do tweet, sem formatação especial, sem aspas ou caracteres especiais, apenas o conteúdo do tweet.")
        );

        return messages;
    }

}
