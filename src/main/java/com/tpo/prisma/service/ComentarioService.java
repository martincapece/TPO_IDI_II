package com.tpo.prisma.service;

import com.tpo.prisma.model.Comentario;
import com.tpo.prisma.repository.ComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;
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
    private static final long CACHE_TTL = 5;

    public Comentario createComentario(Comentario comentario) {
        comentario.setCreatedAt(new Date());
        Comentario saved = comentarioRepository.save(comentario);
        
        if (redisTemplate != null) {
            redisTemplate.delete(CACHE_KEY_PREFIX + comentario.getContenidoId());
        }
        
        return saved;
    }

    public Optional<Comentario> getComentarioById(String id) {
        return comentarioRepository.findById(id);
    }

    public List<Comentario> getAllComentarios() {
        return comentarioRepository.findAll();
    }

    @SuppressWarnings("unchecked")
    public List<Comentario> getComentariosByContenido(String contenidoId) {
        String cacheKey = CACHE_KEY_PREFIX + contenidoId;
        
        if (redisTemplate != null) {
            List<Comentario> cached = (List<Comentario>) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null && !cached.isEmpty()) {
                return cached;
            }
        }
        
        List<Comentario> comentarios = comentarioRepository.findByContenidoIdOrderByCreatedAtDesc(contenidoId);
        
        if (redisTemplate != null && !comentarios.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, comentarios, CACHE_TTL, TimeUnit.MINUTES);
        }
        
        return comentarios;
    }

    public List<Comentario> getComentariosByUsuario(String usuarioId) {
        return comentarioRepository.findByUsuarioId(usuarioId);
    }

    public Optional<Comentario> updateComentario(String id, String nuevoMensaje) {
        return comentarioRepository.findById(id).map(comentario -> {
            comentario.setMensaje(nuevoMensaje);
            Comentario updated = comentarioRepository.save(comentario);
            
            if (redisTemplate != null) {
                redisTemplate.delete(CACHE_KEY_PREFIX + comentario.getContenidoId());
            }
            
            return updated;
        });
    }

    public boolean deleteComentario(String id) {
        Optional<Comentario> comentarioOpt = comentarioRepository.findById(id);
        if (comentarioOpt.isPresent()) {
            Comentario comentario = comentarioOpt.get();
            comentarioRepository.deleteById(id);
            
            if (redisTemplate != null) {
                redisTemplate.delete(CACHE_KEY_PREFIX + comentario.getContenidoId());
            }
            
            return true;
        }
        return false;
    }

    public long countComentariosByContenido(String contenidoId) {
        return comentarioRepository.findByContenidoId(contenidoId).size();
    }
}
