package com.tpo.prisma.controller;

import com.tpo.prisma.model.modelNeo4j.ContenidoNode;
import com.tpo.prisma.service.GrafoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grafo")
public class GrafoController {

    @Autowired
    private GrafoService grafoService;

    @PostMapping("/sincronizar/usuario/{id}")
    public ResponseEntity<Map<String, String>> syncUsuario(@PathVariable String id) {
        grafoService.syncUsuario(id);
        return ResponseEntity.ok(Map.of("message", "Usuario sincronizado"));
    }

    @PostMapping("/sincronizar/contenido/{id}")
    public ResponseEntity<Map<String, String>> syncContenido(@PathVariable String id) {
        grafoService.syncContenido(id);
        return ResponseEntity.ok(Map.of("message", "Contenido sincronizado"));
    }

    @PostMapping("/sigue")
    public ResponseEntity<Map<String, String>> seguir(@AuthenticationPrincipal String userId, @RequestParam String destino) {
        grafoService.seguir(userId, destino);
        return ResponseEntity.ok(Map.of("message", "Relación SIGUE creada"));
    }

    @DeleteMapping("/sigue")
    public ResponseEntity<Map<String, String>> dejarDeSeguir(@AuthenticationPrincipal String userId, @RequestParam String destino) {
        grafoService.dejarDeSeguir(userId, destino);
        return ResponseEntity.ok(Map.of("message", "Relación SIGUE eliminada"));
    }

    @PostMapping("/interes")
    public ResponseEntity<Map<String, String>> interes(@RequestParam String usuarioId, @RequestParam String categoria, @RequestParam(defaultValue = "1") int score) {
        grafoService.interesadoEn(usuarioId, categoria, score);
        return ResponseEntity.ok(Map.of("message", "Interés registrado"));
    }

    @PatchMapping("/interes/ajustar")
    public ResponseEntity<Map<String, String>> ajustarInteres(@RequestParam String usuarioId, @RequestParam String categoria, @RequestParam int delta) {
        grafoService.ajustarInteres(usuarioId, categoria, delta);
        return ResponseEntity.ok(Map.of("message", "Interés ajustado"));
    }

    @DeleteMapping("/interes")
    public ResponseEntity<Map<String, String>> eliminarInteres(@RequestParam String usuarioId, @RequestParam String categoria) {
        grafoService.eliminarInteres(usuarioId, categoria);
        return ResponseEntity.ok(Map.of("message", "Interés eliminado"));
    }

    @PostMapping("/vio")
    public ResponseEntity<Map<String, String>> vio(@RequestParam String usuarioId, @RequestParam String contenidoId) {
        grafoService.vio(usuarioId, contenidoId);
        return ResponseEntity.ok(Map.of("message", "Vista registrada"));
    }

    @PostMapping("/me-gusto")
    public ResponseEntity<Map<String, String>> meGusto(@RequestParam String usuarioId, @RequestParam String contenidoId) {
        grafoService.meGusto(usuarioId, contenidoId);
        return ResponseEntity.ok(Map.of("message", "Me gusta registrado"));
    }

    @DeleteMapping("/me-gusto")
    public ResponseEntity<Map<String, String>> quitarMeGusto(@RequestParam String usuarioId, @RequestParam String contenidoId) {
        grafoService.quitarMeGusta(usuarioId, contenidoId);
        return ResponseEntity.ok(Map.of("message", "Me gusta eliminado"));
    }

    @PostMapping("/en-categoria")
    public ResponseEntity<Map<String, String>> enCategoria(@RequestParam String contenidoId, @RequestParam String categoria) {
        grafoService.enCategoria(contenidoId, categoria);
        return ResponseEntity.ok(Map.of("message", "Categoría asignada"));
    }

    @GetMapping("/recomendar/{usuarioId}")
    public ResponseEntity<Map<String, Object>> recomendar(@PathVariable String usuarioId, @RequestParam(defaultValue = "10") int limite) {
        List<ContenidoNode> lista = grafoService.recomendarPorIntereses(usuarioId, limite);
        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Recomendaciones generadas");
        resp.put("data", lista);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/historial/{usuarioId}")
    public ResponseEntity<List<String>> miHistorial(
        @AuthenticationPrincipal String userId,
        @RequestParam(defaultValue = "50") int limite) {
    return ResponseEntity.ok(grafoService.historialVistos(userId, limite));
    }

    @GetMapping("/seguidores/{usuarioId}")
    public ResponseEntity<List<String>> seguidores(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(grafoService.seguidores(userId));
    }

    @GetMapping("/recomendados")
    public ResponseEntity<Map<String, Object>> usuariosRecomendados(@AuthenticationPrincipal String userId) {
        List<String> recomendados = grafoService.usuariosRecomendados(userId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Usuarios recomendados obtenidos");
        resp.put("data", recomendados);
        return ResponseEntity.ok(resp);
    }

}
