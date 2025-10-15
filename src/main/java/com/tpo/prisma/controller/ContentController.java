package com.tpo.prisma.controller;

import com.tpo.prisma.model.Content;
import com.tpo.prisma.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contents")
public class ContentController {

    @Autowired
    private ContentService contentService;

    // Crear contenido
    @PostMapping
    public ResponseEntity<Content> createContent(@RequestBody Content content) {
        Content created = contentService.createContent(content);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Obtener contenido por ID (con caché Redis)
    @GetMapping("/{id}")
    public ResponseEntity<Content> getContentById(@PathVariable String id) {
        return contentService.getContentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener todos los contenidos
    @GetMapping
    public ResponseEntity<List<Content>> getAllContents() {
        return ResponseEntity.ok(contentService.getAllContents());
    }

    // Obtener contenidos por categoría
    @GetMapping("/categoria")
    public ResponseEntity<List<Content>> getContentsByCategoria(@RequestParam List<String> categorias) {
        return ResponseEntity.ok(contentService.getContentsByCategoria(categorias));
    }

    // Obtener contenidos por creador
    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<Content>> getContentsByCreator(@PathVariable String creatorId) {
        return ResponseEntity.ok(contentService.getContentsByCreator(creatorId));
    }

    // Obtener contenidos por tipo
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Content>> getContentsByTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(contentService.getContentsByTipo(tipo));
    }

    // Obtener contenidos públicos
    @GetMapping("/publicos")
    public ResponseEntity<List<Content>> getPublicContents() {
        return ResponseEntity.ok(contentService.getPublicContents());
    }

    // Obtener contenidos populares (desde caché Redis)
    @GetMapping("/populares")
    public ResponseEntity<List<Content>> getPopularContents() {
        return ResponseEntity.ok(contentService.getPopularContents());
    }

    // Incrementar "me gusta"
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> incrementLikes(@PathVariable String id) {
        contentService.incrementLikes(id);
        return ResponseEntity.ok().build();
    }

    // Actualizar estadísticas regionales
    @PostMapping("/{id}/stats/{region}")
    public ResponseEntity<Void> updateRegionalStats(
            @PathVariable String id, 
            @PathVariable String region, 
            @RequestParam Integer views) {
        contentService.updateRegionalStats(id, region, views);
        return ResponseEntity.ok().build();
    }

    // Actualizar contenido
    @PutMapping("/{id}")
    public ResponseEntity<Content> updateContent(@PathVariable String id, @RequestBody Content content) {
        return contentService.updateContent(id, content)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar contenido
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable String id) {
        boolean deleted = contentService.deleteContent(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

