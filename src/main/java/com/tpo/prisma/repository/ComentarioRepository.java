package com.tpo.prisma.repository;

import com.tpo.prisma.model.Comentario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComentarioRepository extends MongoRepository<Comentario, String> {
    
    // Buscar comentarios por contenido
    @Query("{ 'contenido_id': ?0 }")
    List<Comentario> findByContenidoId(String contenidoId);
    
    // Buscar comentarios por usuario
    @Query("{ 'usuario_id': ?0 }")
    List<Comentario> findByUsuarioId(String usuarioId);
    
    // Buscar comentarios por contenido ordenados por fecha (más recientes primero)
    @Query(value = "{ 'contenido_id': ?0 }", sort = "{ 'created_at': -1 }")
    List<Comentario> findByContenidoIdOrderByCreatedAtDesc(String contenidoId);
}
