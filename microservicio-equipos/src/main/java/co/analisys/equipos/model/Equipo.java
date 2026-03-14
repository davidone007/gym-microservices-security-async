package co.analisys.equipos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipo {
    @EmbeddedId
    private EquipoId id;

    @NotBlank(message = "El nombre del equipo no puede estar vacío")
    private String nombre;

    @Embedded
    private Descripcion descripcion;

    @Positive(message = "La cantidad debe ser mayor a 0")
    private int cantidad;
}
