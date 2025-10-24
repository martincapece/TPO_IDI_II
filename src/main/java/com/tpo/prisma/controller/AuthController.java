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
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse(e.getMessage(), null, null, null));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            AuthResponse response = authService.login(request);
            
            session.setAttribute("userId", response.getUserId());
            session.setAttribute("nombreUsuario", response.getNombreUsuario());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse(e.getMessage(), null, null, null));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        session.invalidate();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout exitoso");
        
        return ResponseEntity.ok(response);
    }
    
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

            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(404).body(error);
        }
    }
}
