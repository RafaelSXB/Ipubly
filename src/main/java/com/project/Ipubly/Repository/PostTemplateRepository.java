package com.project.Ipubly.Repository;

import com.project.Ipubly.Model.PostTemplateEntity;
import org.apache.el.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostTemplateRepository extends JpaRepository<PostTemplateEntity, UUID> {

    List<PostTemplateEntity>  findAllByUserId(UUID userId);

    Optional<PostTemplateEntity> findByNameAndUserId(String name, UUID userId);
}
