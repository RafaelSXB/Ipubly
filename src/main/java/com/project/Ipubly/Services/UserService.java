package com.project.Ipubly.Services;

import com.project.Ipubly.Config.ExceptionAPI;
import com.project.Ipubly.Model.DTO.UserRequestDTO;
import com.project.Ipubly.Model.DTO.UserResponseDTO;
import com.project.Ipubly.Model.UserEntity;
import com.project.Ipubly.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UsersRepository usersRepository;


    public UserResponseDTO addNewUser(UserRequestDTO userRequestDTO) {
        UserEntity newUser = new UserEntity();
        newUser.setUsername(userRequestDTO.getUsername());
        newUser.setName(userRequestDTO.getName());
        newUser.setEmail(userRequestDTO.getEmail());
        newUser.setPassword(encriptPassword(userRequestDTO.getPassword()));
        usersRepository.save(newUser);

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUsername(newUser.getUsername());
        userResponseDTO.setMessage("User added successfully");
        return userResponseDTO;
    }


    private String encriptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        password = passwordEncoder.encode(password);

        return password;
    }

    public void validateNewUser(UserRequestDTO userRequestDTO) {
        Optional<UserEntity> existingUser = usersRepository.findByUsernameOrEmail(userRequestDTO.getUsername(), userRequestDTO.getEmail());
        if (existingUser.isPresent()) {
            if (existingUser.get().getUsername().equals(userRequestDTO.getUsername())) {
                throw new ExceptionAPI("Username already exists");
            }
            throw new ExceptionAPI("Email already exists");
        }

        if (userRequestDTO.getUsername() == null || userRequestDTO.getUsername().isEmpty()) {
            throw new ExceptionAPI("Username is required");
        }
        if (userRequestDTO.getName() == null || userRequestDTO.getName().isEmpty()) {
            throw new ExceptionAPI("Name is required");
        }
        if (userRequestDTO.getEmail() == null || userRequestDTO.getEmail().isEmpty()) {
            throw new ExceptionAPI("Email is required");
        }
        if (userRequestDTO.getPassword() == null || userRequestDTO.getPassword().isEmpty()) {
            throw new ExceptionAPI("Password is required");
        }
    }


}
