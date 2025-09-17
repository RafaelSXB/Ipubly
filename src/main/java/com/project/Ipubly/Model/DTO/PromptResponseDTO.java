package com.project.Ipubly.Model.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromptResponseDTO {
    private String status;
    private String message;
    private Object result;

}