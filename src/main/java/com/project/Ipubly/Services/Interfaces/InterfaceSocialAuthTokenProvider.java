package com.project.Ipubly.Services.Interfaces;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.Ipubly.Model.AuthTokenEntity;
import com.project.Ipubly.Model.DTO.AuthTokenResponseDTO;
import com.project.Ipubly.Model.DTO.AuthTokenSaveDTO;
import com.project.Ipubly.Model.UserEntity;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public interface InterfaceSocialAuthTokenProvider{

    public String RedirectAuthToken(String state);
    public AuthTokenSaveDTO GeneratorSocialAuthToken(String code);
    public AuthTokenResponseDTO saveSocialAuthToken(String saveDTO) throws JsonProcessingException;
    public Boolean saveSocialAccount(AuthTokenEntity authTokenEntity, UserEntity userEntity, String name) ;



}
