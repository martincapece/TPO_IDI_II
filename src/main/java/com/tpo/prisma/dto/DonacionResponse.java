package com.tpo.prisma.dto;

import java.math.BigDecimal;
import java.util.Date;

public class DonacionResponse {
    private Long donacionId;
    private String streamMongoId;
    private String donanteMongoId;
    private BigDecimal monto;
    private Date createdAt;
    private String mensaje;
    
    public DonacionResponse() {}
    
    public DonacionResponse(Long donacionId, String streamMongoId, String donanteMongoId, 
                           BigDecimal monto, Date createdAt) {
        this.donacionId = donacionId;
        this.streamMongoId = streamMongoId;
        this.donanteMongoId = donanteMongoId;
        this.monto = monto;
        this.createdAt = createdAt;
        this.mensaje = "Donación procesada exitosamente";
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

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}