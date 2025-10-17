package com.tpo.prisma.service;

import com.tpo.prisma.model.Comentario;
import com.tpo.prisma.repository.ComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Date; // ✅ Cambiar import
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY_PREFIX = "comentarios:contenido:";
    private static final long CACHE_TTL = 5; // 5 minutos

    // Crear comentario
    public Comentario createComentario(Comentario comentario) {
        comentario.setCreatedAt(new Date()); // ✅ Cambiar a new Date()
        Comentario saved = comentarioRepository.save(comentario);
        
        // Invalidar caché de comentarios del contenido
        if (redisTemplate != null) {
            redisTemplate.delete(CACHE_KEY_PREFIX + comentario.getContenidoId());
        }
        
        return saved;
    }

    // Obtener comentario por ID
    public Optional<Comentario> getComentarioById(String id) {
        return comentarioRepository.findById(id);
    }

    // Obtener todos los comentarios
    public List<Comentario> getAllComentarios() {
        return comentarioRepository.findAll();
    }

    // Obtener comentarios por contenido (con caché)
    @SuppressWarnings("unchecked")
    public List<Comentario> getComentariosByContenido(String contenidoId) {
        String cacheKey = CACHE_KEY_PREFIX + contenidoId;
        
        // Intentar obtener de Redis
        if (redisTemplate != null) {
            List<Comentario> cached = (List<Comentario>) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null && !cached.isEmpty()) {
                System.out.println("✅ Comentarios obtenidos desde REDIS (caché)");
                return cached;
            }
        }
        
        // Si no está en caché, buscar en MongoDB
        List<Comentario> comentarios = comentarioRepository.findByContenidoIdOrderByCreatedAtDesc(contenidoId);
        
        // Guardar en Redis
        if (redisTemplate != null && !comentarios.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, comentarios, CACHE_TTL, TimeUnit.MINUTES);
            System.out.println("✅ Comentarios guardados en REDIS (caché)");
        }
        
        return comentarios;
    }

    // Obtener comentarios por usuario
    public List<Comentario> getComentariosByUsuario(String usuarioId) {
        return comentarioRepository.findByUsuarioId(usuarioId);
    }

    // Actualizar comentario
    public Optional<Comentario> updateComentario(String id, String nuevoMensaje) {
        return comentarioRepository.findById(id).map(comentario -> {
            comentario.setMensaje(nuevoMensaje);
            Comentario updated = comentarioRepository.save(comentario);
            
            // Invalidar caché
            if (redisTemplate != null) {
                redisTemplate.delete(CACHE_KEY_PREFIX + comentario.getContenidoId());
            }
            
            return updated;
        });
    }

    // Eliminar comentario
    public boolean deleteComentario(String id) {
        Optional<Comentario> comentarioOpt = comentarioRepository.findById(id);
        if (comentarioOpt.isPresent()) {
            Comentario comentario = comentarioOpt.get();
            comentarioRepository.deleteById(id);
            
            // Invalidar caché
            if (redisTemplate != null) {
                redisTemplate.delete(CACHE_KEY_PREFIX + comentario.getContenidoId());
            }
            
            return true;
        }
        return false;
    }

    // Contar comentarios de un contenido
    public long countComentariosByContenido(String contenidoId) {
        return comentarioRepository.findByContenidoId(contenidoId).size();
    }
}
