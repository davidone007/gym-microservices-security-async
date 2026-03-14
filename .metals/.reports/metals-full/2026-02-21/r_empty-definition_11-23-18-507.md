error id: file:///C:/Users/nicol/Documents/9%20Semestre/Microservicios/Taller%20de%20Gimnasio/Microservices-GYM/microservicio-clases/src/main/java/co/analisys/clases/model/Clase.java:_empty_/EntrenadorId#
file:///C:/Users/nicol/Documents/9%20Semestre/Microservicios/Taller%20de%20Gimnasio/Microservices-GYM/microservicio-clases/src/main/java/co/analisys/clases/model/Clase.java
empty definition using pc, found symbol in pc: _empty_/EntrenadorId#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 408
uri: file:///C:/Users/nicol/Documents/9%20Semestre/Microservicios/Taller%20de%20Gimnasio/Microservices-GYM/microservicio-clases/src/main/java/co/analisys/clases/model/Clase.java
text:
```scala
package co.analisys.clases.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Clase {
    @EmbeddedId
    private ClaseId id;

    private String nombre;

    @Embedded
    private Horario horario;

    private int capacidadMaxima;

    @Embedded
    private Entrenad@@orId entrenadorId; // referencia al microservicio de entrenadores
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/EntrenadorId#