package com.tpo.prisma.model.modelPostgre;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "streaming")
public class StreamingRef {
    
    @Id
    private String streamMongoId;
    
    private String creadorMongoId;
    
    private Date horaComienzo;
    
    private Date horaFinalizado;
    
    private Boolean enVivo = true;
    
    public StreamingRef() {}
    
    public StreamingRef(String streamMongoId, String creadorMongoId, Date horaComienzo) {
        this.streamMongoId = streamMongoId;
        this.creadorMongoId = creadorMongoId;
        this.horaComienzo = horaComienzo;
        this.enVivo = true;
    }

    public String getStreamMongoId() {
        return streamMongoId;
    }

    public void setStreamMongoId(String streamMongoId) {
        this.streamMongoId = streamMongoId;
    }

    public String getCreadorMongoId() {
        return creadorMongoId;
    }

    public void setCreadorMongoId(String creadorMongoId) {
        this.creadorMongoId = creadorMongoId;
    }

    public Date getHoraComienzo() {
        return horaComienzo;
    }

    public void setHoraComienzo(Date horaComienzo) {
        this.horaComienzo = horaComienzo;
    }

    public Date getHoraFinalizado() {
        return horaFinalizado;
    }

    public void setHoraFinalizado(Date horaFinalizado) {
        this.horaFinalizado = horaFinalizado;
    }

    public Boolean getEnVivo() {
        return enVivo;
    }

    public void setEnVivo(Boolean enVivo) {
        this.enVivo = enVivo;
    }
}