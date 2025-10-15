package com.tpo.prisma.service;

import com.tpo.prisma.model.Content;
import com.tpo.prisma.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY_PREFIX = "content:";
    private static final String POPULAR_CACHE_KEY = "content:popular";
    private static final long CACHE_TTL = 10; // 10 minutos

    // Crear contenido
    public Content createContent(Content content) {
        content.setPublishedAt(LocalDateTime.now());
        content.setUpdatedAt(LocalDateTime.now());
        Content saved = contentRepository.save(content);
        
        // Invalidar caché de contenidos populares
        if (redisTemplate != null) {
            redisTemplate.delete(POPULAR_CACHE_KEY);
        }
        
        return saved;
    }

    // Obtener contenido por ID (con caché)
    public Optional<Content> getContentById(String id) {
        if (redisTemplate != null) {
            String cacheKey = CACHE_KEY_PREFIX + id;
            
            // Intentar obtener de Redis
            Content cached = (Content) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                System.out.println("✅ Contenido obtenido desde REDIS (caché)");
                return Optional.of(cached);
            }
            
            // Si no está en caché, buscar en MongoDB
            Optional<Content> content = contentRepository.findById(id);
            
            // Si existe, guardarlo en Redis
            content.ifPresent(c -> {
                redisTemplate.opsForValue().set(cacheKey, c, CACHE_TTL, TimeUnit.MINUTES);
                System.out.println("✅ Contenido guardado en REDIS (caché)");
            });
            
            return content;
        }
        
        // Si Redis no está disponible, solo buscar en MongoDB
        return contentRepository.findById(id);
    }

    // Obtener todos los contenidos
    public List<Content> getAllContents() {
        return contentRepository.findAll();
    }

    // Obtener contenidos por categoría
    public List<Content> getContentsByCategoria(List<String> categorias) {
        return contentRepository.findByCategoria(categorias);
    }

    // Obtener contenidos por creador
    public List<Content> getContentsByCreator(String creatorId) {
        return contentRepository.findByCreatorId(creatorId);
    }

    // Obtener contenidos por tipo
    public List<Content> getContentsByTipo(String tipo) {
        return contentRepository.findByTipo(tipo);
    }

    // Obtener contenidos públicos
    public List<Content> getPublicContents() {
        return contentRepository.findByVisibilidad("publico");
    }

    // Obtener contenidos populares (con caché)
    @SuppressWarnings("unchecked")
    public List<Content> getPopularContents() {
        if (redisTemplate != null) {
            // Intentar obtener de Redis
            List<Content> cached = (List<Content>) redisTemplate.opsForValue().get(POPULAR_CACHE_KEY);
            if (cached != null && !cached.isEmpty()) {
                System.out.println("✅ Contenidos populares obtenidos desde REDIS (caché)");
                return cached;
            }
            
            // Si no está en caché, buscar en MongoDB
            List<Content> popular = contentRepository.findTopByOrderByCant_meGustaDesc();
            
            // Guardar en Redis
            if (!popular.isEmpty()) {
                redisTemplate.opsForValue().set(POPULAR_CACHE_KEY, popular, CACHE_TTL, TimeUnit.MINUTES);
                System.out.println("✅ Contenidos populares guardados en REDIS (caché)");
            }
            
            return popular;
        }
        
        // Si Redis no está disponible, solo buscar en MongoDB
        return contentRepository.findTopByOrderByCant_meGustaDesc();
    }

    // Incrementar "me gusta" (actualizar MongoDB e invalidar caché)
    public void incrementLikes(String contentId) {
        Optional<Content> contentOpt = contentRepository.findById(contentId);
        contentOpt.ifPresent(content -> {
            content.setCantMeGusta(content.getCantMeGusta() + 1);
            content.setUpdatedAt(LocalDateTime.now());
            contentRepository.save(content);
            
            // Invalidar caché
            if (redisTemplate != null) {
                redisTemplate.delete(CACHE_KEY_PREFIX + contentId);
                redisTemplate.delete(POPULAR_CACHE_KEY);
            }
        });
    }

    // Actualizar estadísticas regionales
    public void updateRegionalStats(String contentId, String region, Integer views) {
        Optional<Content> contentOpt = contentRepository.findById(contentId);
        contentOpt.ifPresent(content -> {
            // 🔧 CAMBIO: Corregir el nombre del método getter
            var stats = content.getEstadisticas_regionales(); // Antes: getEstadisitcas_regionales()
            if (stats != null) {
                stats.put(region, stats.getOrDefault(region, 0) + views);
                content.setEstadisticas_regionales(stats);
                content.setUpdatedAt(LocalDateTime.now());
                contentRepository.save(content);
                
                // Invalidar caché
                if (redisTemplate != null) {
                    redisTemplate.delete(CACHE_KEY_PREFIX + contentId);
                }
            }
        });
    }

    // Actualizar contenido
    public Optional<Content> updateContent(String id, Content updatedContent) {
        return contentRepository.findById(id).map(content -> {
            content.setTitulo(updatedContent.getTitulo());
            content.setTipo(updatedContent.getTipo());
            content.setLenguaje(updatedContent.getLenguaje());
            content.setCategoria(updatedContent.getCategoria());
            content.setVisibilidad(updatedContent.getVisibilidad());
            content.setDuracion(updatedContent.getDuracion());
            content.setUpdatedAt(LocalDateTime.now());
            
            Content saved = contentRepository.save(content);
            
            // Invalidar caché
            if (redisTemplate != null) {
                redisTemplate.delete(CACHE_KEY_PREFIX + id);
                redisTemplate.delete(POPULAR_CACHE_KEY);
            }
            
            return saved;
        });
    }

    // Eliminar contenido
    public boolean deleteContent(String id) {
        if (contentRepository.existsById(id)) {
            contentRepository.deleteById(id);
            
            // Invalidar caché
            if (redisTemplate != null) {
                redisTemplate.delete(CACHE_KEY_PREFIX + id);
                redisTemplate.delete(POPULAR_CACHE_KEY);
            }
            
            return true;
        }
        return false;
    }
}
