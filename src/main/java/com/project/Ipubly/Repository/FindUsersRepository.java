package com.project.Ipubly.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public interface FindUsersRepository extends JpaRepository<usersEntity, UUID> {
    

    Optional<usersEntity> findByUsername(String username);
    
}
