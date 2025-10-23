package com.tpo.prisma.dto;

public class AuthResponse {
    private String message;
    private String userId;
    private String nombreUsuario;
    private String region;

    public AuthResponse() {}

    public AuthResponse(String message, String userId, String nombreUsuario, String region) {
        this.message = message;
        this.userId = userId;
        this.nombreUsuario = nombreUsuario;
        this.region = region;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
}
