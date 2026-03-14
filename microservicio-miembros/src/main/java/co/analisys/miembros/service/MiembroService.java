package co.analisys.miembros.service;

import co.analisys.miembros.exception.InvalidEntityException;
import co.analisys.miembros.exception.ResourceNotFoundException;
import co.analisys.miembros.exception.ServiceUnavailableException;
import co.analisys.miembros.model.Miembro;
import co.analisys.miembros.model.MiembroId;
import co.analisys.miembros.repository.MiembroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Service
public class MiembroService {

    private final MiembroRepository miembroRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${clase.service.url:http://localhost:8082/api/clases}")
    private String claseBaseUrl;

    public MiembroService(MiembroRepository miembroRepository) {
        this.miembroRepository = miembroRepository;
    }

    public Miembro registrar(Miembro miembro) {
        if (miembro.getId() != null && miembroRepository.existsById(miembro.getId())) {
            throw new InvalidEntityException("El ID de miembro ya existe: " + miembro.getId().getMiembro_id());
        }
        return miembroRepository.save(miembro);
    }

    public Miembro obtenerPorId(String id) {
        return miembroRepository.findById(new MiembroId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Miembro no encontrado: " + id));
    }

    public Miembro actualizar(String id, Miembro miembro) {
        Miembro existente = obtenerPorId(id);
        if (miembro.getNombre() != null && !miembro.getNombre().isBlank()) {
            existente.setNombre(miembro.getNombre());
        }
        if (miembro.getEmail() != null) {
            existente.setEmail(miembro.getEmail());
        }
        if (miembro.getFechaInscripcion() != null) {
            existente.setFechaInscripcion(miembro.getFechaInscripcion());
        }
        return miembroRepository.save(existente);
    }

    public void eliminar(String id) {
        Miembro miembro = obtenerPorId(id);

        // Verificar si está referenciado en una clase
        if (isMiembroReferenciado(id)) {
            throw new InvalidEntityException("No se puede eliminar miembro " + id + ". Está inscrito en una clase");
        }

        miembroRepository.deleteById(miembro.getId());
    }

    public List<Miembro> listar() {
        return miembroRepository.findAll();
    }

    public boolean existePorId(String id) {
        return miembroRepository.existsById(new MiembroId(id));
    }

    private boolean isMiembroReferenciado(String miembroId) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Boolean> response = restTemplate.getForObject(
                    claseBaseUrl + "/verificar/miembro/" + miembroId,
                    Map.class);
            return response != null && response.getOrDefault("referenciado", false);
        } catch (RestClientException ex) {
            // Si hay error de conexión, asumimos que está referenciado
            return true;
        }
    }
}
