package co.analisys.miembros.kafka;

public class ResumenEntrenamiento {
    private int totalCalorias;
    private int totalMinutos;

    public ResumenEntrenamiento() {}

    public ResumenEntrenamiento actualizar(DatosEntrenamiento nuevo) {
        this.totalCalorias += nuevo.getCaloriasQuemadas();
        this.totalMinutos += nuevo.getDuracionMinutos();
        return this;
    }

    public int getTotalCalorias() { return totalCalorias; }
    public void setTotalCalorias(int totalCalorias) { this.totalCalorias = totalCalorias; }

    public int getTotalMinutos() { return totalMinutos; }
    public void setTotalMinutos(int totalMinutos) { this.totalMinutos = totalMinutos; }
}
