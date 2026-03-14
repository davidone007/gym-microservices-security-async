package co.analisys.entrenadores.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entrenador {
    @EmbeddedId
    private EntrenadorId id;

    @NotBlank(message = "El nombre del entrenador no puede estar vacío")
    private String nombre;

    @Embedded
    private Especialidad especialidad;
}
