error id: file:///C:/Users/nicol/Documents/9%20Semestre/Microservicios/Taller%20de%20Gimnasio/Microservices-GYM/src/main/java/co/analisys/gimnasio/controller/GimnasioController.java:co/analisys/gimnasio/model/Miembro#
file:///C:/Users/nicol/Documents/9%20Semestre/Microservicios/Taller%20de%20Gimnasio/Microservices-GYM/src/main/java/co/analisys/gimnasio/controller/GimnasioController.java
empty definition using pc, found symbol in pc: co/analisys/gimnasio/model/Miembro#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 210
uri: file:///C:/Users/nicol/Documents/9%20Semestre/Microservicios/Taller%20de%20Gimnasio/Microservices-GYM/src/main/java/co/analisys/gimnasio/controller/GimnasioController.java
text:
```scala
package co.analisys.gimnasio.controller;

import co.analisys.gimnasio.model.Clase;
import co.analisys.gimnasio.model.Entrenador;
import co.analisys.gimnasio.model.Equipo;
import co.analisys.gimnasio.model.@@Miembro;
import co.analisys.gimnasio.service.GimnasioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gimnasio")
public class GimnasioController {
    @Autowired
    private GimnasioService gimnasioService;

    @PostMapping("/miembros")
    public Miembro registrarMiembro(@RequestBody Miembro miembro) {
        return gimnasioService.registrarMiembro(miembro);
    }

    @PostMapping("/clases")
    public Clase programarClase(@RequestBody Clase clase) {
        return gimnasioService.programarClase(clase);
    }

    @PostMapping("/entrenadores")
    public Entrenador agregarEntrenador(@RequestBody Entrenador entrenador) {
        return gimnasioService.agregarEntrenador(entrenador);
    }

    @PostMapping("/equipos")
    public Equipo agregarEquipo(@RequestBody Equipo equipo) {
        return gimnasioService.agregarEquipo(equipo);
    }

    @GetMapping("/miembros")
    public List<Miembro> obtenerTodosMiembros() {
        return gimnasioService.obtenerTodosMiembros();
    }

    @GetMapping("/clases")
    public List<Clase> obtenerTodasClases() {
        return gimnasioService.obtenerTodasClases();
    }

    @GetMapping("/entrenadores")
    public List<Entrenador> obtenerTodosEntrenadores() {
        return gimnasioService.obtenerTodosEntrenadores();
    }

    @GetMapping("/equipos")
    public List<Equipo> obtenerTodosEquipos() {
        return gimnasioService.obtenerTodosEquipos();
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: co/analisys/gimnasio/model/Miembro#