package com.tpo.prisma.repository;

import com.tpo.prisma.model.Content;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContentRepository extends MongoRepository<Content, String> {
    
    // Buscar por tipo de contenido
    List<Content> findByTipo(String tipo);
    
    // Buscar por categoría (usando array)
    @Query("{ 'categoria': { $in: ?0 } }")
    List<Content> findByCategoria(List<String> categorias);
    
    // 🔧 CAMBIO: Usar camelCase en lugar de creator_id
    @Query("{ 'creator_id': ?0 }")
    List<Content> findByCreatorId(String creatorId);
    
    // Buscar por visibilidad
    List<Content> findByVisibilidad(String visibilidad);
    
    // Obtener contenidos más populares (por likes)
    @Query(value = "{}", sort = "{ 'cant_meGusta': -1 }")
    List<Content> findTopByOrderByCant_meGustaDesc();
}
