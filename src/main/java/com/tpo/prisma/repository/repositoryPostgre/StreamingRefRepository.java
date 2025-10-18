package com.tpo.prisma.repository.repositoryPostgre;

import com.tpo.prisma.model.modelPostgre.StreamingRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StreamingRefRepository extends JpaRepository<StreamingRef, String> {
    
    List<StreamingRef> findByEnVivoTrue();
    
    List<StreamingRef> findByCreadorMongoIdOrderByHoraComienzoDesc(String creadorMongoId);
}