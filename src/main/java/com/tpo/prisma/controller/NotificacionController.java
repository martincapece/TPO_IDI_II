package com.tpo.prisma.controller;

import com.tpo.prisma.model.Notificacion;
import com.tpo.prisma.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping("/feed")
    public ResponseEntity<List<Notificacion>> feed(@AuthenticationPrincipal String userId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size) {
        List<Notificacion> notis = notificacionService.feedParaUsuario(userId, page, size);
        return ResponseEntity.ok(notis);
    }

}
