# Microservicio de Equipos

## Descripción

El **microservicio de equipos** administra el inventario de equipos y materiales disponibles en el gimnasio. Un equipo representa cualquier implemento físico (mancuernas, barras, colchonetas, etc.) que puede ser asignado a una clase para su uso. Este servicio expone una API REST para registrar, consultar, actualizar y eliminar equipos, además de un endpoint de verificación de existencia consumido por el microservicio de clases.

Antes de eliminar un equipo, el servicio verifica con el microservicio de clases que el equipo no esté asignado a ninguna clase activa. De estarlo, la eliminación es rechazada.

---

## Tecnologías

| Tecnología       | Detalle                        |
|------------------|--------------------------------|
| Java             | 17                             |
| Spring Boot      | 3.x                            |
| Spring Data JPA  | Persistencia con H2            |
| H2 Database      | Base de datos en memoria       |
| Bean Validation  | Validaciones con Jakarta       |
| RestTemplate     | Comunicación con microservicios|

---

## Configuración

**Puerto:** `8084`  
**Nombre de aplicación:** `microservicio-equipos`  
**Base de datos:** `jdbc:h2:mem:equiposdb`  
**Consola H2:** `http://localhost:8084/h2-console`

El servicio se comunica con el microservicio de clases en:
```
http://localhost:8082/api/clases
```

---

## Modelo de datos

### `Equipo`

| Campo                   | Tipo     | Descripción                                       |
|-------------------------|----------|---------------------------------------------------|
| `id.equipo_id`          | `String` | Identificador único del equipo (PK)               |
| `nombre`                | `String` | Nombre del equipo (requerido)                     |
| `descripcion.descripcion` | `String` | Descripción detallada del equipo                |
| `cantidad`              | `int`    | Cantidad disponible en el gimnasio (debe ser > 0) |

---

## Endpoints

**Base URL:** `http://localhost:8084/api/equipos`

### `POST /api/equipos`
Registra un nuevo equipo en el sistema.

**Request body:**
```json
{
  "id": { "equipo_id": "EQP-001" },
  "nombre": "Mancuernas",
  "descripcion": { "descripcion": "Set de mancuernas de 5 a 30 kg" },
  "cantidad": 10
}
```

**Response:** `200 OK` — objeto `Equipo` creado.

---

### `GET /api/equipos`
Retorna la lista completa de equipos registrados.

**Response:** `200 OK` — arreglo de objetos `Equipo`.

---

### `GET /api/equipos/{id}`
Obtiene los datos de un equipo específico por su ID.

**Response:** `200 OK` — objeto `Equipo`.  
**Error:** `404 Not Found` si el equipo no existe.

---

### `PUT /api/equipos/{id}`
Actualiza los datos de un equipo existente.

**Request body:** misma estructura que el POST.  
**Response:** `200 OK` — objeto `Equipo` actualizado.  
**Error:** `404 Not Found` si el equipo no existe.

---

### `DELETE /api/equipos/{id}`
Elimina un equipo del sistema.

**Response:** `204 No Content` si la eliminación fue exitosa.  
**Error:** `400 Bad Request` si el equipo está asignado a una clase activa.  
**Error:** `404 Not Found` si el equipo no existe.

> **Nota:** Para eliminar un equipo que está en una clase, primero debe removerse mediante `DELETE /api/clases/{id}/equipos/{equipoId}`.

---

### `GET /api/equipos/{id}/existe`
Verifica si un equipo con el ID dado existe. Usado internamente por el microservicio de clases.

**Response:**
```json
{ "existe": true }
```

---

## Manejo de errores

| Excepción                    | Código HTTP | Descripción                                      |
|------------------------------|-------------|--------------------------------------------------|
| `ResourceNotFoundException`  | `404`       | El equipo solicitado no existe                   |
| `InvalidEntityException`     | `400`       | El equipo está referenciado en una clase activa  |
| `ServiceUnavailableException`| `503`       | No se pudo conectar al microservicio de clases   |

---

## Ejecución

```bash
cd microservicio-equipos
./mvnw spring-boot:run
```

O desde la raíz del proyecto:
```bash
./mvnw spring-boot:run -pl microservicio-equipos
```

---

## Autores

- [Andrés Cabezas](https://github.com/andrescabezas26)
- [Davide Flamini](https://github.com/davidone007)
- [Nicolas Cuellar](https://github.com/Nicolas-CM)
- [Miguel Martinez](https://github.com/Miguel-23-ing)
- [Daron Mercado](https://github.com/Ing-Daron11)