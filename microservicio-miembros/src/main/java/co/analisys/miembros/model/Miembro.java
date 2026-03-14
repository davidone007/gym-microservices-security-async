package co.analisys.miembros.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Miembro {
    @EmbeddedId
    private MiembroId id;

    @NotBlank(message = "El nombre del miembro no puede estar vacío")
    private String nombre;

    @Embedded
    private Email email;

    private LocalDate fechaInscripcion;
}
