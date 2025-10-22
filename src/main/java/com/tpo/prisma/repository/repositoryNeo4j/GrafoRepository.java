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

    // 1) SIGUE: Usuario -> Usuario
    @Query("MATCH (u1:Usuario {id:$origen}), (u2:Usuario {id:$destino}) MERGE (u1)-[:SIGUE]->(u2)")
    void crearSigue(@Param("origen") String usuarioOrigen, @Param("destino") String usuarioDestino);

    // 1.1) Eliminar SIGUE
    @Query("MATCH (u1:Usuario {id:$origen})-[r:SIGUE]->(u2:Usuario {id:$destino}) DELETE r")
    void eliminarSigue(@Param("origen") String usuarioOrigen, @Param("destino") String usuarioDestino);

    // 2) INTERESADO_EN: Usuario -> Categoria (score)
    @Query("MATCH (u:Usuario {id:$usuarioId}) MERGE (c:Categoria {nombre:$categoria}) MERGE (u)-[r:INTERESADO_EN]->(c) ON CREATE SET r.score = $score ON MATCH SET r.score = coalesce(r.score,0) + $score")
    void interesadoEn(@Param("usuarioId") String usuarioId, @Param("categoria") String categoria, @Param("score") int score);

    // 2.1) Ajustar INTERESADO_EN por delta; si score <= 0, eliminar relación
    @Query("MERGE (u:Usuario {id:$usuarioId}) MERGE (c:Categoria {nombre:$categoria}) MERGE (u)-[r:INTERESADO_EN]->(c) SET r.score = coalesce(r.score,0) + $delta WITH r FOREACH (_ IN CASE WHEN r.score <= 0 THEN [1] ELSE [] END | DELETE r)")
    void ajustarInteres(@Param("usuarioId") String usuarioId, @Param("categoria") String categoria, @Param("delta") int delta);

    // 2.2) Eliminar INTERESADO_EN explícitamente
    @Query("MATCH (u:Usuario {id:$usuarioId})-[r:INTERESADO_EN]->(c:Categoria {nombre:$categoria}) DELETE r")
    void eliminarInteres(@Param("usuarioId") String usuarioId, @Param("categoria") String categoria);

    // 3) VIO: Usuario -> Contenido (timestamp, secciones)
    @Query("MATCH (u:Usuario {id:$usuarioId}), (c:Contenido {id:$contenidoId}) MERGE (u)-[r:VIO]->(c) ON CREATE SET r.timestamp = datetime(), r.secciones = $secciones ON MATCH SET r.timestamp = datetime(), r.secciones = $secciones")
    void registrarVista(@Param("usuarioId") String usuarioId, @Param("contenidoId") String contenidoId, @Param("secciones") int secciones);

    // 4) LE_GUSTO: Usuario -> Contenido (timestamp)
    @Query("MATCH (u:Usuario {id:$usuarioId}), (c:Contenido {id:$contenidoId}) MERGE (u)-[r:LE_GUSTO]->(c) ON CREATE SET r.timestamp = datetime() ON MATCH SET r.timestamp = datetime()")
    void registrarMeGusta(@Param("usuarioId") String usuarioId, @Param("contenidoId") String contenidoId);

    // 4.1) Eliminar LE_GUSTO (quitar me gusta)
    @Query("MATCH (u:Usuario {id:$usuarioId})-[r:LE_GUSTO]->(c:Contenido {id:$contenidoId}) DELETE r")
    void eliminarMeGusta(@Param("usuarioId") String usuarioId, @Param("contenidoId") String contenidoId);

    // 5) EN_CATEGORIA: Contenido -> Categoria
    @Query("MATCH (c:Contenido {id:$contenidoId}) MERGE (cat:Categoria {nombre:$categoria}) MERGE (c)-[:EN_CATEGORIA]->(cat)")
    void asignarCategoria(@Param("contenidoId") String contenidoId, @Param("categoria") String categoria);

    // Sincronización de nodos mínimos
    @Query("MERGE (:Usuario {id:$id})")
    void mergeUsuario(@Param("id") String id);

    @Query("MERGE (:Contenido {id:$id})")
    void mergeContenido(@Param("id") String id);

    // Consultas para recomendaciones
    // Por intereses del usuario (excluye vistos)
    @Query("MATCH (u:Usuario {id:$usuarioId})-[:INTERESADO_EN]->(cat:Categoria)<-[:EN_CATEGORIA]-(c:Contenido) WHERE NOT (u)-[:VIO]->(c) RETURN DISTINCT c.id AS id LIMIT $limite")
    List<ContenidoNode> recomendarPorIntereses(@Param("usuarioId") String usuarioId, @Param("limite") int limite);
}
