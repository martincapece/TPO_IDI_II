package com.tpo.prisma.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "Contenido")
public class Content {
    
    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;
    
    private String tipo;
    private String titulo;
    private String lenguaje;
    private List<String> categoria;
    private String visibilidad;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer duracion;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Map<String, Integer> estadisticasRegionales;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String creatorId;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer cantMeGusta;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime publishedAt;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    public Content() {
        this.publishedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.cantMeGusta = 0;
        this.duracion = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getLenguaje() {
        return lenguaje;
    }

    public void setLenguaje(String lenguaje) {
        this.lenguaje = lenguaje;
    }

    public List<String> getCategoria() {
        return categoria;
    }

    public void setCategoria(List<String> categoria) {
        this.categoria = categoria;
    }

    public String getVisibilidad() {
        return visibilidad;
    }

    public void setVisibilidad(String visibilidad) {
        this.visibilidad = visibilidad;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    public Map<String, Integer> getEstadisticasRegionales() {
        return estadisticasRegionales;
    }

    public void setEstadisticasRegionales(Map<String, Integer> estadisticasRegionales) {
        this.estadisticasRegionales = estadisticasRegionales;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getCantMeGusta() {
        return cantMeGusta;
    }

    public void setCantMeGusta(Integer cantMeGusta) {
        this.cantMeGusta = cantMeGusta;
    }

    public Integer getCantVistas() {
        if (estadisticasRegionales == null || estadisticasRegionales.isEmpty()) {
            return 0;
        }
        return estadisticasRegionales.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
