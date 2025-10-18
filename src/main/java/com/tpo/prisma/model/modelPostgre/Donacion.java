package com.tpo.prisma.model.modelPostgre;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "donacion")
public class Donacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long donacionId;
    
    private String streamMongoId;
    
    private String donanteMongoId;
    
    private BigDecimal monto;
    
    private Date createdAt;
    
    public Donacion() {
        this.createdAt = new Date();
    }
    
    public Donacion(String streamMongoId, String donanteMongoId, BigDecimal monto, Date createdAt) {
        this.streamMongoId = streamMongoId;
        this.donanteMongoId = donanteMongoId;
        this.monto = monto;
        this.createdAt = createdAt;
    }

    public Long getDonacionId() {
        return donacionId;
    }

    public void setDonacionId(Long donacionId) {
        this.donacionId = donacionId;
    }

    public String getStreamMongoId() {
        return streamMongoId;
    }

    public void setStreamMongoId(String streamMongoId) {
        this.streamMongoId = streamMongoId;
    }

    public String getDonanteMongoId() {
        return donanteMongoId;
    }

    public void setDonanteMongoId(String donanteMongoId) {
        this.donanteMongoId = donanteMongoId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}