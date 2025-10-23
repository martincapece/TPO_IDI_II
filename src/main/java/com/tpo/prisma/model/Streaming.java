package com.tpo.prisma.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "Streaming")
public class Streaming {

    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String creatorId;
    
    private String titulo;
    private String lenguaje;
    private List<String> categoria;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime horaComienzo;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime horaFinalizado;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String chatId;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean enVivo;
    
    private String region;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private EstadisticasVivo estadisticasVivo;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Map<String, Integer> estadisticasRegionales;

    public Streaming() {
        this.enVivo = true;
        this.horaComienzo = LocalDateTime.now();
        this.estadisticasVivo = new EstadisticasVivo();
    }

    public static class EstadisticasVivo {
        private Integer picoEspectadores;
        private Integer promedioEspectadores;
        private Integer espectadores;
        private Integer cantDonaciones;
        private Integer sumMuestras;
        private Integer cantMuestras;

        public EstadisticasVivo() {
            this.picoEspectadores = 0;
            this.promedioEspectadores = 0;
            this.espectadores = 0;
            this.cantDonaciones = 0;
            this.sumMuestras = 0;
            this.cantMuestras = 0;
        }

        public EstadisticasVivo(Integer pico, Integer promedio, Integer espectadores, Integer donaciones) {
            this.picoEspectadores = pico;
            this.promedioEspectadores = promedio;
            this.espectadores = espectadores;
            this.cantDonaciones = donaciones;
            this.sumMuestras = 0;
            this.cantMuestras = 0;
        }

        public void sample(int current) {
            this.espectadores = current;
            if (current > picoEspectadores) picoEspectadores = current;
            sumMuestras += current;
            cantMuestras += 1;
            promedioEspectadores = (int) Math.round(sumMuestras / (double) cantMuestras);
        }

        public Integer getPicoEspectadores() {
            return picoEspectadores;
        }

        public void setPicoEspectadores(Integer picoEspectadores) {
            this.picoEspectadores = picoEspectadores;
        }

        public Integer getPromedioEspectadores() {
            return promedioEspectadores;
        }

        public void setPromedioEspectadores(Integer promedioEspectadores) {
            this.promedioEspectadores = promedioEspectadores;
        }

        public Integer getEspectadores() {
            return espectadores;
        }

        public void setEspectadores(Integer espectadores) {
            this.espectadores = espectadores;
        }

        public Integer getCantDonaciones() {
            return cantDonaciones;
        }

        public void setCantDonaciones(Integer cantDonaciones) {
            this.cantDonaciones = cantDonaciones;
        }

        public void actualizarPicoSiNecesario(Integer espectadoresActuales) {
            if (espectadoresActuales > this.picoEspectadores) {
                this.picoEspectadores = espectadoresActuales;
            }
        }

        public Integer getSumMuestras() {
            return sumMuestras;
        }

        public void setSumMuestras(Integer sumMuestras) {
            this.sumMuestras = sumMuestras;
        }

        public Integer getCantMuestras() {
            return cantMuestras;
        }

        public void setCantMuestras(Integer cantMuestras) {
            this.cantMuestras = cantMuestras;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
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

    public LocalDateTime getHoraComienzo() {
        return horaComienzo;
    }

    public void setHoraComienzo(LocalDateTime horaComienzo) {
        this.horaComienzo = horaComienzo;
    }

    public LocalDateTime getHoraFinalizado() {
        return horaFinalizado;
    }

    public void setHoraFinalizado(LocalDateTime horaFinalizado) {
        this.horaFinalizado = horaFinalizado;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Boolean getEnVivo() {
        return enVivo;
    }

    public void setEnVivo(Boolean enVivo) {
        this.enVivo = enVivo;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public EstadisticasVivo getEstadisticasVivo() {
        return estadisticasVivo;
    }

    public void setEstadisticasVivo(EstadisticasVivo estadisticasVivo) {
        this.estadisticasVivo = estadisticasVivo;
    }

    public Map<String, Integer> getEstadisticasRegionales() {
        return estadisticasRegionales;
    }

    public void setEstadisticasRegionales(Map<String, Integer> estadisticasRegionales) {
        this.estadisticasRegionales = estadisticasRegionales;
    }

    public Integer getPicoEspectadores() {
        return estadisticasVivo != null ? estadisticasVivo.getPicoEspectadores() : 0;
    }

    public Integer getPromedioEspectadores() {
        return estadisticasVivo != null ? estadisticasVivo.getPromedioEspectadores() : 0;
    }

    public Integer getEspectadores() {
        return estadisticasVivo != null ? estadisticasVivo.getEspectadores() : 0;
    }

    public Integer getCantDonaciones() {
        return estadisticasVivo != null ? estadisticasVivo.getCantDonaciones() : 0;
    }

    public Integer getEspectadoresTotales() {
        if (estadisticasRegionales == null || estadisticasRegionales.isEmpty()) {
            return 0;
        }
        return estadisticasRegionales.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }
}
