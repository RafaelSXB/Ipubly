package com.project.Ipubly.Model.DTO;

import lombok.Data;

@Data
public class PostTemplateRequestDTO {
    private String name;
    private String promptText;
    private String promptImage;
    private String targetAudience;
    private String keywords;
}