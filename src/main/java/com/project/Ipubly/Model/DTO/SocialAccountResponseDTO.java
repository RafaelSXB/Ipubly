package com.project.Ipubly.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialAccountResponseDTO {
    private String socialName;
    private String username;
    private String scope;
    private String provider;
    private String message;

}
