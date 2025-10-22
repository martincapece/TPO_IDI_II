package com.tpo.prisma.controller;

import com.tpo.prisma.dto.ChatMessage;
import com.tpo.prisma.dto.CreateChatMessageRequest;
import com.tpo.prisma.model.Streaming;
import com.tpo.prisma.service.StreamingChatService;
import com.tpo.prisma.service.StreamingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/streamings")
public class StreamingController {

    @Autowired
    private StreamingService streamingService;

    @Autowired
    private StreamingChatService streamingChatService;
    
    @PostMapping
    public ResponseEntity<Streaming> createStreaming(
            @RequestBody Streaming streaming, 
            HttpSession session) {
        
        // Obtener el creatorId de la sesión
        String creatorId = (String) session.getAttribute("userId");
        streaming.setCreatorId(creatorId);
        
        Streaming created = streamingService.createStreaming(streaming);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Streaming> getStreamingById(@PathVariable String id) {
        return streamingService.getStreamingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Streaming>> getAllStreamings() {
        return ResponseEntity.ok(streamingService.getAllStreamings());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Streaming> updateStreaming(
            @PathVariable String id, 
            @RequestBody Streaming streaming) {
        return streamingService.updateStreaming(id, streaming)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/finalizar")
    public ResponseEntity<Streaming> finalizarStreaming(@PathVariable String id) {
        return streamingService.finalizarStreaming(id)
                .map(streaming -> {
                    // Configurar expiración del chat: 5 minutos después de finalizar
                    // Solo si el streaming tiene chatId (streamings antiguos no lo tienen)
                    if (streaming.getChatId() != null && !streaming.getChatId().isEmpty()) {
                        streamingChatService.setExpirationOnStreamEnd(streaming.getChatId());
                    }
                    return ResponseEntity.ok(streaming);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/espectadores")
    public ResponseEntity<Void> actualizarEspectadores(
            @PathVariable String id, 
            @RequestParam Integer cantidad) {
        streamingService.actualizarEspectadores(id, cantidad);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/donacion")
    public ResponseEntity<Void> registrarDonacion(@PathVariable String id) {
        streamingService.incrementarDonaciones(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/promedio")
    public ResponseEntity<Void> actualizarPromedio(
            @PathVariable String id, 
            @RequestParam Integer promedio) {
        streamingService.actualizarPromedio(id, promedio);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/live")
    public ResponseEntity<List<Streaming>> getStreamingsEnVivo() {
        return ResponseEntity.ok(streamingService.getStreamingsEnVivo());
    }

    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<Streaming>> getStreamingsByCreator(@PathVariable String creatorId) {
        return ResponseEntity.ok(streamingService.getStreamingsByCreator(creatorId));
    }

    @GetMapping("/live/categoria")
    public ResponseEntity<List<Streaming>> getStreamingsEnVivoPorCategoria(
            @RequestParam List<String> categorias) {
        return ResponseEntity.ok(streamingService.getStreamingsEnVivoPorCategoria(categorias));
    }

    @GetMapping("/region/{region}")
    public ResponseEntity<List<Streaming>> getStreamingsenVivoByRegion(@PathVariable String region) {
        return ResponseEntity.ok(streamingService.getStreamingsenVivoByRegion(region));
    }
    
    @GetMapping("/top/live")
    public ResponseEntity<List<Streaming>> getTopLiveByEspectadores() {
        return ResponseEntity.ok(streamingService.getTopLiveByEspectadores());
    }

    @GetMapping("/top/region/{region}")
    public ResponseEntity<List<Streaming>> getTopStreamingsByRegion(@PathVariable String region) {
        return ResponseEntity.ok(streamingService.getTopStreamingsByRegion(region));
    }
    
    @PostMapping("/{id}/stats/{region}")
    public ResponseEntity<Void> updateRegionalStats(
            @PathVariable String id, 
            @PathVariable String region, 
            @RequestParam Integer espectadores) {
        streamingService.updateRegionalStats(id, region, espectadores);
        return ResponseEntity.ok().build();
    }

    // ==================== CHAT ENDPOINTS (REDIS) ====================
    
    @PostMapping("/{streamId}/chat")
    public ResponseEntity<ChatMessage> sendStreamingChatMessage(
            @PathVariable String streamId,
            @RequestBody CreateChatMessageRequest request,
            HttpSession session) {
        
        Streaming streaming = streamingService.getStreamingById(streamId)
                .orElseThrow(() -> new RuntimeException("Streaming no encontrado"));
        
        String userId = (String) session.getAttribute("userId");
        String username = (String) session.getAttribute("nombreUsuario");
        
        ChatMessage message = streamingChatService.sendMessage(
                streaming.getChatId(), 
                streamId, 
                userId, 
                username, 
                request.getText()
        );
        
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{streamId}/chat")
    public ResponseEntity<List<ChatMessage>> getStreamingChatMessages(
            @PathVariable String streamId,
            @RequestParam(defaultValue = "50") int limit) {
        
        Streaming streaming = streamingService.getStreamingById(streamId)
                .orElseThrow(() -> new RuntimeException("Streaming no encontrado"));
        
        List<ChatMessage> messages = streamingChatService.getRecentMessages(streaming.getChatId(), limit);
        
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/{streamId}/chat")
    public ResponseEntity<Map<String, String>> clearStreamingChat(@PathVariable String streamId) {
        
        Streaming streaming = streamingService.getStreamingById(streamId)
                .orElseThrow(() -> new RuntimeException("Streaming no encontrado"));
        
        streamingChatService.clearChat(streaming.getChatId());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Chat de Streaming limpiado correctamente");
        response.put("storage", "Redis");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{streamId}/chat/ttl")
    public ResponseEntity<Map<String, Object>> getChatTTL(@PathVariable String streamId) {
        
        Streaming streaming = streamingService.getStreamingById(streamId)
                .orElseThrow(() -> new RuntimeException("Streaming no encontrado"));
        
        Long ttlSeconds = streamingChatService.getChatTTL(streaming.getChatId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("chatId", streaming.getChatId());
        response.put("streamId", streamId);
        
        if (ttlSeconds == null || ttlSeconds == -2) {
            response.put("status", "Chat no existe");
            response.put("ttl", null);
        } else if (ttlSeconds == -1) {
            response.put("status", "Sin expiración (streaming activo)");
            response.put("ttl", -1);
        } else {
            response.put("status", "Chat expirará en:");
            response.put("ttlSeconds", ttlSeconds);
            response.put("ttlMinutes", Math.round(ttlSeconds / 60.0 * 100.0) / 100.0);
            response.put("expirationConfigured", "5 minutos después de finalizar");
        }
        
        return ResponseEntity.ok(response);
    }
}