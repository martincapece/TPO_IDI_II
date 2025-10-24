package com.tpo.prisma.repository;

import com.tpo.prisma.model.Content;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContentRepository extends MongoRepository<Content, String> {

    List<Content> findByTipo(String tipo);
    
    @Query("{ 'categoria': { $in: ?0 } }")
    List<Content> findByCategoria(List<String> categorias);
    
    @Query("{ 'creatorId': ?0 }")
    List<Content> findByCreatorId(String creatorId);
    
    List<Content> findByVisibilidad(String visibilidad);

    @Query(value = "{}", sort = "{ 'cantMeGusta': -1 }")
    List<Content> findTopByOrderByCantMeGustaDesc();

    @Query(value = "{}", sort = "{ 'cantVistas': -1 }")
    List<Content> findTopByOrderByCantVistasDesc();
    
    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'cantMeGusta': 1 }, '$set': { 'updatedAt': ?1 } }")
    void incrementLikesById(String id, java.time.LocalDateTime updatedAt);
    
    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'cantMeGusta': -1 }, '$set': { 'updatedAt': ?1 } }")
    void decrementLikesById(String id, java.time.LocalDateTime updatedAt);
}
