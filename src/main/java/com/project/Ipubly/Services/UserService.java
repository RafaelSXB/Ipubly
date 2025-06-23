package com.project.Ipubly.Services;

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

    public List<String> validateNewUser(UserRequestDTO userRequestDTO) {
        Optional<UserEntity> existingUser = usersRepository.findByUsername(userRequestDTO.getUsername());
        if (existingUser.isPresent())
        {
            return List.of("Username already exists");
        }

        List<String> errors = new ArrayList<>();

        if (userRequestDTO.getUsername() == null || userRequestDTO.getUsername().isEmpty()) {
            errors.add("Username is required");
        }
        if (userRequestDTO.getName() == null || userRequestDTO.getName().isEmpty()) {
            errors.add("Name is required");
        }
        if (userRequestDTO.getEmail() == null || userRequestDTO.getEmail().isEmpty()) {
            errors.add("Email is required");
        }
        if (userRequestDTO.getPassword() == null || userRequestDTO.getPassword().isEmpty()) {
            errors.add("Password is required");
        }

        if (!errors.isEmpty()) {
            return errors;
        }
        return List.of();
    }


}
