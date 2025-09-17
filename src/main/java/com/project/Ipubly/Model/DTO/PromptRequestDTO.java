package com.project.Ipubly.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromptRequestDTO {
    private String promptText;
    private String promptImage;
    private List<String> keywords;
    private String theme;
}
