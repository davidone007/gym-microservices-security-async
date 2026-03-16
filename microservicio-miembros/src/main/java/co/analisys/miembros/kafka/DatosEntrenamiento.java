package co.analisys.miembros.kafka;

import java.time.LocalDateTime;

public class DatosEntrenamiento {
    private String miembroId;
    private int caloriasQuemadas;
    private int duracionMinutos;

    public DatosEntrenamiento() {}
    // Getters and setters
    public String getMiembroId() { return miembroId; }
    public void setMiembroId(String miembroId) { this.miembroId = miembroId; }

    public int getCaloriasQuemadas() { return caloriasQuemadas; }
    public void setCaloriasQuemadas(int caloriasQuemadas) { this.caloriasQuemadas = caloriasQuemadas; }

    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }
}
