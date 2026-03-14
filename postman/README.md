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
