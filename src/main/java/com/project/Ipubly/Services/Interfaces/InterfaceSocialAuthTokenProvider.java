package com.project.Ipubly.Services.Interfaces;


import com.project.Ipubly.Model.DTO.AuthTokenSaveDTO;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public interface InterfaceSocialAuthTokenProvider{

    public String RedirectAuthToken();
    public String GeneratorSocialAuthToken(String code);
    public String saveSocialAuthToken(AuthTokenSaveDTO authTokenSaveDTO) ;




}
