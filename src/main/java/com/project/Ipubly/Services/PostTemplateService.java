package com.project.Ipubly.Services;

import com.project.Ipubly.Config.ExceptionAPI;
import com.project.Ipubly.Model.DTO.PostTemplateRequestDTO;
import com.project.Ipubly.Model.DTO.PostTemplateResponseDTO;
import com.project.Ipubly.Model.PostTemplateEntity;
import com.project.Ipubly.Model.UserEntity;
import com.project.Ipubly.Repository.PostTemplateRepository;
import com.project.Ipubly.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PostTemplateService {

    @Autowired
    private PostTemplateRepository postTemplateRepository;

    @Autowired
    private UsersRepository userRepository;

    public List<PostTemplateResponseDTO> findAllByUser(UUID userId) {
        return postTemplateRepository.findAllByUserId(userId)
                .stream()
                .map(this::toDTOWithMessageList)
                .collect(Collectors.toList());
    }

    public PostTemplateResponseDTO findById(UUID id, UUID userId) {
        PostTemplateEntity entity = postTemplateRepository.findById(id)
                .filter(post -> post.getUser().getId().equals(userId))
                .orElseThrow(() -> new ExceptionAPI("Post not found or no permission"));

        return toDTO(entity, "Query performed successfully");
    }

    public PostTemplateResponseDTO createTemplate(PostTemplateRequestDTO dto, UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionAPI("User not found"));

        if (postTemplateRepository.findByNameAndUserId(dto.getName(), userId).isPresent()) {
            throw new ExceptionAPI("Post with this name already exists");
        }

        PostTemplateEntity entity = new PostTemplateEntity();
        entity.setUser(user);
        entity.setName(dto.getName());
        entity.setPromptText(dto.getPromptText());
        entity.setPromptImage(dto.getPromptImage());
        entity.setTargetAudience(dto.getTargetAudience());
        entity.setKeywords(dto.getKeywords());

        return toDTO(postTemplateRepository.save(entity), "Created successfully");
    }

    public PostTemplateResponseDTO updateTemplate(PostTemplateRequestDTO dto, UUID id) {
        PostTemplateEntity entity = postTemplateRepository.findById(id)
                .orElseThrow(() -> new ExceptionAPI("Post not found or no permission"));

        Map<String, String> updatedTemplateFields = new HashMap<>();
        updatedTemplateFields.put("name",
                dto != null && dto.getName() != null ? dto.getName() : entity.getName());
        updatedTemplateFields.put("promptText",
                dto != null && dto.getPromptText() != null ? dto.getPromptText() : entity.getPromptText());
        updatedTemplateFields.put("promptImage",
                dto != null && dto.getPromptImage() != null ? dto.getPromptImage() : entity.getPromptImage());
        updatedTemplateFields.put("targetAudience",
                dto != null && dto.getTargetAudience() != null ? dto.getTargetAudience() : entity.getTargetAudience());
        updatedTemplateFields.put("keywords",
                dto != null && dto.getKeywords() != null ? dto.getKeywords() : entity.getKeywords());


        updatedTemplateFields.forEach((key, value) -> {
            if (value != null && !value.isBlank()) {
                switch (key) {
                    case "name" -> entity.setName(value);
                    case "promptText" -> entity.setPromptText(value);
                    case "promptImage" -> entity.setPromptImage(value);
                    case "targetAudience" -> entity.setTargetAudience(value);
                    case "keywords" -> entity.setKeywords(value);
                }
            }
        });

        return toDTO(postTemplateRepository.save(entity), "Updated successfully");
    }

    public void deleteTemplate(UUID id) {
        PostTemplateEntity entity = postTemplateRepository.findById(id)
                .orElseThrow(() -> new ExceptionAPI("Post not found or no permission"));

        postTemplateRepository.delete(entity);
    }

    private PostTemplateResponseDTO toDTO(PostTemplateEntity entity, String message) {
        PostTemplateResponseDTO dto = new PostTemplateResponseDTO();
        dto.setId(entity.getId());
        dto.setMessage(message);
        dto.setName(entity.getName());
        dto.setPromptText(entity.getPromptText());
        dto.setPromptImage(entity.getPromptImage());
        dto.setTargetAudience(entity.getTargetAudience());
        dto.setKeywords(entity.getKeywords());
        return dto;
    }

    private PostTemplateResponseDTO toDTOWithMessageList(PostTemplateEntity entity) {
        return toDTO(entity, "Item loaded successfully");
    }

    public void validateRequestDTOTemplate(PostTemplateRequestDTO dto) {
        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new ExceptionAPI("Name cannot be empty");
        }
        if (dto.getPromptText() == null || dto.getPromptText().isEmpty()) {
            throw new ExceptionAPI("Prompt text cannot be empty");
        }
    }
}
