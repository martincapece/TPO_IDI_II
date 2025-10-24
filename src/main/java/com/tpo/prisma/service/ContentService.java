package com.tpo.prisma.service;

import com.tpo.prisma.model.Content;
import com.tpo.prisma.repository.ContentRepository;
import com.tpo.prisma.repository.UserRepository;
import com.tpo.prisma.service.GrafoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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


    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private GrafoService grafoService;

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private UserRepository userRepository;

    private static final String LIKED_CACHE_KEY = "content:liked";
    private static final String VIEWS_CACHE_KEY = "content:views";
    private static final String REGIONAL_RANKING_PREFIX = "content:regional:";
    private static final long CACHE_TTL = 10;

    public Content createContent(Content content) {
        // Auto-inicializar campos del servidor
        content.setPublishedAt(LocalDateTime.now());
        content.setUpdatedAt(LocalDateTime.now());
        content.setChatId(java.util.UUID.randomUUID().toString());
        content.setCantMeGusta(0);
        content.setDuracion(0);
        
        // Inicializar estadísticas regionales como mapa vacío
        if (content.getEstadisticasRegionales() == null) {
            content.setEstadisticasRegionales(new java.util.HashMap<>());
        }
        
        Content saved = contentRepository.save(content);

        try {
            grafoService.syncContenido(saved.getId());
            
            if (saved.getCategoria() != null && !saved.getCategoria().isEmpty()) {
                for (String categoria : saved.getCategoria()) {
                    if (categoria != null && !categoria.trim().isEmpty()) {
                        grafoService.enCategoria(saved.getId(), categoria.trim());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[Neo4j] Error sincronizando contenido: " + e.getMessage());
        }
        
        if (redisTemplate != null) {
            redisTemplate.delete(LIKED_CACHE_KEY);
            redisTemplate.delete(VIEWS_CACHE_KEY);
            if (content.getEstadisticasRegionales() != null && !content.getEstadisticasRegionales().isEmpty()) {
                redisTemplate.delete(REGIONAL_RANKING_PREFIX + content.getEstadisticasRegionales().keySet().iterator().next().toLowerCase());
            }
        }

        // Emitir notificación usando SIEMPRE el userId del creador
        notificacionService.emitirContenidoPublicado(saved.getCreatorId(), saved.getId());
        return saved;
    }

    public Optional<Content> getContentById(String id) {
        return contentRepository.findById(id);
    }

    public List<Content> getAllContents() {
        return contentRepository.findAll();
    }

    public List<Content> getContentsByCategoria(List<String> categorias) {
        return contentRepository.findByCategoria(categorias);
    }

    public List<Content> getContentsByCreator(String creatorId) {
        return contentRepository.findByCreatorId(creatorId);
    }

    public List<Content> getContentsByTipo(String tipo) {
        return contentRepository.findByTipo(tipo);
    }

    public List<Content> getPublicContents() {
        return contentRepository.findByVisibilidad("publico");
    }

    @SuppressWarnings("unchecked")
    public List<Content> getLikedContents() {
        if (redisTemplate != null) {
            List<Content> cached = (List<Content>) redisTemplate.opsForValue().get(LIKED_CACHE_KEY);
            if (cached != null && !cached.isEmpty()) {
                return cached;
            }
            
            List<Content> liked = contentRepository.findTopByOrderByCantMeGustaDesc();
            
            if (!liked.isEmpty()) {
                redisTemplate.opsForValue().set(LIKED_CACHE_KEY, liked, CACHE_TTL, TimeUnit.MINUTES);
            }
            
            return liked;
        }
        
        return contentRepository.findTopByOrderByCantMeGustaDesc();
    }

    @SuppressWarnings("unchecked")
    public List<Content> getViewsContents() {
        if (redisTemplate != null) {
            List<Content> cached = (List<Content>) redisTemplate.opsForValue().get(VIEWS_CACHE_KEY);
            if (cached != null && !cached.isEmpty()) {
                return cached;
            }

            List<Content> views = contentRepository.findTopByOrderByCantVistasDesc();
            if (!views.isEmpty()) {
                redisTemplate.opsForValue().set(VIEWS_CACHE_KEY, views, CACHE_TTL, TimeUnit.MINUTES);
            }
            
            return views;
        }
        
        return contentRepository.findTopByOrderByCantVistasDesc();
    }

    public void incrementLikes(String contentId, String usuarioId) {
        if (!grafoService.existeMeGusta(usuarioId, contentId)) {
            Optional<Content> contentOpt = contentRepository.findById(contentId);
            contentOpt.ifPresent(content -> {
                content.setCantMeGusta(content.getCantMeGusta() + 1);
                content.setUpdatedAt(LocalDateTime.now());
                contentRepository.save(content);
                
                if (redisTemplate != null) {
                    redisTemplate.delete(LIKED_CACHE_KEY);  
                }
            });
            grafoService.meGusto(usuarioId, contentId);
        } else {
            throw new RuntimeException("El usuario ya ha dado me gusta a este contenido");
        }
    }

    public void decrementLikes(String contentId, String usuarioId) {
        if (grafoService.existeMeGusta(usuarioId, contentId)) {
        Optional<Content> contentOpt = contentRepository.findById(contentId);
        contentOpt.ifPresent(content -> {
            content.setCantMeGusta(content.getCantMeGusta() - 1);
            content.setUpdatedAt(LocalDateTime.now());
            contentRepository.save(content);
            
            if (redisTemplate != null) {
                redisTemplate.delete(LIKED_CACHE_KEY);  
            }
            });
            grafoService.quitarMeGusta(usuarioId, contentId);
        } else {
            throw new RuntimeException("El usuario no ha dado me gusta a este contenido");
        }
    }

    @SuppressWarnings("unchecked")
    public List<Content> getTopContentsByRegion(String region) {
        String cacheKey = REGIONAL_RANKING_PREFIX + region.toLowerCase();
        
        if (redisTemplate != null) {
            List<Content> cached = (List<Content>) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null && !cached.isEmpty()) {
                return cached;
            }
        }
        
        Query query = new Query();
        String fieldPath = "estadisticasRegionales." + region;
        
        query.addCriteria(Criteria.where(fieldPath).exists(true).gte(1));
        
        query.with(Sort.by(Sort.Direction.DESC, fieldPath));
        
        query.limit(20);
        
        List<Content> topContents = mongoTemplate.find(query, Content.class);
        
        if (redisTemplate != null && !topContents.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, topContents, CACHE_TTL, TimeUnit.MINUTES);
        }
        
        return topContents;
    }

    public void updateRegionalStats(String contentId, String region, Integer views) {
        Optional<Content> contentOpt = contentRepository.findById(contentId);
        contentOpt.ifPresent(content -> {
            var stats = content.getEstadisticasRegionales();
            if (stats != null) {
                stats.put(region, stats.getOrDefault(region, 0) + views);
                content.setEstadisticasRegionales(stats);
                content.setUpdatedAt(LocalDateTime.now());
                contentRepository.save(content);
                
                if (redisTemplate != null) {
                    String cacheKey = REGIONAL_RANKING_PREFIX + region.toLowerCase();
                    redisTemplate.delete(cacheKey);
                }
            }
        });
    }

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
            
            return saved;
        });
    }

    public boolean deleteContent(String id) {
        if (contentRepository.existsById(id)) {
            contentRepository.deleteById(id);
            
            if (redisTemplate != null) {
                redisTemplate.delete(LIKED_CACHE_KEY);
                redisTemplate.delete(VIEWS_CACHE_KEY);
                redisTemplate.delete(REGIONAL_RANKING_PREFIX + contentRepository.findById(id).get().getEstadisticasRegionales().keySet().iterator().next().toLowerCase());
            }
            
            return true;
        }
        return false;
    }

    public void enterContent(String contentId, String usuarioId, String region) {

        var q = new Query(Criteria.where("_id").is(contentId));
        var u = new org.springframework.data.mongodb.core.query.Update()
                .inc("cantVistas", 1)
                .set("updatedAt", LocalDateTime.now());
    
        if (region != null && !region.isBlank()) {
            u.inc("estadisticasRegionales." + region.toLowerCase(), 1);
        }
    
        mongoTemplate.updateFirst(q, u, Content.class);
    
        grafoService.vio(usuarioId, contentId);
    
        if (redisTemplate != null) {
            redisTemplate.delete(VIEWS_CACHE_KEY);
            if (region != null && !region.isBlank()) {
                redisTemplate.delete(REGIONAL_RANKING_PREFIX + region.toLowerCase());
            }
        }
    }
}
