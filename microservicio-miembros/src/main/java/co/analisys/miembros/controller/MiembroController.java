package co.analisys.miembros.controller;

import co.analisys.miembros.model.Miembro;
import co.analisys.miembros.service.MiembroService;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Miembros", description = "CRUD de miembros. Solo ADMIN puede crear/actualizar/eliminar; consultas para cualquier autenticado.")
@RestController
@RequestMapping("/api/miembros")
public class MiembroController {

    private final MiembroService miembroService;

    public MiembroController(MiembroService miembroService) {
        this.miembroService = miembroService;
    }

    @Operation(summary = "Crear miembro", description = "Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Miembro creado", content = @Content(schema = @Schema(implementation = Miembro.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permiso (solo ADMIN)")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Miembro crear(@Valid @RequestBody Miembro miembro) {
        return miembroService.registrar(miembro);
    }

    @Operation(summary = "Obtener miembro por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Miembro encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Miembro no encontrado")
    })
    @GetMapping("/{id}")
    public Miembro obtenerPorId(@Parameter(description = "ID del miembro") @PathVariable String id) {
        return miembroService.obtenerPorId(id);
    }

    @Operation(summary = "Actualizar miembro", description = "Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Miembro actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permiso"),
            @ApiResponse(responseCode = "404", description = "Miembro no encontrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Miembro actualizar(
            @Parameter(description = "ID del miembro") @PathVariable String id,
            @Valid @RequestBody Miembro miembro) {
        return miembroService.actualizar(id, miembro);
    }

    @Operation(summary = "Eliminar miembro", description = "Requiere rol ADMIN. Respuesta 204.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Miembro eliminado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permiso"),
            @ApiResponse(responseCode = "404", description = "Miembro no encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@Parameter(description = "ID del miembro") @PathVariable String id) {
        miembroService.eliminar(id);
    }

    @Operation(summary = "Listar todos los miembros")
    @ApiResponse(responseCode = "200", description = "Lista de miembros")
    @GetMapping
    public List<Miembro> listar() {
        return miembroService.listar();
    }

    @Operation(summary = "Verificar si existe un miembro con el ID dado")
    @GetMapping("/{id}/existe")
    public Map<String, Boolean> existe(@Parameter(description = "ID del miembro") @PathVariable String id) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("existe", miembroService.existePorId(id));
        return response;
    }

    @Autowired
    private org.springframework.kafka.core.KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private co.analisys.miembros.kafka.RecuperacionService recuperacionService;

    @Operation(summary = "Enviar datos de entrenamiento (Kafka)", description = "Simula el envío de datos a Kafka para procesamiento de streams")
    @PostMapping("/{id}/entrenamiento")
    public org.springframework.http.ResponseEntity<Map<String, Object>> registrarEntrenamiento(@PathVariable String id, @RequestBody co.analisys.miembros.kafka.DatosEntrenamiento datos) {
        Map<String, Object> response = new HashMap<>();
        try {
            datos.setMiembroId(id);
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String json = mapper.writeValueAsString(datos);
            kafkaTemplate.send("datos-entrenamiento", id, json);
            kafkaTemplate.send("topic-a", id, json); // para probar la recuperación simulada
            System.out.println("✅ Datos de entrenamiento enviados a Kafka (Topic: datos-entrenamiento): " + json);
            
            response.put("mensaje", "Exito: Datos de entrenamiento enviados a Kafka");
            response.put("topic", "datos-entrenamiento");
            response.put("payload_enviado", mapper.readTree(json));
            response.put("observacion", "Revisa la consola de streams y offset explorer.");
            return org.springframework.http.ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", e.getMessage());
            return org.springframework.http.ResponseEntity.status(500).body(response);
        }
    }

    @Operation(summary = "Forzar Recuperación Kafka", description = "Simula la caída del sistema y recupera desde el último Offset guardado")
    @PostMapping("/recuperacion")
    public org.springframework.http.ResponseEntity<Map<String, Object>> forzarRecuperacion() {
        Map<String, Object> response = new HashMap<>();
        try {
            recuperacionService.iniciarProcesamiento();
            response.put("mensaje", "Recuperacion de OFFSETS iniciada.");
            response.put("funcionalidad", "Se ha desactivado el auto-commit. Leyendo mensajes pendientes manualmente.");
            response.put("topic", "topic-a");
            return org.springframework.http.ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return org.springframework.http.ResponseEntity.status(500).body(response);
        }
    }
}
