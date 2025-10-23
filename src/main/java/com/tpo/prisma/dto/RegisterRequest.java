package com.tpo.prisma.dto;

import com.tpo.prisma.model.Usuario;
import java.util.List;

public class RegisterRequest {
    private String nombreUsuario;
    private String mail;
    private String contrasena;
    private Integer edad;
    private List<String> intereses;
    private Usuario.Direccion direccion;

    public RegisterRequest() {}

    public RegisterRequest(String nombreUsuario, String mail, String contrasena, 
                          Integer edad, List<String> intereses, Usuario.Direccion direccion) {
        this.nombreUsuario = nombreUsuario;
        this.mail = mail;
        this.contrasena = contrasena;
        this.edad = edad;
        this.intereses = intereses;
        this.direccion = direccion;
    }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public List<String> getIntereses() { return intereses; }
    public void setIntereses(List<String> intereses) { this.intereses = intereses; }

    public Usuario.Direccion getDireccion() { return direccion; }
    public void setDireccion(Usuario.Direccion direccion) { this.direccion = direccion; }
}
