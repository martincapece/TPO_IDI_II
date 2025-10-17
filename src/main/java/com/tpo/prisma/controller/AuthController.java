package com.tpo.prisma.controller;

import com.tpo.prisma.dto.AuthResponse;
import com.tpo.prisma.dto.LoginRequest;
import com.tpo.prisma.dto.RegisterRequest;
import com.tpo.prisma.model.Usuario;
import com.tpo.prisma.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * POST /api/auth/register
     * Registrar un nuevo usuario
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse(e.getMessage(), null, null));
        }
    }
    
    /**
     * POST /api/auth/login
     * Iniciar sesión y crear sesión en Redis
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            AuthResponse response = authService.login(request);
            
            // Guardar información del usuario en la sesión (se almacenará en Redis)
            session.setAttribute("userId", response.getUserId());
            session.setAttribute("nombreUsuario", response.getNombreUsuario());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse(e.getMessage(), null, null));
        }
    }
    
    /**
     * POST /api/auth/logout
     * Cerrar sesión e invalidar la sesión en Redis
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        session.invalidate(); // Invalida la sesión (se borra de Redis)
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout exitoso");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/auth/me
     * Obtener información del usuario autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        
        if (userId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "No hay sesión activa");
            return ResponseEntity.status(401).body(error);
        }
        
        try {
            Usuario usuario = authService.getUserById(userId);
            
            // No devolver la contraseña
            usuario.setContrasena(null);
            
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(404).body(error);
        }
    }
}
