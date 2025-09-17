package com.project.Ipubly.Services;

import com.project.Ipubly.Model.DTO.PromptRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class GenareteAITextService {


        RestTemplate restTemplate = new RestTemplate();
        @Value("${openai.api.key}")
        private String apiKey;

        @Value("${openai.api.url}")
        private String apiUrl;

        @Value("${openai.model}")
        private String model;

        @Value("${openai.image.size}")
        private String imageSize;

        public String generateText(PromptRequestDTO dto) {
            String prompt = buildPromptText(dto);

            String url = apiUrl + "/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> message = Map.of("role", "user", "content", prompt);

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", List.of(message));
            body.put("temperature", 0.7);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> messageMap = (Map<String, Object>) choices.get(0).get("message");
                return (String) messageMap.get("content");
            }

            return "No response from OpenAI.";
        }

        public String generateImage(PromptRequestDTO dto) {
            String prompt = buildPromptImage(dto); // Monta o prompt de imagem

            String url = apiUrl + "/images/generations";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("prompt", prompt);
            body.put("n", 1);
            body.put("size", imageSize);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            List<Map<String, String>> data = (List<Map<String, String>>) response.getBody().get("data");
            if (data != null && !data.isEmpty()) {
                return data.get(0).get("url");
            }

            return "No image generated.";
        }

        private String buildPromptText(PromptRequestDTO dto) {
            StringBuilder sb = new StringBuilder();

            if (dto.getTheme() != null)
                sb.append("Theme: ").append(dto.getTheme()).append(". ");

            if (dto.getKeywords() != null && !dto.getKeywords().isEmpty())
                sb.append("Use these keywords: ").append(String.join(", ", dto.getKeywords())).append(". ");

            if (dto.getPromptText() != null)
                sb.append(dto.getPromptText());

            return sb.toString();
        }

        private String buildPromptImage(PromptRequestDTO dto) {
            StringBuilder sb = new StringBuilder();

            if (dto.getTheme() != null)
                sb.append("Theme: ").append(dto.getTheme()).append(". ");

            if (dto.getKeywords() != null && !dto.getKeywords().isEmpty())
                sb.append("Elements: ").append(String.join(", ", dto.getKeywords())).append(". ");

            if (dto.getPromptImage() != null)
                sb.append(dto.getPromptImage());

            return sb.toString();
        }
}

