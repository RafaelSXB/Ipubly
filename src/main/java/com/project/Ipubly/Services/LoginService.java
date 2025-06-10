package com.project.Ipubly.Services;

import com.project.Ipubly.Model.DTO.UserRequestDTO;
import com.project.Ipubly.Model.DTO.UserResponseDTO;
import com.project.Ipubly.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
   /* @Autowired
    private JwtService jwtService;

    @Autowired
    private UsersRepository usersRepository;

    public Login loginAuth(UserRequestDTO userRequestDTO) {

       usersRepository.findByUsernameAndPassword(String userRequestDTO.getUsername(), String verifyPassword(userRequestDTO.getPassword()))
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        UserResponseDTO userResponse = new UserResponseDTO();
        userResponse.setUsername(userRequestDTO.getUsername());
        userResponse.setToken("dummy-token");
        userResponse.setMessage("Login successful");

        return userResponse;

    }

    public verifyPassword(String password) {
        // Implement your password verification logic here
        // For example, you might want to hash the password and compare it with the stored hash
        return password; // Placeholder, replace with actual verification logic
    }
*/
}