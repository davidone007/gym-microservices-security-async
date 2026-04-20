# Colección Postman - Pruebas de Seguridad JWT

## Uso

1. **Importar** en Postman: `File → Import` y selecciona `Gimnasio-Seguridad-JWT.postman_collection.json`.
2. **Requisitos**: Keycloak corriendo en `http://localhost:8180` con el realm `gimnasio` importado; los cuatro microservicios en ejecución (miembros 8081, clases 8082, entrenadores 8083, equipos 8084).
3. **Orden recomendado**:
   - Ejecutar la carpeta **0 - Keycloak - Obtener tokens** (los tres requests) para poblar `token_admin`, `token_trainer` y `token_member`.
   - Luego **1 - Acceso con token válido** para comprobar 200 con JWT válido.
   - **2 - Token inválido o sin token** para comprobar 401.
   - **3 - Autorización por roles** para comprobar 403 cuando el rol no tiene permiso.

## Contenido de la colección

| Carpeta | Objetivo |
|--------|----------|
| **0 - Keycloak - Obtener tokens** | Obtiene JWT para admin, trainer y member; los guarda en variables de colección. |
| **1 - Acceso con token válido** | GET/POST con token válido; esperado 200/201. Incluye crear miembro (ADMIN) y programar clase (TRAINER). |
| **2 - Token inválido o sin token** | Sin Authorization, Bearer vacío, token inválido o malformado; esperado **401**. |
| **3 - Autorización por roles (403)** | POST/DELETE con token MEMBER o TRAINER donde solo ADMIN tiene permiso; esperado **403**. |
| **4 - Resumen por servicio** | GET a los cuatro servicios con token_admin para verificar conectividad. |

## Variables de colección

Puedes editarlas en la pestaña *Variables* de la colección:

- `keycloak_url`, `realm`, `client_id`, `client_secret`: para obtener tokens.
- `miembros_url`, `entrenadores_url`, `equipos_url`, `clases_url`: bases de los microservicios.
- `token_admin`, `token_trainer`, `token_member`: se rellenan al ejecutar los requests de la carpeta 0.

## Respuestas esperadas

- **200/201**: Petición autorizada y exitosa.
- **401 Unauthorized**: Sin token, token vacío, inválido o expirado.
- **403 Forbidden**: Token válido pero el rol no tiene permiso para la acción.

---

## Colección adicional para taller de API Gateway

Se agregó la colección `API Gateway - Taller Completo.postman_collection.json` para validar de punta a punta los 5 puntos del taller:

1. Infraestructura disponible (Eureka + Gateway)
2. Obtención de token JWT (admin)
3. Enrutamiento por Gateway (`/miembros`, `/clases`, `/entrenadores`, `/equipos`)
4. Carga de datos de pagos para agregación
5. Filtro de agregación (`/resumen/miembros/{miembroId}`)

### Orden recomendado de ejecución (Run Collection)

1. `0 - Infra Smoke`
2. `1 - Keycloak Tokens`
3. `2 - Gateway Routing`
4. `3 - Pagos data for aggregation`
5. `4 - Aggregation Filter`

Si la variable `miembro_id` no existe en tus datos, ejecuta primero `GET miembros via gateway`; ese request toma automáticamente el primer miembro retornado y guarda su id en la variable de colección.
