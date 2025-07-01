package com.project.Ipubly.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthTokenResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String scope;
    private String provider;
    private String userId;
    private String expiresAt;
}
