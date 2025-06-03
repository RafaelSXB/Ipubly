package com.project.Ipubly.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "post_contents")
@Data
@NoArgsConstructor
public class PostContentEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_template_id")
    private PostTemplateEntity postTemplate;

    @Column(name = "generated_text", nullable = false)
    private String generatedText;

    @Lob
    @Column(name = "generated_image")
    private byte[] generatedImage;

    @Column(name = "image_mime_type", length = 50)
    private String imageMimeType;

    @Column(name = "ai_model_used", length = 100)
    private String aiModelUsed;

    @Column(name = "generation_status", nullable = false, length = 50)
    private String generationStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        if (this.generationStatus == null || this.generationStatus.isEmpty()) {
            this.generationStatus = "completed"; // Set default value
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}