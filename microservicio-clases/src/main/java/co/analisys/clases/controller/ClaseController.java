package co.analisys.clases.controller;

import co.analisys.clases.model.Clase;
import co.analisys.clases.service.ClaseService;
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

import co.analisys.clases.kafka.OcupacionClaseProducer;

@Tag(name = "Clases", description = "Programación y gestión de clases del gimnasio")
@RestController
@RequestMapping("/api/clases")
public class ClaseController {

    private final ClaseService claseService;
    private final OcupacionClaseProducer ocupacionClaseProducer;

    public ClaseController(ClaseService claseService, OcupacionClaseProducer ocupacionClaseProducer) {
        this.claseService = claseService;
        this.ocupacionClaseProducer = ocupacionClaseProducer;
    }

    @Operation(summary = "Actualizar y notificar ocupación de clase vía Kafka")
    @PostMapping("/{id}/ocupacion")
    public void notificarOcupacion(@PathVariable String id, @RequestParam int ocupacion) {
        ocupacionClaseProducer.actualizarOcupacion(id, ocupacion);
    }

    @Operation(summary = "Programar una clase", description = "Requiere rol ADMIN o TRAINER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clase creada", content = @Content(schema = @Schema(implementation = Clase.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permiso (requiere ADMIN o TRAINER)")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public Clase programar(@Valid @RequestBody Clase clase) {
        return claseService.programar(clase);
    }

    @Operation(summary = "Asignar entrenador a una clase", description = "Requiere ADMIN o TRAINER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clase actualizada"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permiso"),
            @ApiResponse(responseCode = "404", description = "Clase o entrenador no encontrado")
    })
    @PutMapping("/{id}/entrenador/{entrenadorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public Clase asignarEntrenador(
            @Parameter(description = "ID de la clase") @PathVariable String id,
            @Parameter(description = "ID del entrenador") @PathVariable String entrenadorId) {
        return claseService.asignarEntrenador(id, entrenadorId);
    }

    @Operation(summary = "Agregar equipo a una clase", description = "Requiere ADMIN o TRAINER.")
    @PutMapping("/{id}/equipos/{equipoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public Clase agregarEquipo(
            @Parameter(description = "ID de la clase") @PathVariable String id,
            @Parameter(description = "ID del equipo") @PathVariable String equipoId) {
        return claseService.agregarEquipo(id, equipoId);
    }

    @Operation(summary = "Quitar equipo de una clase", description = "Requiere ADMIN o TRAINER.")
    @DeleteMapping("/{id}/equipos/{equipoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public Clase removerEquipo(
            @Parameter(description = "ID de la clase") @PathVariable String id,
            @Parameter(description = "ID del equipo") @PathVariable String equipoId) {
        return claseService.removerEquipo(id, equipoId);
    }

    @Operation(summary = "Agregar miembro a una clase", description = "Requiere ADMIN o TRAINER.")
    @PutMapping("/{id}/miembros/{miembroId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public Clase agregarMiembro(
            @Parameter(description = "ID de la clase") @PathVariable String id,
            @Parameter(description = "ID del miembro") @PathVariable String miembroId) {
        return claseService.agregarMiembro(id, miembroId);
    }

    @Operation(summary = "Quitar miembro de una clase", description = "Requiere ADMIN o TRAINER.")
    @DeleteMapping("/{id}/miembros/{miembroId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public Clase removerMiembro(
            @Parameter(description = "ID de la clase") @PathVariable String id,
            @Parameter(description = "ID del miembro") @PathVariable String miembroId) {
        return claseService.removerMiembro(id, miembroId);
    }

    @Operation(summary = "Eliminar una clase", description = "Requiere ADMIN o TRAINER. Respuesta 204 sin cuerpo.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Clase eliminada"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permiso"),
            @ApiResponse(responseCode = "404", description = "Clase no encontrada")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public void eliminar(@Parameter(description = "ID de la clase") @PathVariable String id) {
        claseService.eliminar(id);
    }

    @Operation(summary = "Quitar entrenador de una clase", description = "Requiere ADMIN o TRAINER.")
    @DeleteMapping("/{id}/entrenador")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public Clase quitarEntrenador(@Parameter(description = "ID de la clase") @PathVariable String id) {
        return claseService.quitarEntrenador(id);
    }

    @Operation(summary = "Listar todas las clases", description = "Cualquier usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Lista de clases")
    @GetMapping
    public List<Clase> listar() {
        return claseService.listar();
    }

    @Operation(summary = "Actualizar horario de una clase", description = "Requiere ADMIN o TRAINER.")
    @PutMapping("/{id}/horario")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public Clase actualizarHorario(
            @Parameter(description = "ID de la clase") @PathVariable String id,
            @Valid @RequestBody co.analisys.clases.model.Horario horario) {
        return claseService.actualizarHorario(id, horario);
    }

    @Operation(summary = "Verificar si un entrenador está referenciado en alguna clase")
    @GetMapping("/verificar/entrenador/{entrenadorId}")
    public Map<String, Boolean> verificarEntrenador(
            @Parameter(description = "ID del entrenador") @PathVariable String entrenadorId) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("referenciado", claseService.isEntrenadorReferenciado(entrenadorId));
        return response;
    }

    @Operation(summary = "Verificar si un equipo está referenciado en alguna clase")
    @GetMapping("/verificar/equipo/{equipoId}")
    public Map<String, Boolean> verificarEquipo(
            @Parameter(description = "ID del equipo") @PathVariable String equipoId) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("referenciado", claseService.isEquipoReferenciado(equipoId));
        return response;
    }

    @Operation(summary = "Verificar si un miembro está referenciado en alguna clase")
    @GetMapping("/verificar/miembro/{miembroId}")
    public Map<String, Boolean> verificarMiembro(
            @Parameter(description = "ID del miembro") @PathVariable String miembroId) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("referenciado", claseService.isMiembroReferenciado(miembroId));
        return response;
    }
}
