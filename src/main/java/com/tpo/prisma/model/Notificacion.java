package com.tpo.prisma.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "Notificacion")
public class Notificacion {

    @Id
    private String id;
    
    private String mensaje;
    private String contentId;
    private String creatorUser;
    private Date createdAt;

    public Notificacion() {} 
    public Notificacion(String mensaje, String contentId, String creatorUser) {
        this.mensaje = mensaje;
        this.contentId = contentId;
        this.creatorUser = creatorUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getCreatorUser() {
        return creatorUser;
    }

    public void setCreatorUser(String creatorUser) {
        this.creatorUser = creatorUser;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
