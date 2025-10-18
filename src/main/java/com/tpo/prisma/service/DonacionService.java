package com.tpo.prisma.service;

import com.tpo.prisma.dto.DonacionRequest;
import com.tpo.prisma.dto.DonacionResponse;
import com.tpo.prisma.model.modelPostgre.Donacion;
import com.tpo.prisma.model.modelPostgre.StreamingRef;
import com.tpo.prisma.model.modelPostgre.UsuarioRef;
import com.tpo.prisma.repository.repositoryPostgre.DonacionRepository;
import com.tpo.prisma.repository.repositoryPostgre.StreamingRefRepository;
import com.tpo.prisma.repository.repositoryPostgre.UsuarioRefRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonacionService {

    @Autowired
    private DonacionRepository donacionRepository;
    
    @Autowired
    private UsuarioRefRepository usuarioRefRepository;
    
    @Autowired
    private StreamingRefRepository streamingRefRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Transactional
    public DonacionResponse procesarDonacion(DonacionRequest request) {
        validarDonacion(request);
        
        StreamingRef streaming = streamingRefRepository.findById(request.getStreamMongoId())
                .orElseThrow(() -> new RuntimeException("Stream no encontrado"));
        
        if (!streaming.getEnVivo()) {
            throw new RuntimeException("El stream no está activo");
        }
        
        if (!usuarioRefRepository.existsById(request.getDonanteMongoId())) {
            UsuarioRef usuario = new UsuarioRef(request.getDonanteMongoId());
            usuarioRefRepository.save(usuario);
        }
        
        Donacion donacion = new Donacion(
            request.getStreamMongoId(),
            request.getDonanteMongoId(),
            request.getMonto(),
            new Date()
        );
        Donacion saved = donacionRepository.save(donacion);
        
        actualizarTotalDonacionesEnMongo(request.getStreamMongoId(), request.getMonto());
        
        return new DonacionResponse(
            saved.getDonacionId(),
            saved.getStreamMongoId(),
            saved.getDonanteMongoId(),
            saved.getMonto(),
            saved.getCreatedAt()
        );
    }

    private void validarDonacion(DonacionRequest request) {
        if (request.getStreamMongoId() == null || request.getStreamMongoId().isEmpty()) {
            throw new IllegalArgumentException("Stream ID es requerido");
        }
        if (request.getDonanteMongoId() == null || request.getDonanteMongoId().isEmpty()) {
            throw new IllegalArgumentException("Donante ID es requerido");
        }
        if (request.getMonto() == null || request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }
    }

    private void actualizarTotalDonacionesEnMongo(String streamId, BigDecimal monto) {
        Query query = new Query(Criteria.where("_id").is(streamId));
        Update update = new Update().inc("estadisticasVivo.cantDonaciones", 1);
        mongoTemplate.updateFirst(query, update, "Streaming");
    }

    public List<DonacionResponse> getDonacionesByStream(String streamId) {
        List<Donacion> donaciones = donacionRepository
                .findByStreamMongoIdOrderByCreatedAtDesc(streamId);
        
        return donaciones.stream()
                .map(d -> new DonacionResponse(
                    d.getDonacionId(),
                    d.getStreamMongoId(),
                    d.getDonanteMongoId(),
                    d.getMonto(),
                    d.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public BigDecimal getTotalDonaciones(String streamId) {
        return donacionRepository.calcularTotalDonaciones(streamId);
    }

    public List<DonacionResponse> getDonacionesByUsuario(String usuarioId) {
        List<Donacion> donaciones = donacionRepository
                .findByDonanteMongoIdOrderByCreatedAtDesc(usuarioId);
        
        return donaciones.stream()
                .map(d -> new DonacionResponse(
                    d.getDonacionId(),
                    d.getStreamMongoId(),
                    d.getDonanteMongoId(),
                    d.getMonto(),
                    d.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void sincronizarStreaming(String streamMongoId, String creadorMongoId) {
        if (!usuarioRefRepository.existsById(creadorMongoId)) {
            UsuarioRef usuario = new UsuarioRef(creadorMongoId);
            usuarioRefRepository.save(usuario);
        }
        
        if (!streamingRefRepository.existsById(streamMongoId)) {
            StreamingRef streaming = new StreamingRef(
                streamMongoId,
                creadorMongoId,
                new Date()
            );
            streamingRefRepository.save(streaming);
        }
    }
    
    @Transactional
    public void finalizarStreaming(String streamMongoId) {
        streamingRefRepository.findById(streamMongoId).ifPresent(streaming -> {
            streaming.setEnVivo(false);
            streaming.setHoraFinalizado(new Date());
            streamingRefRepository.save(streaming);
        });
    }
}