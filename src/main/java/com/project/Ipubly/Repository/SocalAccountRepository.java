package com.project.Ipubly.Repository;

import com.project.Ipubly.Model.SocialAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SocalAccountRepository extends JpaRepository<SocialAccountEntity, UUID> {
}
