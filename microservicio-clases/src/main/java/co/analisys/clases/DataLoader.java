
package co.analisys.clases;

import co.analisys.clases.model.Clase;
import co.analisys.clases.model.ClaseId;
import co.analisys.clases.model.Horario;
import co.analisys.clases.model.EntrenadorId;
import co.analisys.clases.repository.ClaseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
                new EntrenadorId("1"),
                new ArrayList<>(),
                new ArrayList<>());
        claseRepository.save(clase1);

        Clase clase2 = new Clase(
                new ClaseId("2"),
                "Spinning Vespertino",
                new Horario(LocalDateTime.now().plusDays(1).withHour(18).withMinute(0)),
                15,
                new EntrenadorId("2"),
                new ArrayList<>(),
                new ArrayList<>());
        claseRepository.save(clase2);

        // clase sin entrenador, equipos y miembros asignados inicialmente
        Clase clase3 = new Clase(
                new ClaseId("3"),
                "Pilates",
                new Horario(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0)),
                12,
                null,
                new ArrayList<>(),
                new ArrayList<>());
        claseRepository.save(clase3);

        System.out.println("Datos de clases cargados exitosamente.");
    }
}
