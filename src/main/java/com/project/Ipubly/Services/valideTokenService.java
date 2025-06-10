package com.project.Ipubly.Services;
import com.project.Ipubly.Model.UserEntity;
import com.project.Ipubly.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class valideTokenService {
  /*  @Autowired
    private UsersRepository usersRepository;

    public boolean validateToken(String Username) {
        Optional<UserEntity> users = usersRepository.findByUsername(Username);
       return users.map(user -> users.get().getEXPIRE() != null && users.get().getEXPIRE() > System.currentTimeMillis()).orElse(false);
          
    } */
}
