package com.tpo.prisma.service;
import com.tpo.prisma.model.Notificacion;
import com.tpo.prisma.repository.NotificacionRepository;
import com.tpo.prisma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionService {

    @Autowired private NotificacionRepository notiRepo;
    @Autowired private GrafoService grafoService;
    @Autowired private UserRepository userRepository;
    // @Autowired private MongoTemplate mongoTemplate; // No usado actualmente

    public void emitirContenidoPublicado(String creatorUserId, String contenidoId) {
        // Buscar el nombre del usuario
        String nombreUsuario = userRepository.findById(creatorUserId)
                .map(u -> u.getNombreUsuario())
                .orElse(creatorUserId); // Fallback al ID si no se encuentra
        
        Notificacion n = new Notificacion();
        n.setCreatorUser(creatorUserId);
        n.setContentId(contenidoId);
        n.setMensaje(nombreUsuario + " publicó un nuevo contenido!");
        n.setCreatedAt(new java.util.Date());
        notiRepo.save(n);
    }

    public void emitirStreamIniciado(String creatorUserId, String streamId) {
        // Buscar el nombre del usuario
        String nombreUsuario = userRepository.findById(creatorUserId)
                .map(u -> u.getNombreUsuario())
                .orElse(creatorUserId); // Fallback al ID si no se encuentra
        
        Notificacion n = new Notificacion();
        n.setCreatorUser(creatorUserId);
        n.setContentId(streamId);
        n.setMensaje("El stream de " + nombreUsuario + " ha comenzado!");
        n.setCreatedAt(new java.util.Date());
        notiRepo.save(n);
    }
    
    public List<Notificacion> feedParaUsuario(String userId, int page, int size) {
        // Usuarios a los que sigue el usuario actual (ids de usuario)
        List<String> seguidos = grafoService.seguidores(userId);
        if (seguidos == null || seguidos.isEmpty()) {
            return List.of();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return notiRepo.findByCreatorUserIn(seguidos, pageable);
    }
    
}
