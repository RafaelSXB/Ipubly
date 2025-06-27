package com.project.Ipubly.Controller;

import com.project.Ipubly.Model.DTO.LoginRequestDTO;
import com.project.Ipubly.Model.DTO.LoginResponseDTO;
import com.project.Ipubly.Services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login/auth")
    public ResponseEntity<LoginResponseDTO>login(@RequestBody(required = false) LoginRequestDTO loginRequest) {

        LoginResponseDTO responseDTO = loginService.loginAuth(loginRequest);
       if(responseDTO.getUsername() == null) {
           return ResponseEntity.badRequest().body(responseDTO);
       }

        return ResponseEntity.ok(responseDTO);
    }
}
