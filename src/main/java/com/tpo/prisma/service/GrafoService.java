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

    @Transactional("neo4jTransactionManager")
    public void syncUsuario(String id) { grafoRepository.mergeUsuario(id); }
    @Transactional("neo4jTransactionManager")
    public void syncContenido(String id) { grafoRepository.mergeContenido(id); }

    @Transactional("neo4jTransactionManager")
    public void seguir(String origen, String destino) { grafoRepository.crearSigue(origen, destino); }
    @Transactional("neo4jTransactionManager")
    public void dejarDeSeguir(String origen, String destino) { grafoRepository.eliminarSigue(origen, destino); }

    @Transactional("neo4jTransactionManager")
    public void interesadoEn(String usuarioId, String categoria, int score) { grafoRepository.interesadoEn(usuarioId, categoria, score); }
    @Transactional("neo4jTransactionManager")
    public void ajustarInteres(String usuarioId, String categoria, int delta) { grafoRepository.ajustarInteres(usuarioId, categoria, delta); }
    @Transactional("neo4jTransactionManager")
    public void eliminarInteres(String usuarioId, String categoria) { grafoRepository.eliminarInteres(usuarioId, categoria); }

    @Transactional("neo4jTransactionManager")
    public void vio(String usuarioId, String contenidoId, int secciones) { grafoRepository.registrarVista(usuarioId, contenidoId, secciones); }

    @Transactional("neo4jTransactionManager")
    public void meGusto(String usuarioId, String contenidoId) { grafoRepository.registrarMeGusta(usuarioId, contenidoId); }
    @Transactional("neo4jTransactionManager")
    public void quitarMeGusta(String usuarioId, String contenidoId) { grafoRepository.eliminarMeGusta(usuarioId, contenidoId); }

    @Transactional("neo4jTransactionManager")
    public void enCategoria(String contenidoId, String categoria) { grafoRepository.asignarCategoria(contenidoId, categoria); }

    public List<ContenidoNode> recomendarPorIntereses(String usuarioId, int limite) { return grafoRepository.recomendarPorIntereses(usuarioId, limite); }

    public boolean existeMeGusta(String usuarioId, String contenidoId) { return grafoRepository.existeMeGusta(usuarioId, contenidoId); }

    @Transactional("neo4jTransactionManager")
    public List<String> historialVistos(String usuarioId, int limite) { return grafoRepository.historialVistos(usuarioId, limite); }

    @Transactional("neo4jTransactionManager")
    public List<String> seguidores(String usuarioId) { return grafoRepository.seguidores(usuarioId); }

    @Transactional("neo4jTransactionManager")
    public List<String> usuariosRecomendados(String usuarioId) { return grafoRepository.usuariosRecomendados(usuarioId); }
}
