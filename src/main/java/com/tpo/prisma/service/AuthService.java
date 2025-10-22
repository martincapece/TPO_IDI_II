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
    
    /**
     * Registrar un nuevo usuario
     */
    public AuthResponse register(RegisterRequest request) {
        // Validaciones básicas
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
        // Validar que el mail no esté registrado
        if (userRepository.existsByMail(request.getMail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        // Validar que el nombre de usuario no esté registrado
        if (userRepository.existsByNombreUsuario(request.getNombreUsuario())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }
        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setMail(request.getMail());
        usuario.setContrasena(passwordEncoder.encode(request.getContrasena())); // Encriptar contraseña
        usuario.setEdad(request.getEdad());
        usuario.setIntereses(request.getIntereses());
        usuario.setDireccion(request.getDireccion());
        usuario.setCantVideos(0);
        usuario.setHistorial(new ArrayList<>());
        // Guardar en MongoDB
        Usuario savedUser = userRepository.save(usuario);
        
        // Sincronizar automáticamente con Neo4j
        try {
            // 1. Crear nodo Usuario en Neo4j
            grafoService.syncUsuario(savedUser.getId());
            
            // 2. Crear relaciones INTERESADO_EN para cada interés
            if (savedUser.getIntereses() != null && !savedUser.getIntereses().isEmpty()) {
                for (String interes : savedUser.getIntereses()) {
                    // Score inicial de 1 para cada interés declarado
                    grafoService.interesadoEn(savedUser.getId(), interes, 1);
                }
            }
        } catch (Exception e) {
            System.err.println("[Neo4j] Error sincronizando usuario: " + e.getMessage());
            // No fallar el registro si falla Neo4j
        }
        
        return new AuthResponse(
            "Usuario registrado exitosamente",
            savedUser.getId(),
            savedUser.getNombreUsuario()
        );
    }
    
    /**
     * Iniciar sesión
     */
    public AuthResponse login(LoginRequest request) {
        // Buscar usuario por mail
        Usuario usuario = userRepository.findByMail(request.getMail())
            .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
        
        // Verificar contraseña
        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        
        return new AuthResponse(
            "Login exitoso",
            usuario.getId(),
            usuario.getNombreUsuario()
        );
    }
    
    /**
     * Obtener usuario por ID
     */
    public Usuario getUserById(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
