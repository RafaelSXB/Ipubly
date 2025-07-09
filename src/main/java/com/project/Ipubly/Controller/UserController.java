package com.project.Ipubly.Controller;

import com.project.Ipubly.Model.DTO.UserRequestDTO;
import com.project.Ipubly.Model.DTO.UserResponseDTO;
import com.project.Ipubly.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/user/register")
    public ResponseEntity<Object> createUser(@RequestBody UserRequestDTO user) {

        userService.validateNewUser(user);
        UserResponseDTO userResponse = userService.addNewUser(user);
        return ResponseEntity.status(201).body(userResponse);
    }
}