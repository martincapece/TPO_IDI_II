package com.tpo.prisma.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "Contenido")
public class Content {
    
    @Id
    private String id; // MongoDB _id
    
    private String tipo; // "video", "audio", "image", "text"
    private String titulo;
    private String lenguaje; // "español", "inglés", etc.
    private List<String> categoria; // Array de categorías: ["futbol", "IRL"]
    private String visibilidad; // "publico", "privado", "seguidores"
    private Integer duracion; // Duración en segundos (para videos/audios)
    
    // Estadísticas regionales
    private Map<String, Integer> estadisticas_regionales; // {"Argentina": 521, "Brasil": 50, ...}
    
    private String creatorId; // ID del creador (referencia a User en MongoDB)
    private Integer cantMeGusta; // Cantidad de "me gusta"
    
    // Timestamps
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;

    // Constructor por defecto
    public Content() {
        this.publishedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.cantMeGusta = 0;
        this.duracion = 0;
    }

    // Getters y Setters
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

    public Map<String, Integer> getEstadisticas_regionales() {
        return estadisticas_regionales;
    }

    public void setEstadisticas_regionales(Map<String, Integer> estadisticas_regionales) {
        this.estadisticas_regionales = estadisticas_regionales;
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
