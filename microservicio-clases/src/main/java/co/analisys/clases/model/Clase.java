package co.analisys.clases.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Clase {
    @EmbeddedId
    private ClaseId id;

    @NotBlank(message = "El nombre de la clase no puede estar vacío")
    private String nombre;

    @Embedded
    private Horario horario;

    @Positive(message = "La capacidad máxima debe ser mayor a 0")
    private int capacidadMaxima;

    @Embedded
    private EntrenadorId entrenadorId; // referencia al microservicio de entrenadores

    @ElementCollection
    @CollectionTable(name = "clase_equipos", joinColumns = @JoinColumn(name = "clase_id"))
    private List<EquipoId> equipos = new ArrayList<>(); // referencia a múltiples equipos

    @ElementCollection
    @CollectionTable(name = "clase_miembros", joinColumns = @JoinColumn(name = "clase_id"))
    private List<MiembroId> miembros = new ArrayList<>(); // referencia a múltiples miembros
}
