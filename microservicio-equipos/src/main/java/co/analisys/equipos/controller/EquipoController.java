package co.analisys.equipos.controller;

import co.analisys.equipos.model.Equipo;
import co.analisys.equipos.service.EquipoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    @PostMapping
    public Equipo crear(@Valid @RequestBody Equipo equipo) {
        return equipoService.registrar(equipo);
    }

    @GetMapping("/{id}")
    public Equipo obtenerPorId(@PathVariable String id) {
        return equipoService.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public Equipo actualizar(@PathVariable String id, @Valid @RequestBody Equipo equipo) {
        return equipoService.actualizar(id, equipo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        equipoService.eliminar(id);
    }

    @GetMapping
    public List<Equipo> listar() {
        return equipoService.listar();
    }

    @GetMapping("/{id}/existe")
    public Map<String, Boolean> existe(@PathVariable String id) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("existe", equipoService.existePorId(id));
        return response;
    }
}
