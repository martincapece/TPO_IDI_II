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
    
    @Query("{ 'enVivo': true }")
    List<Streaming> findByEnVivo();
    
    @Query("{ 'categoria': { $in: ?0 } }")
    List<Streaming> findByCategoria(List<String> categorias);
    
    @Query("{ 'region': ?0 }")
    List<Streaming> findByRegion(String region);
    
    @Query("{ 'enVivo': true, 'categoria': { $in: ?0 } }")
    List<Streaming> findLiveByCategoria(List<String> categorias);
    
    @Query(value = "{ 'enVivo': true }", sort = "{ 'estadisticasVivo.espectadores': -1 }")
    List<Streaming> findTopLiveByEspectadores();
    
    @Query(value = "{}", sort = "{ 'estadisticasVivo.picoEspectadores': -1 }")
    List<Streaming> findTopByPicoEspectadores();
    
}
