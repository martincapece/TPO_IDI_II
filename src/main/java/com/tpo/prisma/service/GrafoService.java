package com.tpo.prisma.service;

import com.tpo.prisma.model.modelNeo4j.ContenidoNode;
import com.tpo.prisma.repository.repositoryNeo4j.GrafoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GrafoService {

    @Autowired
    private GrafoRepository grafoRepository;

    // Sincronización mínima de nodos
    @Transactional("neo4jTransactionManager")
    public void syncUsuario(String id) { grafoRepository.mergeUsuario(id); }
    @Transactional("neo4jTransactionManager")
    public void syncContenido(String id) { grafoRepository.mergeContenido(id); }

    // 1) SIGUE
    @Transactional("neo4jTransactionManager")
    public void seguir(String origen, String destino) { grafoRepository.crearSigue(origen, destino); }
    @Transactional("neo4jTransactionManager")
    public void dejarDeSeguir(String origen, String destino) { grafoRepository.eliminarSigue(origen, destino); }

    // 2) INTERESADO_EN
    @Transactional("neo4jTransactionManager")
    public void interesadoEn(String usuarioId, String categoria, int score) { grafoRepository.interesadoEn(usuarioId, categoria, score); }
    @Transactional("neo4jTransactionManager")
    public void ajustarInteres(String usuarioId, String categoria, int delta) { grafoRepository.ajustarInteres(usuarioId, categoria, delta); }
    @Transactional("neo4jTransactionManager")
    public void eliminarInteres(String usuarioId, String categoria) { grafoRepository.eliminarInteres(usuarioId, categoria); }

    // 3) VIO
    @Transactional("neo4jTransactionManager")
    public void vio(String usuarioId, String contenidoId, int secciones) { grafoRepository.registrarVista(usuarioId, contenidoId, secciones); }

    // 4) LE_GUSTO
    @Transactional("neo4jTransactionManager")
    public void meGusto(String usuarioId, String contenidoId) { grafoRepository.registrarMeGusta(usuarioId, contenidoId); }
    @Transactional("neo4jTransactionManager")
    public void quitarMeGusta(String usuarioId, String contenidoId) { grafoRepository.eliminarMeGusta(usuarioId, contenidoId); }

    // 5) EN_CATEGORIA
    @Transactional("neo4jTransactionManager")
    public void enCategoria(String contenidoId, String categoria) { grafoRepository.asignarCategoria(contenidoId, categoria); }

    // 6) TIENE_TAG
    @Transactional("neo4jTransactionManager")
    public void tieneTag(String contenidoId, String tag) { grafoRepository.asignarTag(contenidoId, tag); }

    // Recomendaciones
    public List<ContenidoNode> recomendarPorIntereses(String usuarioId, int limite) { return grafoRepository.recomendarPorIntereses(usuarioId, limite); }
    public List<ContenidoNode> relacionadoPorCoVistas(String contenidoId, int limite) { return grafoRepository.relacionadoPorCoVistas(contenidoId, limite); }
}
