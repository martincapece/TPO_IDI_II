package com.tpo.prisma.repository.repositoryPostgre;

import com.tpo.prisma.model.modelPostgre.Donacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DonacionRepository extends JpaRepository<Donacion, Long> {
    
    List<Donacion> findByStreamMongoIdOrderByCreatedAtDesc(String streamMongoId);
    
    List<Donacion> findByDonanteMongoIdOrderByCreatedAtDesc(String donanteMongoId);
    
    @Query("SELECT COALESCE(SUM(d.monto), 0) FROM Donacion d WHERE d.streamMongoId = :streamId")
    BigDecimal calcularTotalDonaciones(@Param("streamId") String streamId);
    
}