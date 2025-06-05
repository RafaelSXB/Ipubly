package com.project.Ipubly.Repository;

import com.project.Ipubly.Model.PostTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostTemplateRepository extends JpaRepository<PostTemplateEntity, UUID> {
}
