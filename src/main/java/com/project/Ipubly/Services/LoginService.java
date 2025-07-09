package com.project.Ipubly.Services;

import com.project.Ipubly.Config.ExceptionAPI;
import com.project.Ipubly.Model.DTO.LoginRequestDTO;
import com.project.Ipubly.Model.DTO.LoginResponseDTO;
import com.project.Ipubly.Model.UserEntity;
import com.project.Ipubly.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsersRepository usersRepository;

    public LoginResponseDTO loginAuth(LoginRequestDTO loginRequestDTO) {

        if (loginRequestDTO.getUsername() == null || loginRequestDTO.getPassword() == null) {
            throw new ExceptionAPI("Username and Password are required");
        }

       UserEntity user = usersRepository.findByUsername(loginRequestDTO.getUsername()).orElse(new UserEntity());

        if (!verifyPassword(loginRequestDTO.getPassword(), user.getPassword())){
            throw new ExceptionAPI("Invalid Password or Username");

        };

        String token = jwtService.generateToken(user.getId().toString(), user.getUsername());

        LoginResponseDTO response = new LoginResponseDTO();
        response.setUsername(user.getUsername());
        response.setToken(token);

        response.setMessage("Login successful");

        return response;

    }

    public Boolean verifyPassword(String password, String hashedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(password, hashedPassword);
    }

}