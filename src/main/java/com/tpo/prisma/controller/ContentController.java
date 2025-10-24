package com.tpo.prisma.controller;

import com.tpo.prisma.model.Content;
import com.tpo.prisma.service.ContentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/contents")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @PostMapping
    public ResponseEntity<Content> createContent(
            @RequestBody Content content, 
            HttpSession session) {
        
        String creatorId = (String) session.getAttribute("userId");
        content.setCreatorId(creatorId);
        
        Content created = contentService.createContent(content);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Content> getContentById(@PathVariable String id) {
        return contentService.getContentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Content>> getAllContents() {
        return ResponseEntity.ok(contentService.getAllContents());
    }

    @GetMapping("/categoria")
    public ResponseEntity<List<Content>> getContentsByCategoria(@RequestParam List<String> categorias) {
        return ResponseEntity.ok(contentService.getContentsByCategoria(categorias));
    }

    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<Content>> getContentsByCreator(@PathVariable String creatorId) {
        return ResponseEntity.ok(contentService.getContentsByCreator(creatorId));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Content>> getContentsByTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(contentService.getContentsByTipo(tipo));
    }

    @GetMapping("/publicos")
    public ResponseEntity<List<Content>> getPublicContents() {
        return ResponseEntity.ok(contentService.getPublicContents());
    }

    @GetMapping("/liked")
    public ResponseEntity<List<Content>> getLikedContents() {
        return ResponseEntity.ok(contentService.getLikedContents());
    }

    @GetMapping("/vistos")
    public ResponseEntity<List<Content>> getViewsContents(@RequestParam(required = false) String region) {
        if (region != null && !region.isEmpty()) {
            return ResponseEntity.ok(contentService.getTopContentsByRegion(region));
        }
        return ResponseEntity.ok(contentService.getViewsContents());
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> incrementLikes(@PathVariable String id, @RequestParam String usuarioId) {
        contentService.incrementLikes(id, usuarioId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<Void> decrementLikes(@PathVariable String id, @RequestParam String usuarioId) {
        contentService.decrementLikes(id, usuarioId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Void> incrementViews(@PathVariable String id, @RequestParam String region) {
        contentService.updateRegionalStats(id, region, 1);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/stats/{region}")
    public ResponseEntity<Void> updateRegionalStats(
            @PathVariable String id, 
            @PathVariable String region, 
            @RequestParam Integer views) {
        contentService.updateRegionalStats(id, region, views);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Content> updateContent(@PathVariable String id, @RequestBody Content content) {
        return contentService.updateContent(id, content)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable String id) {
        boolean deleted = contentService.deleteContent(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{id}/enter")
    public ResponseEntity<Void> enterContent(
            @PathVariable String id,
            @RequestParam(required = false) String region,
            @AuthenticationPrincipal String userId
    ) {

        contentService.enterContent(id, userId, region);
        return ResponseEntity.ok().build();
    }
}   

