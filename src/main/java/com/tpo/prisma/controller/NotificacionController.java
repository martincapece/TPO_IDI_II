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

    /**
     * Lista las notificaciones del usuario autenticado.
     */

}
