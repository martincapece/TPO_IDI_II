package com.tpo.prisma.service;

import com.tpo.prisma.dto.AuthResponse;
import com.tpo.prisma.dto.LoginRequest;
import com.tpo.prisma.dto.RegisterRequest;
import com.tpo.prisma.model.Usuario;
import com.tpo.prisma.repository.UserRepository;
import com.tpo.prisma.service.GrafoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GrafoService grafoService;
    
    public AuthResponse register(RegisterRequest request) {
        if (request.getMail() == null || !request.getMail().contains("@") || request.getMail().trim().isEmpty()) {
            throw new RuntimeException("El email debe ser válido y contener '@'");
        }
        if (request.getNombreUsuario() == null || request.getNombreUsuario().trim().isEmpty()) {
            throw new RuntimeException("El nombre de usuario no puede estar vacío");
        }
        if (request.getContrasena() == null || request.getContrasena().length() < 6) {
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        }
        if (request.getEdad() == null || request.getEdad() <= 0) {
            throw new RuntimeException("La edad debe ser un número positivo");
        }
        if (userRepository.existsByMail(request.getMail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (userRepository.existsByNombreUsuario(request.getNombreUsuario())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setMail(request.getMail());
        usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
        usuario.setEdad(request.getEdad());
        usuario.setIntereses(request.getIntereses());
        usuario.setDireccion(request.getDireccion());
        usuario.setCantVideos(0);
        usuario.setHistorial(new ArrayList<>());
        Usuario savedUser = userRepository.save(usuario);
        
        try {
            grafoService.syncUsuario(savedUser.getId());
            
            if (savedUser.getIntereses() != null && !savedUser.getIntereses().isEmpty()) {
                for (String interes : savedUser.getIntereses()) {
                    grafoService.interesadoEn(savedUser.getId(), interes, 1);
                }
            }
        } catch (Exception e) {
            System.err.println("[Neo4j] Error sincronizando usuario: " + e.getMessage());
        }
        
        return new AuthResponse(
            "Usuario registrado exitosamente",
            savedUser.getId(),
            savedUser.getNombreUsuario(),
            savedUser.getDireccion() != null ? savedUser.getDireccion().getPais() : null
        );
    }
    
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = userRepository.findByMail(request.getMail())
            .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
        
        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        
        return new AuthResponse(
            "Login exitoso",
            usuario.getId(),
            usuario.getNombreUsuario(),
            usuario.getDireccion() != null ? usuario.getDireccion().getPais() : null
        );
    }
    
    public Usuario getUserById(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
