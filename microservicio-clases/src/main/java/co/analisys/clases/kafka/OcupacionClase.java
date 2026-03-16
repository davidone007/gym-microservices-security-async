package co.analisys.clases.kafka;

import java.time.LocalDateTime;

public class OcupacionClase {
    private String claseId;
    private int ocupacionActual;
    private LocalDateTime timestamp;

    public OcupacionClase() {}

    public OcupacionClase(String claseId, int ocupacionActual, LocalDateTime timestamp) {
        this.claseId = claseId;
        this.ocupacionActual = ocupacionActual;
        this.timestamp = timestamp;
    }

    public String getClaseId() { return claseId; }
    public void setClaseId(String claseId) { this.claseId = claseId; }

    public int getOcupacionActual() { return ocupacionActual; }
    public void setOcupacionActual(int ocupacionActual) { this.ocupacionActual = ocupacionActual; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "OcupacionClase{" +
                "claseId='" + claseId + '\'' +
                ", ocupacionActual=" + ocupacionActual +
                ", timestamp=" + timestamp +
                '}';
    }
}
