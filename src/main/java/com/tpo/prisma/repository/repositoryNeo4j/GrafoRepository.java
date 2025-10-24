package com.tpo.prisma.repository.repositoryNeo4j;

import com.tpo.prisma.model.modelNeo4j.ContenidoNode;
import com.tpo.prisma.model.modelNeo4j.UsuarioNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrafoRepository extends Neo4jRepository<UsuarioNode, String> {

    @Query("MATCH (u1:Usuario {id:$origen}), (u2:Usuario {id:$destino}) MERGE (u1)-[:SIGUE]->(u2)")
    void crearSigue(@Param("origen") String usuarioOrigen, @Param("destino") String usuarioDestino);

    @Query("MATCH (u1:Usuario {id:$origen})-[r:SIGUE]->(u2:Usuario {id:$destino}) DELETE r")
    void eliminarSigue(@Param("origen") String usuarioOrigen, @Param("destino") String usuarioDestino);

    @Query("MATCH (u:Usuario {id:$usuarioId}) MERGE (c:Categoria {nombre:$categoria}) MERGE (u)-[r:INTERESADO_EN]->(c) ON CREATE SET r.score = $score ON MATCH SET r.score = coalesce(r.score,0) + $score")
    void interesadoEn(@Param("usuarioId") String usuarioId, @Param("categoria") String categoria, @Param("score") int score);

    @Query("MERGE (u:Usuario {id:$usuarioId}) MERGE (c:Categoria {nombre:$categoria}) MERGE (u)-[r:INTERESADO_EN]->(c) SET r.score = coalesce(r.score,0) + $delta WITH r FOREACH (_ IN CASE WHEN r.score <= 0 THEN [1] ELSE [] END | DELETE r)")
    void ajustarInteres(@Param("usuarioId") String usuarioId, @Param("categoria") String categoria, @Param("delta") int delta);

    @Query("MATCH (u:Usuario {id:$usuarioId})-[r:INTERESADO_EN]->(c:Categoria {nombre:$categoria}) DELETE r")
    void eliminarInteres(@Param("usuarioId") String usuarioId, @Param("categoria") String categoria);

    @Query("MATCH (u:Usuario {id:$usuarioId}), (c:Contenido {id:$contenidoId}) MERGE (u)-[r:VIO]->(c) ON CREATE SET r.timestamp = datetime() ON MATCH SET r.timestamp = datetime()")
    void registrarVista(@Param("usuarioId") String usuarioId, @Param("contenidoId") String contenidoId);

    @Query("MATCH (u:Usuario {id:$usuarioId}), (c:Contenido {id:$contenidoId}) MERGE (u)-[r:LE_GUSTO]->(c) ON CREATE SET r.timestamp = datetime() ON MATCH SET r.timestamp = datetime()")
    void registrarMeGusta(@Param("usuarioId") String usuarioId, @Param("contenidoId") String contenidoId);

    @Query("MATCH (u:Usuario {id:$usuarioId})-[r:LE_GUSTO]->(c:Contenido {id:$contenidoId}) DELETE r")
    void eliminarMeGusta(@Param("usuarioId") String usuarioId, @Param("contenidoId") String contenidoId);

    @Query("MATCH (c:Contenido {id:$contenidoId}) MERGE (cat:Categoria {nombre:$categoria}) MERGE (c)-[:EN_CATEGORIA]->(cat)")
    void asignarCategoria(@Param("contenidoId") String contenidoId, @Param("categoria") String categoria);

    @Query("MERGE (:Usuario {id:$id})")
    void mergeUsuario(@Param("id") String id);

    @Query("MERGE (:Contenido {id:$id})")
    void mergeContenido(@Param("id") String id);

    @Query("MATCH (u:Usuario {id:$usuarioId})-[:INTERESADO_EN]->(cat:Categoria)<-[:EN_CATEGORIA]-(c:Contenido) WHERE NOT (u)-[:VIO]->(c) RETURN DISTINCT c.id AS id LIMIT $limite")
    List<ContenidoNode> recomendarPorIntereses(@Param("usuarioId") String usuarioId, @Param("limite") int limite);

    @Query("MATCH (u:Usuario {id:$usuarioId})-[r:LE_GUSTO]->(c:Contenido {id:$contenidoId}) RETURN COUNT(r) > 0")
    boolean existeMeGusta(@Param("usuarioId") String usuarioId, @Param("contenidoId") String contenidoId);

    @Query("MATCH (u:Usuario {id:$usuarioId})-[r:VIO]->(c:Contenido) RETURN c.id AS id ORDER BY r.timestamp DESC LIMIT $limite")
    List<String> historialVistos(@Param("usuarioId") String usuarioId, @Param("limite") int limite);

    @Query("MATCH (u:Usuario {id:$usuarioId})-[:SIGUE]->(c:Usuario) RETURN c.id")
    List<String> seguidores(@Param("usuarioId") String usuarioId);

    // Encuentra usuarios recomendados: usuarios seguidos por las personas que el usuario sigue
    // Excluye: al usuario mismo y usuarios que ya sigue
    @Query("MATCH (u:Usuario {id:$usuarioId})-[:SIGUE]->(amigo:Usuario)-[:SIGUE]->(recomendado:Usuario) " +
           "WHERE NOT (u)-[:SIGUE]->(recomendado) " +
           "AND u.id <> recomendado.id " +
           "RETURN DISTINCT recomendado.id AS id")
    List<String> usuariosRecomendados(@Param("usuarioId") String usuarioId);
    
    // Elimina un usuario y todas sus relaciones
    @Query("MATCH (u:Usuario {id:$usuarioId}) DETACH DELETE u")
    void eliminarUsuario(@Param("usuarioId") String usuarioId);
    
    // Elimina un contenido y todas sus relaciones
    @Query("MATCH (c:Contenido {id:$contenidoId}) DETACH DELETE c")
    void eliminarContenido(@Param("contenidoId") String contenidoId);
}
