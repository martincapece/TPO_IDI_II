package com.tpo.prisma.controller;

import com.tpo.prisma.model.Streaming;
import com.tpo.prisma.service.StreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/streamings")
public class StreamingController {

    @Autowired
    private StreamingService streamingService;
    
    @PostMapping
    public ResponseEntity<Streaming> createStreaming(@RequestBody Streaming streaming) {
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
                .map(ResponseEntity::ok)
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
}