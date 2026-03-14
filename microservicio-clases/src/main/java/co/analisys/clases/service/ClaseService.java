package co.analisys.clases.service;

import co.analisys.clases.exception.InvalidEntityException;
import co.analisys.clases.exception.ResourceNotFoundException;
import co.analisys.clases.exception.ServiceUnavailableException;
import co.analisys.clases.model.Clase;
import co.analisys.clases.model.ClaseId;
import co.analisys.clases.model.EntrenadorId;
import co.analisys.clases.model.EquipoId;
import co.analisys.clases.model.MiembroId;
import co.analisys.clases.repository.ClaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Service
public class ClaseService {

    private final ClaseRepository claseRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${entrenador.service.url}")
    private String entrenadorBaseUrl;

    @Value("${equipo.service.url}")
    private String equipoBaseUrl;

    @Value("${miembro.service.url}")
    private String miembroBaseUrl;

    public ClaseService(ClaseRepository claseRepository) {
        this.claseRepository = claseRepository;
    }

    public Clase programar(Clase clase) {
        // Validar que no exista una clase con el mismo ID
        if (clase.getId() != null && claseRepository.existsById(clase.getId())) {
            throw new InvalidEntityException("El ID de clase ya existe: " + clase.getId().getClase_id());
        }

        if (clase.getEntrenadorId() != null && clase.getEntrenadorId().getEntrenador_id() != null) {
            validarEntrenador(clase.getEntrenadorId().getEntrenador_id());
        }

        // Validar todos los equipos en la lista
        if (clase.getEquipos() != null && !clase.getEquipos().isEmpty()) {
            for (EquipoId equipoId : clase.getEquipos()) {
                if (equipoId != null && equipoId.getEquipo_id() != null) {
                    validarEquipo(equipoId.getEquipo_id());
                }
            }
        }

        // Validar todos los miembros en la lista
        if (clase.getMiembros() != null && !clase.getMiembros().isEmpty()) {
            for (MiembroId miembroId : clase.getMiembros()) {
                if (miembroId != null && miembroId.getMiembro_id() != null) {
                    validarMiembro(miembroId.getMiembro_id());
                }
            }
        }

        return claseRepository.save(clase);
    }

    public List<Clase> listar() {
        return claseRepository.findAll();
    }

    public Clase asignarEntrenador(String claseId, String entrenadorIdValue) {
        Clase clase = claseRepository.findById(new ClaseId(claseId))
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada: " + claseId));
        validarEntrenador(entrenadorIdValue);
        clase.setEntrenadorId(new EntrenadorId(entrenadorIdValue));
        return claseRepository.save(clase);
    }

    public Clase agregarEquipo(String claseId, String equipoIdValue) {
        Clase clase = claseRepository.findById(new ClaseId(claseId))
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada: " + claseId));

        validarEquipo(equipoIdValue);

        EquipoId equipoId = new EquipoId(equipoIdValue);

        // Verificar si el equipo ya está asignado
        if (clase.getEquipos().contains(equipoId)) {
            throw new InvalidEntityException("El equipo ya está asignado a esta clase: " + equipoIdValue);
        }

        clase.getEquipos().add(equipoId);
        return claseRepository.save(clase);
    }

    public Clase removerEquipo(String claseId, String equipoIdValue) {
        Clase clase = claseRepository.findById(new ClaseId(claseId))
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada: " + claseId));

        EquipoId equipoId = new EquipoId(equipoIdValue);

        if (!clase.getEquipos().contains(equipoId)) {
            throw new InvalidEntityException("El equipo no está asignado a esta clase: " + equipoIdValue);
        }

        clase.getEquipos().remove(equipoId);
        return claseRepository.save(clase);
    }

    public Clase agregarMiembro(String claseId, String miembroIdValue) {
        Clase clase = claseRepository.findById(new ClaseId(claseId))
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada: " + claseId));

        // Validar capacidad
        if (clase.getMiembros().size() >= clase.getCapacidadMaxima()) {
            throw new InvalidEntityException("Clase llena. Capacidad máxima: " + clase.getCapacidadMaxima());
        }

        validarMiembro(miembroIdValue);

        MiembroId miembroId = new MiembroId(miembroIdValue);

        // Verificar si el miembro ya está inscrito
        if (clase.getMiembros().contains(miembroId)) {
            throw new InvalidEntityException("El miembro ya está inscrito en esta clase: " + miembroIdValue);
        }

        clase.getMiembros().add(miembroId);
        return claseRepository.save(clase);
    }

    public Clase removerMiembro(String claseId, String miembroIdValue) {
        Clase clase = claseRepository.findById(new ClaseId(claseId))
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada: " + claseId));

        MiembroId miembroId = new MiembroId(miembroIdValue);

        if (!clase.getMiembros().contains(miembroId)) {
            throw new InvalidEntityException("El miembro no está inscrito en esta clase: " + miembroIdValue);
        }

        clase.getMiembros().remove(miembroId);
        return claseRepository.save(clase);
    }

    public void eliminar(String claseId) {
        ClaseId id = new ClaseId(claseId);
        if (!claseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Clase no encontrada: " + claseId);
        }
        claseRepository.deleteById(id);
    }

    public Clase quitarEntrenador(String claseId) {
        Clase clase = claseRepository.findById(new ClaseId(claseId))
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada: " + claseId));
        if (clase.getEntrenadorId() == null || clase.getEntrenadorId().getEntrenador_id() == null) {
            throw new InvalidEntityException("La clase no tiene un entrenador asignado.");
        }
        clase.setEntrenadorId(null);
        return claseRepository.save(clase);
    }

    private void validarEntrenador(String entrenadorId) {
        Boolean existe;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Boolean> response = restTemplate.getForObject(
                    entrenadorBaseUrl + "/" + entrenadorId + "/existe",
                    Map.class);
            existe = response != null && response.getOrDefault("existe", false);
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException(
                    "No se pudo conectar al servicio de entrenadores: " + ex.getMessage());
        }
        if (!existe) {
            throw new ResourceNotFoundException("Entrenador no encontrado: " + entrenadorId);
        }
    }

    private void validarEquipo(String equipoId) {
        Boolean existe;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Boolean> response = restTemplate.getForObject(
                    equipoBaseUrl + "/" + equipoId + "/existe",
                    Map.class);
            existe = response != null && response.getOrDefault("existe", false);
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException(
                    "No se pudo conectar al servicio de equipos: " + ex.getMessage());
        }
        if (!existe) {
            throw new ResourceNotFoundException("Equipo no encontrado: " + equipoId);
        }
    }

    private void validarMiembro(String miembroId) {
        Boolean existe;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Boolean> response = restTemplate.getForObject(
                    miembroBaseUrl + "/" + miembroId + "/existe",
                    Map.class);
            existe = response != null && response.getOrDefault("existe", false);
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException(
                    "No se pudo conectar al servicio de miembros: " + ex.getMessage());
        }
        if (!existe) {
            throw new ResourceNotFoundException("Miembro no encontrado: " + miembroId);
        }
    }

    // Métodos para verificar si una entidad está siendo usada en una clase
    public boolean isEntrenadorReferenciado(String entrenadorId) {
        return claseRepository.findAll().stream()
                .anyMatch(clase -> clase.getEntrenadorId() != null &&
                        clase.getEntrenadorId().getEntrenador_id().equals(entrenadorId));
    }

    public boolean isEquipoReferenciado(String equipoId) {
        return claseRepository.findAll().stream()
                .anyMatch(clase -> clase.getEquipos().stream()
                        .anyMatch(eq -> eq.getEquipo_id().equals(equipoId)));
    }

    public boolean isMiembroReferenciado(String miembroId) {
        return claseRepository.findAll().stream()
                .anyMatch(clase -> clase.getMiembros().stream()
                        .anyMatch(m -> m.getMiembro_id().equals(miembroId)));
    }
}
