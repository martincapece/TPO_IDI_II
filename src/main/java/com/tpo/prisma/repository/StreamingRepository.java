package com.tpo.prisma.repository;

import com.tpo.prisma.model.Streaming;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StreamingRepository extends MongoRepository<Streaming, String> {

    @Query("{ 'creatorId': ?0 }")
    List<Streaming> findByCreatorId(String creatorId);
    
    @Query("{ 'enVivo': true, 'horaFinalizado': null }")
    List<Streaming> findByEnVivo();
    
    @Query("{ 'enVivo': true, 'region': { $regex: ?0, $options: 'i' } }")
    List<Streaming> findLiveByRegion(String region);
    
    @Query("{ 'enVivo': true, 'categoria': { $in: ?0 } }")
    List<Streaming> findLiveByCategorias(List<String> categorias);
    
    @Query(value = "{ 'enVivo': true }", sort = "{ 'estadisticasVivo.espectadores': -1 }")
    List<Streaming> findTopLiveByEspectadores();
    
    @Query(value = "{ 'enVivo': true, 'region': { $regex: ?0, $options: 'i' } }", sort = "{ 'estadisticasVivo.espectadores': -1 }")
    List<Streaming> findTopLiveByEspectadoresByRegion(String region);
}
