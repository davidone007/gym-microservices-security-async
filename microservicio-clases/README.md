# Microservicio de Clases

## Descripción

El **microservicio de clases** es el núcleo coordinador del sistema. Gestiona la programación de clases del gimnasio y actúa como orquestador de las relaciones entre entrenadores, equipos y miembros. Cuando se asigna cualquiera de estas entidades a una clase, este microservicio valida su existencia consultando en tiempo real a los demás microservicios mediante HTTP.

También expone endpoints de verificación que son consultados por los microservicios de entrenadores, equipos y miembros para saber si una entidad está siendo usada antes de permitir su eliminación. Esto garantiza la integridad referencial distribuida del sistema.

---

## Tecnologías

| Tecnología       | Detalle                              |
|------------------|--------------------------------------|
| Java             | 17                                   |
| Spring Boot      | 3.x                                  |
| Spring Data JPA  | Persistencia con H2                  |
| H2 Database      | Base de datos en memoria             |
| Bean Validation  | Validaciones con Jakarta             |
| RestTemplate     | Comunicación con los demás servicios |

---

## Configuración

**Puerto:** `8082`  
**Nombre de aplicación:** `microservicio-clases`  
**Base de datos:** `jdbc:h2:mem:clasesdb`  
**Consola H2:** `http://localhost:8082/h2-console`

### Dependencias de otros microservicios

| Servicio      | URL por defecto                        |
|---------------|----------------------------------------|
| Entrenadores  | `http://localhost:8083/api/entrenadores` |
| Equipos       | `http://localhost:8084/api/equipos`      |
| Miembros      | `http://localhost:8081/api/miembros`     |

Estas URLs son configurables en `src/main/resources/application.properties`.

---

## Modelo de datos

### `Clase`

| Campo                   | Tipo               | Descripción                                          |
|-------------------------|--------------------|------------------------------------------------------|
| `id.clase_id`           | `String`           | Identificador único de la clase (PK)                 |
| `nombre`                | `String`           | Nombre de la clase (requerido)                       |
| `horario.horario`       | `LocalDateTime`    | Fecha y hora programada de la clase                  |
| `capacidadMaxima`       | `int`              | Cantidad máxima de miembros permitidos (debe ser > 0)|
| `entrenadorId.entrenador_id` | `String`      | Referencia al entrenador asignado (puede ser nula)   |
| `equipos`               | `List<EquipoId>`   | Lista de equipos asignados a la clase                |
| `miembros`              | `List<MiembroId>`  | Lista de miembros inscritos en la clase              |

---

## Endpoints

**Base URL:** `http://localhost:8082/api/clases`

### `POST /api/clases`
Programa una nueva clase. Si se incluyen entrenador, equipos o miembros en el cuerpo, se valida su existencia contra los microservicios correspondientes.

**Request body:**
```json
{
  "id": { "clase_id": "CLS-001" },
  "nombre": "Crossfit Matutino",
  "horario": { "horario": "2026-03-01T07:00:00" },
  "capacidadMaxima": 20
}
```

**Response:** `200 OK` — objeto `Clase` creado.

---

### `GET /api/clases`
Retorna la lista completa de clases programadas incluyendo entrenador, equipos y miembros asignados.

**Response:** `200 OK` — arreglo de objetos `Clase`.

---

### `PUT /api/clases/{id}/entrenador/{entrenadorId}`
Asigna un entrenador a la clase. Valida la existencia del entrenador en el microservicio de entrenadores.

**Response:** `200 OK` — objeto `Clase` actualizado.  
**Error:** `404 Not Found` si la clase o el entrenador no existen.  
**Error:** `503 Service Unavailable` si el microservicio de entrenadores no responde.

---

### `DELETE /api/clases/{id}/entrenador`
Desvincula al entrenador actual de la clase sin eliminarlo del sistema.

**Response:** `200 OK` — objeto `Clase` sin entrenador asignado.  
**Error:** `400 Bad Request` si la clase no tiene entrenador asignado.  
**Error:** `404 Not Found` si la clase no existe.

---

### `PUT /api/clases/{id}/equipos/{equipoId}`
Agrega un equipo a la lista de equipos de la clase. Valida la existencia del equipo y que no esté duplicado.

**Response:** `200 OK` — objeto `Clase` actualizado.  
**Error:** `400 Bad Request` si el equipo ya está asignado.

---

### `DELETE /api/clases/{id}/equipos/{equipoId}`
Remueve un equipo de la lista de equipos de la clase.

**Response:** `200 OK` — objeto `Clase` actualizado.  
**Error:** `400 Bad Request` si el equipo no está en la clase.

---

### `PUT /api/clases/{id}/miembros/{miembroId}`
Inscribe un miembro en la clase. Valida la existencia del miembro, que no esté duplicado y que no se haya superado la capacidad máxima.

**Response:** `200 OK` — objeto `Clase` actualizado.  
**Error:** `400 Bad Request` si la clase está llena o el miembro ya está inscrito.

---

### `DELETE /api/clases/{id}/miembros/{miembroId}`
Desinscribe un miembro de la clase.

**Response:** `200 OK` — objeto `Clase` actualizado.  
**Error:** `400 Bad Request` si el miembro no está inscrito.

---

### `DELETE /api/clases/{id}`
Elimina la clase completa del sistema.

**Response:** `204 No Content` si la eliminación fue exitosa.  
**Error:** `404 Not Found` si la clase no existe.

---

### `GET /api/clases/verificar/entrenador/{entrenadorId}`
Verifica si el entrenador está asignado a alguna clase. Usado por el microservicio de entrenadores antes de eliminar.

**Response:**
```json
{ "referenciado": true }
```

---

### `GET /api/clases/verificar/equipo/{equipoId}`
Verifica si el equipo está asignado a alguna clase. Usado por el microservicio de equipos antes de eliminar.

**Response:**
```json
{ "referenciado": false }
```

---

### `GET /api/clases/verificar/miembro/{miembroId}`
Verifica si el miembro está inscrito en alguna clase. Usado por el microservicio de miembros antes de eliminar.

**Response:**
```json
{ "referenciado": false }
```

---

## Flujo de integridad referencial

```
Eliminar entrenador/equipo/miembro
        │
        ▼
  Microservicio origen consulta:
  GET /api/clases/verificar/{entidad}/{id}
        │
  ┌─────┴──────┐
referenciado  no referenciado
  = true         = false
  │               │
  ▼               ▼
400 Error     204 Eliminado
```

---

## Manejo de errores

| Excepción                    | Código HTTP | Descripción                                               |
|------------------------------|-------------|-----------------------------------------------------------|
| `ResourceNotFoundException`  | `404`       | La clase, entrenador, equipo o miembro no existe          |
| `InvalidEntityException`     | `400`       | Regla de negocio violada (duplicado, sin entrenador, etc.)|
| `ServiceUnavailableException`| `503`       | No se pudo conectar a un microservicio dependiente        |

---

## Ejecución

```bash
cd microservicio-clases
./mvnw spring-boot:run
```

O desde la raíz del proyecto:
```bash
./mvnw spring-boot:run -pl microservicio-clases
```

> **Importante:** Para el correcto funcionamiento de las validaciones, los microservicios de miembros (8081), entrenadores (8083) y equipos (8084) deben estar en ejecución previamente.

---

## Autores

- [Andrés Cabezas](https://github.com/andrescabezas26)
- [Davide Flamini](https://github.com/davidone007)
- [Nicolas Cuellar](https://github.com/Nicolas-CM)
- [Miguel Martinez](https://github.com/Miguel-23-ing)
- [Daron Mercado](https://github.com/Ing-Daron11)