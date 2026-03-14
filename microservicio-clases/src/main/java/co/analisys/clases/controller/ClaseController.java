package co.analisys.clases.controller;

import co.analisys.clases.model.Clase;
import co.analisys.clases.service.ClaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clases")
public class ClaseController {

    private final ClaseService claseService;

    public ClaseController(ClaseService claseService) {
        this.claseService = claseService;
    }

    @PostMapping
    public Clase programar(@Valid @RequestBody Clase clase) {
        return claseService.programar(clase);
    }

    @PutMapping("/{id}/entrenador/{entrenadorId}")
    public Clase asignarEntrenador(@PathVariable String id, @PathVariable String entrenadorId) {
        return claseService.asignarEntrenador(id, entrenadorId);
    }

    @PutMapping("/{id}/equipos/{equipoId}")
    public Clase agregarEquipo(@PathVariable String id, @PathVariable String equipoId) {
        return claseService.agregarEquipo(id, equipoId);
    }

    @DeleteMapping("/{id}/equipos/{equipoId}")
    public Clase removerEquipo(@PathVariable String id, @PathVariable String equipoId) {
        return claseService.removerEquipo(id, equipoId);
    }

    @PutMapping("/{id}/miembros/{miembroId}")
    public Clase agregarMiembro(@PathVariable String id, @PathVariable String miembroId) {
        return claseService.agregarMiembro(id, miembroId);
    }

    @DeleteMapping("/{id}/miembros/{miembroId}")
    public Clase removerMiembro(@PathVariable String id, @PathVariable String miembroId) {
        return claseService.removerMiembro(id, miembroId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        claseService.eliminar(id);
    }

    @DeleteMapping("/{id}/entrenador")
    public Clase quitarEntrenador(@PathVariable String id) {
        return claseService.quitarEntrenador(id);
    }

    @GetMapping
    public List<Clase> listar() {
        return claseService.listar();
    }

    // Endpoints para verificar si una entidad está siendo usada
    @GetMapping("/verificar/entrenador/{entrenadorId}")
    public Map<String, Boolean> verificarEntrenador(@PathVariable String entrenadorId) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("referenciado", claseService.isEntrenadorReferenciado(entrenadorId));
        return response;
    }

    @GetMapping("/verificar/equipo/{equipoId}")
    public Map<String, Boolean> verificarEquipo(@PathVariable String equipoId) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("referenciado", claseService.isEquipoReferenciado(equipoId));
        return response;
    }

    @GetMapping("/verificar/miembro/{miembroId}")
    public Map<String, Boolean> verificarMiembro(@PathVariable String miembroId) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("referenciado", claseService.isMiembroReferenciado(miembroId));
        return response;
    }
}
