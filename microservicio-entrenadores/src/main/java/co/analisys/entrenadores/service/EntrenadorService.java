package co.analisys.entrenadores.service;

import co.analisys.entrenadores.exception.InvalidEntityException;
import co.analisys.entrenadores.exception.ResourceNotFoundException;
import co.analisys.entrenadores.exception.ServiceUnavailableException;
import co.analisys.entrenadores.model.Entrenador;
import co.analisys.entrenadores.model.EntrenadorId;
import co.analisys.entrenadores.repository.EntrenadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Service
public class EntrenadorService {

    private final EntrenadorRepository entrenadorRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${clase.service.url:http://localhost:8082/api/clases}")
    private String claseBaseUrl;

    public EntrenadorService(EntrenadorRepository entrenadorRepository) {
        this.entrenadorRepository = entrenadorRepository;
    }

    public Entrenador registrar(Entrenador entrenador) {
        // Validar que no exista un entrenador con el mismo ID
        if (entrenador.getId() != null && entrenadorRepository.existsById(entrenador.getId())) {
            throw new InvalidEntityException("El ID de entrenador ya existe: " + entrenador.getId().getEntrenador_id());
        }
        return entrenadorRepository.save(entrenador);
    }

    public Entrenador obtenerPorId(String id) {
        return entrenadorRepository.findById(new EntrenadorId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Entrenador no encontrado: " + id));
    }

    public Entrenador actualizar(String id, Entrenador entrenador) {
        Entrenador existente = obtenerPorId(id);
        if (entrenador.getNombre() != null && !entrenador.getNombre().isBlank()) {
            existente.setNombre(entrenador.getNombre());
        }
        if (entrenador.getEspecialidad() != null) {
            existente.setEspecialidad(entrenador.getEspecialidad());
        }
        return entrenadorRepository.save(existente);
    }

    public void eliminar(String id) {
        Entrenador entrenador = obtenerPorId(id);

        // Verificar si está referenciado en una clase
        if (isEntrenadorReferenciado(id)) {
            throw new InvalidEntityException("No se puede eliminar entrenador " + id + ". Está asignado a una clase");
        }

        entrenadorRepository.deleteById(entrenador.getId());
    }

    public Boolean existePorId(String id) {
        return entrenadorRepository.existsById(new EntrenadorId(id));
    }

    public List<Entrenador> listar() {
        return entrenadorRepository.findAll();
    }

    private boolean isEntrenadorReferenciado(String entrenadorId) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Boolean> response = restTemplate.getForObject(
                    claseBaseUrl + "/verificar/entrenador/" + entrenadorId,
                    Map.class);
            return response != null && response.getOrDefault("referenciado", false);
        } catch (RestClientException ex) {
            // Si hay error de conexión, asumimos que está referenciado (mejor ser
            // cautelosos)
            return true;
        }
    }
}
