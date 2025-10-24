package com.tpo.prisma.service;

import com.tpo.prisma.model.Usuario;
import com.tpo.prisma.model.modelNeo4j.ContenidoNode;
import com.tpo.prisma.repository.UserRepository;
import com.tpo.prisma.repository.repositoryNeo4j.GrafoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GrafoService {

    @Autowired
    private GrafoRepository grafoRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    @Lazy
    private ContentService contentService;

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
    public void vio(String usuarioId, String contenidoId) { 
        grafoRepository.registrarVista(usuarioId, contenidoId);
        
        Optional<Usuario> usuarioOpt = userRepository.findById(usuarioId);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getDireccion() != null && usuario.getDireccion().getPais() != null) {
                String region = usuario.getDireccion().getPais();
                contentService.updateRegionalStats(contenidoId, region, 1);
            }
        }
    }

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
    
    @Transactional("neo4jTransactionManager")
    public void eliminarUsuario(String usuarioId) { grafoRepository.eliminarUsuario(usuarioId); }
    
    @Transactional("neo4jTransactionManager")
    public void eliminarContenido(String contenidoId) { grafoRepository.eliminarContenido(contenidoId); }
}
