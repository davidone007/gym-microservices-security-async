package co.analisys.entrenadores.controller;

import co.analisys.entrenadores.model.Entrenador;
import co.analisys.entrenadores.service.EntrenadorService;
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

@Tag(name = "Entrenadores", description = "CRUD de entrenadores. Solo ADMIN puede crear/actualizar/eliminar; consultas para cualquier autenticado.")
@RestController
@RequestMapping("/api/entrenadores")
public class EntrenadorController {

    private final EntrenadorService entrenadorService;

    public EntrenadorController(EntrenadorService entrenadorService) {
        this.entrenadorService = entrenadorService;
    }

    @Operation(summary = "Crear entrenador", description = "Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrenador creado", content = @Content(schema = @Schema(implementation = Entrenador.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permiso (solo ADMIN)")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Entrenador crear(@Valid @RequestBody Entrenador entrenador) {
        return entrenadorService.registrar(entrenador);
    }

    @Operation(summary = "Obtener entrenador por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrenador encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Entrenador no encontrado")
    })
    @GetMapping("/{id}")
    public Entrenador obtenerPorId(@Parameter(description = "ID del entrenador") @PathVariable String id) {
        return entrenadorService.obtenerPorId(id);
    }

    @Operation(summary = "Actualizar entrenador", description = "Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrenador actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permiso"),
            @ApiResponse(responseCode = "404", description = "Entrenador no encontrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Entrenador actualizar(
            @Parameter(description = "ID del entrenador") @PathVariable String id,
            @Valid @RequestBody Entrenador entrenador) {
        return entrenadorService.actualizar(id, entrenador);
    }

    @Operation(summary = "Eliminar entrenador", description = "Requiere rol ADMIN. Respuesta 204.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Entrenador eliminado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permiso"),
            @ApiResponse(responseCode = "404", description = "Entrenador no encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@Parameter(description = "ID del entrenador") @PathVariable String id) {
        entrenadorService.eliminar(id);
    }

    @Operation(summary = "Verificar si existe un entrenador con el ID dado")
    @GetMapping("/{id}/existe")
    public Map<String, Boolean> existeEntrenador(@Parameter(description = "ID del entrenador") @PathVariable String id) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("existe", entrenadorService.existePorId(id));
        return response;
    }

    @Operation(summary = "Listar todos los entrenadores")
    @ApiResponse(responseCode = "200", description = "Lista de entrenadores")
    @GetMapping
    public List<Entrenador> listar() {
        return entrenadorService.listar();
    }
}
