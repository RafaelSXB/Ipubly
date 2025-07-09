package com.project.Ipubly.Model.DTO;

import com.project.Ipubly.Model.Enum.Provider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveSocialAccountDTO {

    private UUID id;
    private UUID user;
    private String name;
    private String accessToken;
    private String refreshToken;
    private String scope;
    private Provider provider;
    private Integer provider_social_id;
    private Boolean isActive;
    private OffsetDateTime expiresAt;

}
