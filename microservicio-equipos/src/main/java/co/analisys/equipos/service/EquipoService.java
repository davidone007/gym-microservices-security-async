package co.analisys.equipos.service;

import co.analisys.equipos.exception.InvalidEntityException;
import co.analisys.equipos.exception.ResourceNotFoundException;
import co.analisys.equipos.exception.ServiceUnavailableException;
import co.analisys.equipos.model.Equipo;
import co.analisys.equipos.model.EquipoId;
import co.analisys.equipos.repository.EquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${clase.service.url:http://localhost:8082/api/clases}")
    private String claseBaseUrl;

    public EquipoService(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }

    public Equipo registrar(Equipo equipo) {
        if (equipo.getId() != null && equipoRepository.existsById(equipo.getId())) {
            throw new InvalidEntityException("El ID de equipo ya existe: " + equipo.getId().getEquipo_id());
        }
        return equipoRepository.save(equipo);
    }

    public Equipo obtenerPorId(String id) {
        return equipoRepository.findById(new EquipoId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado: " + id));
    }

    public Equipo actualizar(String id, Equipo equipo) {
        Equipo existente = obtenerPorId(id);
        if (equipo.getNombre() != null && !equipo.getNombre().isBlank()) {
            existente.setNombre(equipo.getNombre());
        }
        if (equipo.getDescripcion() != null) {
            existente.setDescripcion(equipo.getDescripcion());
        }
        if (equipo.getCantidad() > 0) {
            existente.setCantidad(equipo.getCantidad());
        }
        return equipoRepository.save(existente);
    }

    public void eliminar(String id) {
        Equipo equipo = obtenerPorId(id);

        // Verificar si está referenciado en una clase
        if (isEquipoReferenciado(id)) {
            throw new InvalidEntityException("No se puede eliminar equipo " + id + ". Está asignado a una clase");
        }

        equipoRepository.deleteById(equipo.getId());
    }

    public List<Equipo> listar() {
        return equipoRepository.findAll();
    }

    public boolean existePorId(String id) {
        return equipoRepository.existsById(new EquipoId(id));
    }

    private boolean isEquipoReferenciado(String equipoId) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Boolean> response = restTemplate.getForObject(
                    claseBaseUrl + "/verificar/equipo/" + equipoId,
                    Map.class);
            return response != null && response.getOrDefault("referenciado", false);
        } catch (RestClientException ex) {
            // Si hay error de conexión, asumimos que está referenciado
            return true;
        }
    }
}
