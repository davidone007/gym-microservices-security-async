package co.analisys.pagos.dto;

import java.time.Instant;

public class Pago {
    private String id;
    private String miembroId;
    private String claseId;
    private double amount;
    private Instant timestamp;

    public Pago() {
    }

    public Pago(String id, String miembroId, double amount, Instant timestamp) {
        this.id = id;
        this.miembroId = miembroId;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getClaseId() {
        return claseId;
    }

    public void setClaseId(String claseId) {
        this.claseId = claseId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMiembroId() {
        return miembroId;
    }

    public void setMiembroId(String miembroId) {
        this.miembroId = miembroId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
