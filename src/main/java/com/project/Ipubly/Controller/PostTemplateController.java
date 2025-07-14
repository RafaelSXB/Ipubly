package com.project.Ipubly.Controller;

import com.project.Ipubly.Model.DTO.PostTemplateRequestDTO;
import com.project.Ipubly.Model.DTO.PostTemplateResponseDTO;
import com.project.Ipubly.Services.PostTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/postTemplates")
public class PostTemplateController {

    @Autowired
    private PostTemplateService postTemplateService;

    private UUID getCurrentUserId() {
        return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping
    public ResponseEntity<List<PostTemplateResponseDTO>> getAll() {
        return ResponseEntity.ok(postTemplateService.findAllByUser(getCurrentUserId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostTemplateResponseDTO> getById(@PathVariable UUID id) {
            return ResponseEntity.ok(postTemplateService.findById(id, getCurrentUserId()));
    }

    @PostMapping
    public ResponseEntity<PostTemplateResponseDTO> createTemplate(@RequestBody PostTemplateRequestDTO dto) {
            postTemplateService.validateRequestDTOTemplate(dto);
            return ResponseEntity.ok(postTemplateService.createTemplate(dto, getCurrentUserId()));
    }

    @PutMapping("{id}")
    public ResponseEntity<PostTemplateResponseDTO> updateTemplate(@PathVariable UUID id, @RequestBody PostTemplateRequestDTO dto) {
            postTemplateService.validateRequestDTOTemplate(dto);
            return ResponseEntity.ok(postTemplateService.updateTemplate(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTemplate(@PathVariable UUID id) {

            postTemplateService.deleteTemplate(id);
            return ResponseEntity.ok("Deleted successfully");

    }
}
