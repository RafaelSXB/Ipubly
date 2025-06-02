package com.project.Ipubly.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.Ipubly.Repository.FindUsersRepository;

import java.util.Optional;

@Service
public class valideTokenService {
    @Autowired
    private FindUsersRepository FindUsersRepository;

    public boolean validateToken(String Username) {
        Optional<usersEntity> users = FindUsersRepository.findByUsername(Username);
       return users.map(user -> users.get().getEXPIRE() != null && users.get().getEXPIRE() > System.currentTimeMillis()).orElse(false);
          
    }
}
