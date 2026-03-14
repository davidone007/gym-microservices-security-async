error id: file:///C:/Users/nicol/Documents/9%20Semestre/Microservicios/Taller%20de%20Gimnasio/Microservices-GYM/microservicio-clases/src/main/java/co/analisys/clases/DataLoader.java:co/analisys/clases/model/EntrenadorId#
file:///C:/Users/nicol/Documents/9%20Semestre/Microservicios/Taller%20de%20Gimnasio/Microservices-GYM/microservicio-clases/src/main/java/co/analisys/clases/DataLoader.java
empty definition using pc, found symbol in pc: co/analisys/clases/model/EntrenadorId#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 183
uri: file:///C:/Users/nicol/Documents/9%20Semestre/Microservicios/Taller%20de%20Gimnasio/Microservices-GYM/microservicio-clases/src/main/java/co/analisys/clases/DataLoader.java
text:
```scala

package co.analisys.clases;

import co.analisys.clases.model.Clase;
import co.analisys.clases.model.ClaseId;
import co.analisys.clases.model.Horario;
import co.analisys.clases.model.@@EntrenadorId;
import co.analisys.clases.repository.ClaseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {
    private final ClaseRepository claseRepository;

    public DataLoader(ClaseRepository claseRepository) {
        this.claseRepository = claseRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        Clase clase1 = new Clase(
                new ClaseId("1"),
                "Yoga Matutino",
                new Horario(LocalDateTime.now().plusDays(1).withHour(8).withMinute(0)),
                20,
                new EntrenadorId("1"));
        claseRepository.save(clase1);

        Clase clase2 = new Clase(
                new ClaseId("2"),
                "Spinning Vespertino",
                new Horario(LocalDateTime.now().plusDays(1).withHour(18).withMinute(0)),
                15,
                new EntrenadorId("2"));
        claseRepository.save(clase2);

        // clase sin entrenador asignado inicialmente
        Clase clase3 = new Clase(
                new ClaseId("3"),
                "Pilates",
                new Horario(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0)),
                12,
                null);
        claseRepository.save(clase3);

        System.out.println("Datos de clases cargados exitosamente.");
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: co/analisys/clases/model/EntrenadorId#