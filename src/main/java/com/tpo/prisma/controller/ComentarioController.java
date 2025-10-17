package com.tpo.prisma.controller;

import com.tpo.prisma.model.Comentario;
import com.tpo.prisma.service.ComentarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    // Crear comentario
    @PostMapping
    public ResponseEntity<Comentario> createComentario(@RequestBody Comentario comentario) {
        Comentario created = comentarioService.createComentario(comentario);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Obtener comentario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Comentario> getComentarioById(@PathVariable String id) {
        return comentarioService.getComentarioById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener todos los comentarios
    @GetMapping
    public ResponseEntity<List<Comentario>> getAllComentarios() {
        return ResponseEntity.ok(comentarioService.getAllComentarios());
    }

    // Obtener comentarios por contenido (con caché Redis)
    @GetMapping("/contenido/{contenidoId}")
    public ResponseEntity<List<Comentario>> getComentariosByContenido(@PathVariable String contenidoId) {
        return ResponseEntity.ok(comentarioService.getComentariosByContenido(contenidoId));
    }

    // Obtener comentarios por usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Comentario>> getComentariosByUsuario(@PathVariable String usuarioId) {
        return ResponseEntity.ok(comentarioService.getComentariosByUsuario(usuarioId));
    }

    // Contar comentarios de un contenido
    @GetMapping("/contenido/{contenidoId}/count")
    public ResponseEntity<Map<String, Long>> countComentarios(@PathVariable String contenidoId) {
        long count = comentarioService.countComentariosByContenido(contenidoId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // Actualizar comentario
    @PutMapping("/{id}")
    public ResponseEntity<Comentario> updateComentario(
            @PathVariable String id, 
            @RequestBody Map<String, String> body) {
        String nuevoMensaje = body.get("mensaje");
        return comentarioService.updateComentario(id, nuevoMensaje)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar comentario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComentario(@PathVariable String id) {
        boolean deleted = comentarioService.deleteComentario(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
