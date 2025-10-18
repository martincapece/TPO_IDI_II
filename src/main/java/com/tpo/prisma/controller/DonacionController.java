package com.tpo.prisma.controller;

import com.tpo.prisma.dto.DonacionRequest;
import com.tpo.prisma.dto.DonacionResponse;
import com.tpo.prisma.service.DonacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/donaciones")
public class DonacionController {

    @Autowired
    private DonacionService donacionService;

    @PostMapping
    public ResponseEntity<?> procesarDonacion(@RequestBody DonacionRequest request) {
        try {
            DonacionResponse response = donacionService.procesarDonacion(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error procesando donación: " + e.getMessage());
        }
    }

    @GetMapping("/stream/{streamId}")
    public ResponseEntity<List<DonacionResponse>> getDonacionesByStream(
            @PathVariable String streamId) {
        List<DonacionResponse> donaciones = donacionService.getDonacionesByStream(streamId);
        return ResponseEntity.ok(donaciones);
    }

    @GetMapping("/stream/{streamId}/total")
    public ResponseEntity<BigDecimal> getTotalDonaciones(
            @PathVariable String streamId) {
        BigDecimal total = donacionService.getTotalDonaciones(streamId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DonacionResponse>> getDonacionesByUsuario(
            @PathVariable String usuarioId) {
        List<DonacionResponse> donaciones = donacionService.getDonacionesByUsuario(usuarioId);
        return ResponseEntity.ok(donaciones);
    }
}