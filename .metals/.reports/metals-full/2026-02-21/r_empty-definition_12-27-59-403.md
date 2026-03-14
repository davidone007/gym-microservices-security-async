error id: file:///C:/Users/nicol/Documents/9%20Semestre/Microservicios/Taller%20de%20Gimnasio/Microservices-GYM/microservicio-clases/src/main/java/co/analisys/clases/controller/ClaseController.java:co/analisys/clases/model/Clase#
file:///C:/Users/nicol/Documents/9%20Semestre/Microservicios/Taller%20de%20Gimnasio/Microservices-GYM/microservicio-clases/src/main/java/co/analisys/clases/controller/ClaseController.java
empty definition using pc, found symbol in pc: co/analisys/clases/model/Clase#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 72
uri: file:///C:/Users/nicol/Documents/9%20Semestre/Microservicios/Taller%20de%20Gimnasio/Microservices-GYM/microservicio-clases/src/main/java/co/analisys/clases/controller/ClaseController.java
text:
```scala
package co.analisys.clases.controller;

import co.analisys.clases.model.@@Clase;
import co.analisys.clases.service.ClaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clases")
public class ClaseController {

    private final ClaseService claseService;

    public ClaseController(ClaseService claseService) {
        this.claseService = claseService;
    }

    @PostMapping
    public Clase crear(@RequestBody Clase clase) {
        return claseService.programar(clase);
    }

    @PutMapping("/{id}/entrenador/{entrenadorId}")
    public Clase asignarEntrenador(@PathVariable String id, @PathVariable String entrenadorId) {
        return claseService.asignarEntrenador(id, entrenadorId);
    }

    @GetMapping
    public List<Clase> listar() {
        return claseService.listar();
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: co/analisys/clases/model/Clase#