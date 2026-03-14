package co.analisys.miembros.controller;

import co.analisys.miembros.model.Miembro;
import co.analisys.miembros.service.MiembroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/miembros")
public class MiembroController {

    private final MiembroService miembroService;

    public MiembroController(MiembroService miembroService) {
        this.miembroService = miembroService;
    }

    @PostMapping
    public Miembro crear(@Valid @RequestBody Miembro miembro) {
        return miembroService.registrar(miembro);
    }

    @GetMapping("/{id}")
    public Miembro obtenerPorId(@PathVariable String id) {
        return miembroService.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public Miembro actualizar(@PathVariable String id, @Valid @RequestBody Miembro miembro) {
        return miembroService.actualizar(id, miembro);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        miembroService.eliminar(id);
    }

    @GetMapping
    public List<Miembro> listar() {
        return miembroService.listar();
    }

    @GetMapping("/{id}/existe")
    public Map<String, Boolean> existe(@PathVariable String id) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("existe", miembroService.existePorId(id));
        return response;
    }
}
