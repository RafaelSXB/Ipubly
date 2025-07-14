package com.project.Ipubly.Model.DTO;

import lombok.Data;

import java.util.UUID;

@Data
public class PostTemplateResponseDTO {
    private UUID id;
    private String message;
    private String name;
    private String promptText;
    private String promptImage;
    private String targetAudience;
    private String keywords;
}