package co.analisys.entrenadores;

import co.analisys.entrenadores.model.Entrenador;
import co.analisys.entrenadores.model.EntrenadorId;
import co.analisys.entrenadores.model.Especialidad;

import co.analisys.entrenadores.repository.EntrenadorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final EntrenadorRepository entrenadorRepository;

    public DataLoader(EntrenadorRepository entrenadorRepository) {
        this.entrenadorRepository = entrenadorRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        Entrenador entrenador1 = new Entrenador(
                new EntrenadorId("1"),
                "Carlos Rodríguez",
                new Especialidad("Yoga"));
        entrenadorRepository.save(entrenador1);

        Entrenador entrenador2 = new Entrenador(
                new EntrenadorId("2"),
                "Ana Martínez",
                new Especialidad("Spinning"));
        entrenadorRepository.save(entrenador2);

        System.out.println("Datos de entrenadores cargados exitosamente.");
    }
}
