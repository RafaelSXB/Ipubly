package com.project.Ipubly.Repository;

import com.project.Ipubly.Model.PostContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostContentRepository extends JpaRepository<PostContentEntity, UUID> {
}
