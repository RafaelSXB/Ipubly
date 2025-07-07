package com.project.Ipubly.Services.Interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.Ipubly.Model.DTO.ProfileSocialAccountDTO;
import com.project.Ipubly.Model.DTO.SocialAccountResponseDTO;
import com.project.Ipubly.Model.SocialAccountEntity;
import com.project.Ipubly.Model.DTO.SaveSocialAccountDTO;
import com.project.Ipubly.Model.UserEntity;

import java.io.IOException;


public interface InterfaceSocialAuthTokenProvider{

    public String RedirectAuthToken(String state);
    public SaveSocialAccountDTO GeneratorSocialAuthToken(String code);
    public SocialAccountResponseDTO returnSocialAuthToken(String test) throws JsonProcessingException;
    public ProfileSocialAccountDTO getSocialAccountProfile(String accessToken);
    public SocialAccountResponseDTO saveSocialAccount(UserEntity userEntity, SaveSocialAccountDTO saveSocialAccountDTO, ProfileSocialAccountDTO profileSocialAccountDTO) ;


}
