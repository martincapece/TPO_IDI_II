package com.tpo.prisma.model.modelPostgre;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "usuario_ref")
public class UsuarioRef {
    
    @Id
    private String usuarioMongoId;
    
    private Date createdAt;
    public UsuarioRef() {
        this.createdAt = new Date();
    }
    
    public UsuarioRef(String usuarioMongoId) {
        this.usuarioMongoId = usuarioMongoId;
        this.createdAt = new Date();
    }

    public String getUsuarioMongoId() {
        return usuarioMongoId;
    }

    public void setUsuarioMongoId(String usuarioMongoId) {
        this.usuarioMongoId = usuarioMongoId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}