# Microservicio de Miembros

## Descripción

El **microservicio de miembros** es el responsable de gestionar el ciclo de vida completo de los miembros del gimnasio. Un miembro representa a una persona inscrita en el sistema que puede ser asignada a clases. Este servicio expone una API REST para registrar, consultar, actualizar y eliminar miembros, además de ofrecer un endpoint de verificación utilizado internamente por otros microservicios.

Antes de eliminar un miembro, el microservicio de clases es consultado para garantizar que el miembro no esté inscrito en ninguna clase activa. Si existe una referencia, la eliminación es rechazada con un error.

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

**Puerto:** `8081`  
**Nombre de aplicación:** `microservicio-miembros`  
**Base de datos:** `jdbc:h2:mem:miembrosdb`  
**Consola H2:** `http://localhost:8081/h2-console`

El servicio se comunica con el microservicio de clases en:
```
http://localhost:8082/api/clases
```

---

## Modelo de datos

### `Miembro`

| Campo             | Tipo        | Descripción                            |
|-------------------|-------------|----------------------------------------|
| `id.miembro_id`   | `String`    | Identificador único del miembro (PK)   |
| `nombre`          | `String`    | Nombre completo del miembro (requerido)|
| `email.email`     | `String`    | Correo electrónico del miembro         |
| `fechaInscripcion`| `LocalDate` | Fecha de inscripción al gimnasio        |

---

## Endpoints

**Base URL:** `http://localhost:8081/api/miembros`

### `POST /api/miembros`
Registra un nuevo miembro en el sistema.

**Request body:**
```json
{
  "id": { "miembro_id": "MBR-001" },
  "nombre": "Ana García",
  "email": { "email": "ana.garcia@email.com" },
  "fechaInscripcion": "2026-01-15"
}
```

**Response:** `200 OK` — objeto `Miembro` creado.

---

### `GET /api/miembros`
Retorna la lista completa de miembros registrados.

**Response:** `200 OK` — arreglo de objetos `Miembro`.

---

### `GET /api/miembros/{id}`
Obtiene los datos de un miembro específico por su ID.

**Response:** `200 OK` — objeto `Miembro`.  
**Error:** `404 Not Found` si el miembro no existe.

---

### `PUT /api/miembros/{id}`
Actualiza los datos de un miembro existente.

**Request body:** misma estructura que el POST.  
**Response:** `200 OK` — objeto `Miembro` actualizado.  
**Error:** `404 Not Found` si el miembro no existe.

---

### `DELETE /api/miembros/{id}`
Elimina un miembro del sistema.

**Response:** `204 No Content` si la eliminación fue exitosa.  
**Error:** `400 Bad Request` si el miembro está inscrito en una clase activa.  
**Error:** `404 Not Found` si el miembro no existe.

---

### `GET /api/miembros/{id}/existe`
Verifica si un miembro con el ID dado existe. Usado internamente por el microservicio de clases.

**Response:**
```json
{ "existe": true }
```

---

## Manejo de errores

| Excepción                  | Código HTTP | Descripción                                      |
|----------------------------|-------------|--------------------------------------------------|
| `ResourceNotFoundException`| `404`       | El miembro solicitado no existe                  |
| `InvalidEntityException`   | `400`       | El miembro está referenciado en una clase activa |
| `ServiceUnavailableException`| `503`     | No se pudo conectar al microservicio de clases   |

---

## Ejecución

```bash
cd microservicio-miembros
./mvnw spring-boot:run
```

O desde la raíz del proyecto:
```bash
./mvnw spring-boot:run -pl microservicio-miembros
```

---

## Autores

- [Andrés Cabezas](https://github.com/andrescabezas26)
- [Davide Flamini](https://github.com/davidone007)
- [Nicolas Cuellar](https://github.com/Nicolas-CM)
- [Miguel Martinez](https://github.com/Miguel-23-ing)
- [Daron Mercado](https://github.com/Ing-Daron11)