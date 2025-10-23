package com.tpo.prisma.service;
import com.tpo.prisma.model.Notificacion;
import com.tpo.prisma.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacionService {

    @Autowired private NotificacionRepository notiRepo;
    @Autowired private GrafoService grafoService;
    @Autowired private MongoTemplate mongoTemplate;

    public void emitirContenidoPublicado(String creatorUser, String contenidoId) {
        Notificacion n = new Notificacion();
        n.setCreatorUser(creatorUser);
        n.setContentId(contenidoId);
        n.setMensaje(creatorUser + " publicó un nuevo contenido!");
        notiRepo.save(n);
    }

    public void emitirStreamIniciado(String creatorUser, String streamId) {
        Notificacion n = new Notificacion();
        n.setCreatorUser(creatorUser);
        n.setContentId(streamId);
        n.setMensaje("El stream de " + creatorUser + " ha comenzado!");
        notiRepo.save(n);
    }
    
}
