package co.analisys.equipos.controller;

import co.analisys.equipos.model.Equipo;
import co.analisys.equipos.service.EquipoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Equipos", description = "CRUD de equipos. Solo ADMIN puede crear/actualizar/eliminar; consultas para cualquier autenticado.")
@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    @Operation(summary = "Crear equipo", description = "Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Equipo creado", content = @Content(schema = @Schema(implementation = Equipo.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permiso (solo ADMIN)")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Equipo crear(@Valid @RequestBody Equipo equipo) {
        return equipoService.registrar(equipo);
    }

    @Operation(summary = "Obtener equipo por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Equipo encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Equipo no encontrado")
    })
    @GetMapping("/{id}")
    public Equipo obtenerPorId(@Parameter(description = "ID del equipo") @PathVariable String id) {
        return equipoService.obtenerPorId(id);
    }

    @Operation(summary = "Actualizar equipo", description = "Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Equipo actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permiso"),
            @ApiResponse(responseCode = "404", description = "Equipo no encontrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Equipo actualizar(
            @Parameter(description = "ID del equipo") @PathVariable String id,
            @Valid @RequestBody Equipo equipo) {
        return equipoService.actualizar(id, equipo);
    }

    @Operation(summary = "Eliminar equipo", description = "Requiere rol ADMIN. Respuesta 204.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Equipo eliminado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permiso"),
            @ApiResponse(responseCode = "404", description = "Equipo no encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@Parameter(description = "ID del equipo") @PathVariable String id) {
        equipoService.eliminar(id);
    }

    @Operation(summary = "Listar todos los equipos")
    @ApiResponse(responseCode = "200", description = "Lista de equipos")
    @GetMapping
    public List<Equipo> listar() {
        return equipoService.listar();
    }

    @Operation(summary = "Verificar si existe un equipo con el ID dado")
    @GetMapping("/{id}/existe")
    public Map<String, Boolean> existe(@Parameter(description = "ID del equipo") @PathVariable String id) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("existe", equipoService.existePorId(id));
        return response;
    }
}
