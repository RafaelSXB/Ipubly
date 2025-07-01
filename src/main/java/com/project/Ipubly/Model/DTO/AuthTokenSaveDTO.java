package com.project.Ipubly.Model.DTO;

import com.project.Ipubly.Model.Enum.Provider;
import com.project.Ipubly.Model.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenSaveDTO {

    private String accessToken;
    private String refreshToken;
    private String scope;
    private Provider provider;
    private UUID user;
    private OffsetDateTime expiresAt;

}
