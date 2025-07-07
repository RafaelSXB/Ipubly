package com.project.Ipubly.Repository;

import com.project.Ipubly.Model.SocialAccountEntity;
import com.project.Ipubly.Model.Enum.Provider;
import com.project.Ipubly.Model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccountEntity, UUID> {

    Optional<SocialAccountEntity> findByUserId_IdAndProvider(UUID userId, Provider provider);
}
