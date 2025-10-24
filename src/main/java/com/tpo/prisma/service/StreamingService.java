package com.tpo.prisma.service;

import com.tpo.prisma.model.Streaming;
import com.tpo.prisma.repository.StreamingRepository;

import org.springframework.beans.factory.annotation.Autowired;
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
public class StreamingService {
    
    @Autowired
    private StreamingRepository streamingRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DonacionService donacionService;

    @Autowired
    private NotificacionService notificacionService;
    
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private RedisTemplate<String, String> redisStr;
    
    private static final String LIVE_CACHE_KEY = "streaming:live";
    private static final String TOP_VIEWERS_CACHE_KEY = "streaming:top:viewers";
    private static final String REGIONAL_RANKING_PREFIX = "streaming:regional:";
    private static final long CACHE_TTL = 1;
    
    private String viewersKey(String id) { return "streaming:%s:viewers".formatted(id); }

    public Streaming createStreaming(Streaming streaming) {
        streaming.setHoraComienzo(LocalDateTime.now());
        streaming.setEnVivo(true);
        streaming.setChatId(java.util.UUID.randomUUID().toString());
        
        streaming.setEstadisticasVivo(new Streaming.EstadisticasVivo());
        
        if (streaming.getEstadisticasRegionales() == null) {
            streaming.setEstadisticasRegionales(new java.util.HashMap<>());
        }
        
        Streaming saved = streamingRepository.save(streaming);

        donacionService.sincronizarStreaming(saved.getId(), saved.getCreatorId());
        
        if (redisTemplate != null) {
            redisTemplate.delete(LIVE_CACHE_KEY);
            if (streaming.getRegion() != null) {
                redisTemplate.delete(REGIONAL_RANKING_PREFIX + streaming.getRegion().toLowerCase());
            }
        }

        notificacionService.emitirStreamIniciado(saved.getCreatorId(), saved.getId());
        return saved;
    }
    
    public Optional<Streaming> getStreamingById(String id) {
        return streamingRepository.findById(id);
    }
    
    public List<Streaming> getAllStreamings() {
        return streamingRepository.findAll();
    }
    
    public Optional<Streaming> updateStreaming(String id, Streaming updatedStreaming) {
        return streamingRepository.findById(id).map(streaming -> {
            streaming.setTitulo(updatedStreaming.getTitulo());
            streaming.setLenguaje(updatedStreaming.getLenguaje());
            streaming.setCategoria(updatedStreaming.getCategoria());
            streaming.setRegion(updatedStreaming.getRegion());
            
            Streaming saved = streamingRepository.save(streaming);
            
            if (redisTemplate != null) {
                redisTemplate.delete(LIVE_CACHE_KEY);
            }
            
            return saved;
        });
    }
    
    public boolean deleteStreaming(String id) {
        if (streamingRepository.existsById(id)) {
            streamingRepository.deleteById(id);
            
            if (redisTemplate != null) {
                redisTemplate.delete(LIVE_CACHE_KEY);
                redisTemplate.delete(TOP_VIEWERS_CACHE_KEY);
            }
            
            return true;
        }
        return false;
    }
    
    
    public Optional<Streaming> finalizarStreaming(String id, String userId) {
        return streamingRepository.findById(id).map(streaming -> {
            if (redisStr != null) {
                    if (streaming.getCreatorId().equals(userId)) {
                        Long last = redisStr.opsForSet().size(viewersKey(id));
                        if (last != null) {
                            var stats = streaming.getEstadisticasVivo();
                            long newSum = stats.getSumMuestras() + last;
                            long newCnt = stats.getCantMuestras() + 1;
                            stats.setSumMuestras((int) newSum);
                            stats.setCantMuestras((int) newCnt);
                            stats.setPromedioEspectadores((int)Math.round(newSum / (double)newCnt));
                            stats.setEspectadores(last.intValue());
                            streaming.setEstadisticasVivo(stats);
                        }
                        redisStr.delete(viewersKey(id));
                    }else{
                        return null;
                    }
            }
            streaming.setEnVivo(false);
            streaming.setHoraFinalizado(LocalDateTime.now());
            Streaming saved = streamingRepository.save(streaming);
    
            if (redisTemplate != null) {
                redisTemplate.delete(LIVE_CACHE_KEY);
                redisTemplate.delete(TOP_VIEWERS_CACHE_KEY);
                if (streaming.getRegion() != null) {
                    redisTemplate.delete(REGIONAL_RANKING_PREFIX + streaming.getRegion().toLowerCase());
                }
            }
            donacionService.finalizarStreaming(id);
            return saved;
        });
    }
    
    public void actualizarEspectadores(String streamingId, Integer espectadores) {
        Optional<Streaming> streamingOpt = streamingRepository.findById(streamingId);
        streamingOpt.ifPresent(streaming -> {
            if (streaming.getEnVivo()) {
                Streaming.EstadisticasVivo stats = streaming.getEstadisticasVivo();
                stats.setEspectadores(espectadores);
                stats.actualizarPicoSiNecesario(espectadores);
                
                streaming.setEstadisticasVivo(stats);
                streamingRepository.save(streaming);
                
                if (redisTemplate != null) {
                    redisTemplate.delete(LIVE_CACHE_KEY);
                    redisTemplate.delete(TOP_VIEWERS_CACHE_KEY);
                }
            }
        });
    }
    
    public void incrementarDonaciones(String streamingId) {
        Optional<Streaming> streamingOpt = streamingRepository.findById(streamingId);
        streamingOpt.ifPresent(streaming -> {
            Streaming.EstadisticasVivo stats = streaming.getEstadisticasVivo();
            stats.setCantDonaciones(stats.getCantDonaciones() + 1);
            streaming.setEstadisticasVivo(stats);
            streamingRepository.save(streaming);
        });
    }
    
    public void actualizarPromedio(String streamingId, Integer promedio) {
        Optional<Streaming> streamingOpt = streamingRepository.findById(streamingId);
        streamingOpt.ifPresent(streaming -> {
            Streaming.EstadisticasVivo stats = streaming.getEstadisticasVivo();
            stats.setPromedioEspectadores(promedio);
            streaming.setEstadisticasVivo(stats);
            streamingRepository.save(streaming);
        });
    }

    public void joinStreaming(String streamingId, String userId, String region) {
        var opt = streamingRepository.findById(streamingId);
        opt.ifPresent(streaming -> {
            if (!Boolean.TRUE.equals(streaming.getEnVivo())) return;
    
            Long current = 0L;
    
            if (redisStr != null) {
                redisStr.opsForSet().add(viewersKey(streamingId), userId);
                current = redisStr.opsForSet().size(viewersKey(streamingId));
            } else {
                current = (long) (streaming.getEstadisticasVivo().getEspectadores() + 1);
            }
    
            int cur = current.intValue();   
            mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").is(streamingId).and("enVivo").is(true)),
                new org.springframework.data.mongodb.core.query.Update()
                    .set("estadisticasVivo.espectadores", cur)
                    .max("estadisticasVivo.picoEspectadores", cur)
                    .inc("estadisticasVivo.sumMuestras", cur)
                    .inc("estadisticasVivo.cantMuestras", 1)
                    .set("estadisticasVivo.promedioEspectadores",
                         Math.toIntExact(Math.round(
                           (streaming.getEstadisticasVivo().getSumMuestras() + cur) /
                           (double) (streaming.getEstadisticasVivo().getCantMuestras() + 1)
                         )))
                , Streaming.class
            );
    
            if (region != null && !region.isBlank()) {
                mongoTemplate.updateFirst(
                    Query.query(Criteria.where("_id").is(streamingId).and("enVivo").is(true)),
                    new org.springframework.data.mongodb.core.query.Update()
                        .inc("estadisticasRegionales." + region.toLowerCase(), 1),
                    Streaming.class
                );
            }
    
            if (redisTemplate != null) {
                redisTemplate.delete(LIVE_CACHE_KEY);
                redisTemplate.delete(TOP_VIEWERS_CACHE_KEY);
                if (streaming.getRegion() != null) {
                    redisTemplate.delete(REGIONAL_RANKING_PREFIX + streaming.getRegion().toLowerCase());
                }
            }
        });
    }

    public void leaveStreaming(String streamingId, String userId) {
        var opt = streamingRepository.findById(streamingId);
        opt.ifPresent(streaming -> {
            if (!Boolean.TRUE.equals(streaming.getEnVivo())) return;

            Long current = 0L;
            if (redisStr != null) {
                redisStr.opsForSet().remove(viewersKey(streamingId), userId);
                current = redisStr.opsForSet().size(viewersKey(streamingId));
            } else {
                current = (long) Math.max(0, streaming.getEstadisticasVivo().getEspectadores() - 1);
            }

            int cur = current.intValue();
            mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").is(streamingId).and("enVivo").is(true)),
                new org.springframework.data.mongodb.core.query.Update()
                    .set("estadisticasVivo.espectadores", cur)
                    .inc("estadisticasVivo.sumMuestras", cur)
                    .inc("estadisticasVivo.cantMuestras", 1)
                    .set("estadisticasVivo.promedioEspectadores",
                        Math.toIntExact(Math.round(
                        (streaming.getEstadisticasVivo().getSumMuestras() + cur) /
                        (double) (streaming.getEstadisticasVivo().getCantMuestras() + 1)
                        )))
                , Streaming.class
            );

            if (redisTemplate != null) {
                redisTemplate.delete(LIVE_CACHE_KEY);
                redisTemplate.delete(TOP_VIEWERS_CACHE_KEY);
                if (streaming.getRegion() != null) {
                    redisTemplate.delete(REGIONAL_RANKING_PREFIX + streaming.getRegion().toLowerCase());
                }
            }
        });
    }
    
    
    @SuppressWarnings("unchecked")
    public List<Streaming> getStreamingsEnVivo() {
        if (redisTemplate != null) {
            List<Streaming> cached = (List<Streaming>) redisTemplate.opsForValue().get(LIVE_CACHE_KEY);
            if (cached != null && !cached.isEmpty()) {
                return cached;
            }
            
            List<Streaming> live = streamingRepository.findByEnVivo();
            
            if (!live.isEmpty()) {
                redisTemplate.opsForValue().set(LIVE_CACHE_KEY, live, CACHE_TTL, TimeUnit.MINUTES);
            }
            
            return live;
        }
        
        return streamingRepository.findByEnVivo();
    }
    
    public List<Streaming> getStreamingsByCreator(String creatorId) {
        return streamingRepository.findByCreatorId(creatorId);
    }
    
    public List<Streaming> getStreamingsEnVivoPorCategoria(List<String> categorias) {
        return streamingRepository.findLiveByCategorias(categorias);
    }
    
    public List<Streaming> getStreamingsenVivoByRegion(String region) {
        return streamingRepository.findLiveByRegion(region);
    }
    
    
    @SuppressWarnings("unchecked")
    public List<Streaming> getTopLiveByEspectadores() {
        if (redisTemplate != null) {
            List<Streaming> cached = (List<Streaming>) redisTemplate.opsForValue().get(TOP_VIEWERS_CACHE_KEY);
            if (cached != null && !cached.isEmpty()) {
                return cached;
            }
            
            List<Streaming> top = streamingRepository.findTopLiveByEspectadores();
            
            if (!top.isEmpty()) {
                redisTemplate.opsForValue().set(TOP_VIEWERS_CACHE_KEY, top, CACHE_TTL, TimeUnit.MINUTES);
            }
            
            return top;
        }
        
        return streamingRepository.findTopLiveByEspectadores();
    }
    
    @SuppressWarnings("unchecked")
    public List<Streaming> getTopStreamingsByRegion(String region) {
        String cacheKey = REGIONAL_RANKING_PREFIX + region.toLowerCase();
        
        if (redisTemplate != null) {
            List<Streaming> cached = (List<Streaming>) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null && !cached.isEmpty()) {
                return cached;
            }
        }
        
        List <Streaming> top = streamingRepository.findTopLiveByEspectadoresByRegion(region);
            
        if (redisTemplate != null && !top.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, top, CACHE_TTL, TimeUnit.MINUTES);
        }
        
        return top;

    }
    
    
    public void updateRegionalStats(String streamingId, String region, Integer espectadores) {
        Optional<Streaming> streamingOpt = streamingRepository.findById(streamingId);
        streamingOpt.ifPresent(streaming -> {
            var stats = streaming.getEstadisticasRegionales();
            if (stats != null) {
                stats.put(region, stats.getOrDefault(region, 0) + espectadores);
                streaming.setEstadisticasRegionales(stats);
                streamingRepository.save(streaming);
                
                if (redisTemplate != null) {
                    String cacheKey = REGIONAL_RANKING_PREFIX + region.toLowerCase();
                    redisTemplate.delete(cacheKey);
                }
            }
        });
    }
}