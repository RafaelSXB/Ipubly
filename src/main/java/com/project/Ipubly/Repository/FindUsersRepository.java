package com.project.Ipubly.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

import com.project.Ipubly.Model.usersEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;

@Repository
public interface FindUsersRepository extends JpaRepository<usersEntity, UUID> {
    

    Optional<usersEntity> findByUsername(String username);
    
}
