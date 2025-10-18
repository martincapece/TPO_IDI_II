package com.tpo.prisma.dto;

import java.math.BigDecimal;

public class DonacionRequest {
    private String streamMongoId;
    private String donanteMongoId;
    private BigDecimal monto;
    
    public DonacionRequest() {}
    
    public DonacionRequest(String streamMongoId, String donanteMongoId, BigDecimal monto) {
        this.streamMongoId = streamMongoId;
        this.donanteMongoId = donanteMongoId;
        this.monto = monto;
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
}