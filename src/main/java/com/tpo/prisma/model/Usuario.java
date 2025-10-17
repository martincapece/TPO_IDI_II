package com.tpo.prisma.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "Usuario")
public class Usuario {
    
    @Id
    private String id;
    
    @Field("nombre_usuario")
    private String nombreUsuario;
    
    private String mail;
    
    private String contrasena; // Se guardará encriptada
    
    private Integer edad;
    
    @Field("cant_videos")
    private Integer cantVideos;
    
    private List<String> intereses;
    
    private List<Object> historial; // Puede ser List<Map<String, Object>> si necesitas más estructura
    
    private Direccion direccion;

    // Constructor vacío
    public Usuario() {
        this.cantVideos = 0;
    }

    // Clase interna para Direccion
    public static class Direccion {
        private String pais;
        private String ciudad;

        public Direccion() {}

        public Direccion(String pais, String ciudad) {
            this.pais = pais;
            this.ciudad = ciudad;
        }

        // Getters y Setters
        public String getPais() { return pais; }
        public void setPais(String pais) { this.pais = pais; }
        public String getCiudad() { return ciudad; }
        public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public Integer getCantVideos() { return cantVideos; }
    public void setCantVideos(Integer cantVideos) { this.cantVideos = cantVideos; }

    public List<String> getIntereses() { return intereses; }
    public void setIntereses(List<String> intereses) { this.intereses = intereses; }

    public List<Object> getHistorial() { return historial; }
    public void setHistorial(List<Object> historial) { this.historial = historial; }

    public Direccion getDireccion() { return direccion; }
    public void setDireccion(Direccion direccion) { this.direccion = direccion; }
}
