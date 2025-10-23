package com.tpo.prisma.repository;
import com.tpo.prisma.model.Notificacion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface NotificacionRepository extends MongoRepository<Notificacion, String> {
    List<Notificacion> findByCreatorUserIn(List<String> creators, Pageable pageable);
    List<Notificacion> findByCreatorUserInOrderByCreatedAtDesc(Collection<String> creatorUsers);
}
