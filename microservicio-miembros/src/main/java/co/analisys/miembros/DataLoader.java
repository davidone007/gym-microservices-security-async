package co.analisys.miembros;

import co.analisys.miembros.model.Miembro;
import co.analisys.miembros.model.MiembroId;
import co.analisys.miembros.model.Email;
import co.analisys.miembros.repository.MiembroRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {
    private final MiembroRepository miembroRepository;

    public DataLoader(MiembroRepository miembroRepository) {
        this.miembroRepository = miembroRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        Miembro miembro1 = new Miembro(
                new MiembroId("1"),
                "Juan Pérez",
                new Email("juan@email.com"),
                LocalDate.now());
        miembroRepository.save(miembro1);

        Miembro miembro2 = new Miembro(
                new MiembroId("2"),
                "María López",
                new Email("maria@email.com"),
                LocalDate.now().minusDays(30));
        miembroRepository.save(miembro2);

        System.out.println("Datos de miembros cargados exitosamente.");
    }
}
