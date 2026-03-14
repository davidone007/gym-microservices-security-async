package co.analisys.equipos;

import co.analisys.equipos.model.Equipo;
import co.analisys.equipos.model.EquipoId;
import co.analisys.equipos.model.Descripcion;
import co.analisys.equipos.repository.EquipoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final EquipoRepository equipoRepository;

    public DataLoader(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        Equipo equipo1 = new Equipo(
                new EquipoId("1"),
                "Mancuernas",
                new Descripcion("Set de mancuernas de 5kg"),
                20);
        equipoRepository.save(equipo1);

        Equipo equipo2 = new Equipo(
                new EquipoId("2"),
                "Bicicleta estática",
                new Descripcion("Bicicleta para spinning"),
                15);
        equipoRepository.save(equipo2);

        System.out.println("Datos de equipos cargados exitosamente.");
    }
}
