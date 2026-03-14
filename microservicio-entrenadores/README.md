# Microservicio de Entrenadores

## Descripción

El **microservicio de entrenadores** gestiona el registro y administración de los entrenadores del gimnasio. Un entrenador puede ser asignado a una clase para dirigirla. Este servicio expone una API REST para crear, consultar, actualizar y eliminar entrenadores, y ofrece un endpoint de verificación de existencia utilizado por el microservicio de clases.

Antes de eliminar un entrenador, el servicio consulta al microservicio de clases para comprobar que el entrenador no esté asignado a ninguna clase activa. Si existe una referencia, la eliminación se rechaza con un error descriptivo.

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

**Puerto:** `8083`  
**Nombre de aplicación:** `microservicio-entrenadores`  
**Base de datos:** `jdbc:h2:mem:entrenadoresdb`  
**Consola H2:** `http://localhost:8083/h2-console`

El servicio se comunica con el microservicio de clases en:
```
http://localhost:8082/api/clases
```

---

## Modelo de datos

### `Entrenador`

| Campo                   | Tipo     | Descripción                                   |
|-------------------------|----------|-----------------------------------------------|
| `id.entrenador_id`      | `String` | Identificador único del entrenador (PK)       |
| `nombre`                | `String` | Nombre completo del entrenador (requerido)    |
| `especialidad.especialidad` | `String` | Área de especialidad (ej. Crossfit, Yoga) |

---

## Endpoints

**Base URL:** `http://localhost:8083/api/entrenadores`

### `POST /api/entrenadores`
Registra un nuevo entrenador en el sistema.

**Request body:**
```json
{
  "id": { "entrenador_id": "ENT-001" },
  "nombre": "Carlos Ruiz",
  "especialidad": { "especialidad": "Crossfit" }
}
```

**Response:** `200 OK` — objeto `Entrenador` creado.

---

### `GET /api/entrenadores`
Retorna la lista completa de entrenadores registrados.

**Response:** `200 OK` — arreglo de objetos `Entrenador`.

---

### `GET /api/entrenadores/{id}`
Obtiene los datos de un entrenador específico por su ID.

**Response:** `200 OK` — objeto `Entrenador`.  
**Error:** `404 Not Found` si el entrenador no existe.

---

### `PUT /api/entrenadores/{id}`
Actualiza los datos de un entrenador existente.

**Request body:** misma estructura que el POST.  
**Response:** `200 OK` — objeto `Entrenador` actualizado.  
**Error:** `404 Not Found` si el entrenador no existe.

---

### `DELETE /api/entrenadores/{id}`
Elimina un entrenador del sistema.

**Response:** `204 No Content` si la eliminación fue exitosa.  
**Error:** `400 Bad Request` si el entrenador está asignado a una clase activa.  
**Error:** `404 Not Found` si el entrenador no existe.

> **Nota:** Para eliminar un entrenador que está asignado a una clase, primero debe quitarse de la clase mediante `DELETE /api/clases/{id}/entrenador`.

---

### `GET /api/entrenadores/{id}/existe`
Verifica si un entrenador con el ID dado existe. Usado internamente por el microservicio de clases.

**Response:**
```json
{ "existe": true }
```

---

## Manejo de errores

| Excepción                    | Código HTTP | Descripción                                         |
|------------------------------|-------------|-----------------------------------------------------|
| `ResourceNotFoundException`  | `404`       | El entrenador solicitado no existe                  |
| `InvalidEntityException`     | `400`       | El entrenador está referenciado en una clase activa |
| `ServiceUnavailableException`| `503`       | No se pudo conectar al microservicio de clases      |

---

## Ejecución

```bash
cd microservicio-entrenadores
./mvnw spring-boot:run
```

O desde la raíz del proyecto:
```bash
./mvnw spring-boot:run -pl microservicio-entrenadores
```

---

## Autores

- [Andrés Cabezas](https://github.com/andrescabezas26)
- [Davide Flamini](https://github.com/davidone007)
- [Nicolas Cuellar](https://github.com/Nicolas-CM)
- [Miguel Martinez](https://github.com/Miguel-23-ing)
- [Daron Mercado](https://github.com/Ing-Daron11)