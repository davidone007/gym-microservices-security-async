# Configuración Keycloak - Realm Gimnasio

## Importar el realm

### Opción 1: Import al iniciar Keycloak (recomendado)

1. Copia `gimnasio-realm.json` a la carpeta de import de Keycloak:
  - **Local**: copia a `<KEYCLOAK_HOME>/data/import/gimnasio-realm.json`

2. Inicia Keycloak con importación (Docker recomendado para desarrollo local):
   ```bash
   docker run -d --name keycloak-gimnasio -p 8180:8080 \
     -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
     -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
     -v keycloak_gym_data:/opt/keycloak/data \
     -v "$(pwd)/keycloak:/opt/keycloak/data/import" \
     quay.io/keycloak/keycloak:26.5.4 start-dev --import-realm
   ```

### Opción 2: Consola de administración

1. Inicia Keycloak y entra en la consola (ej. http://localhost:8180/admin).
2. Crea un nuevo realm: **Create realm**.
3. Pulsa **Browse** y selecciona `gimnasio-realm.json`, luego **Create**.

### Opción 3: CLI sin levantar el servidor

```bash
bin/kc.sh import --file /ruta/a/keycloak/gimnasio-realm.json
```

## Contenido del realm

- **Realm**: `gimnasio`
- **Roles**: `ROLE_ADMIN`, `ROLE_TRAINER`, `ROLE_MEMBER`
- **Cliente** `gym-backend`:
  - **Protocol**: openid-connect
  - **Access Type**: confidential (`public-client: false`)
  - **Direct Access Grants Enabled**: ON
  - **Service Accounts Enabled**: ON
  - **Standard Flow Enabled**: ON
  - Demás opciones en OFF (implicit flow, frontchannel logout, etc.)
  - **Secret del cliente** (en el JSON y en `application.properties` de cada microservicio): `g7nM2pQ5rT8vX1yB4cF0hJ3kL6nP9sU`
- **Usuarios de prueba**: todos con `enabled: true`, credenciales tipo `password`, `temporary: false` y realm roles asignados:

| Usuario  | Contraseña  | Rol         |
|----------|-------------|-------------|
| admin    | admin123    | ROLE_ADMIN  |
| trainer  | trainer123  | ROLE_TRAINER |
| member   | member123   | ROLE_MEMBER |

## Obtener un JWT (para pruebas)

El cliente `gym-backend` es **confidential**: hay que enviar `client_id` y `client_secret`:

```bash
# Ejemplo con usuario admin (reemplaza username/password por trainer o member si quieres otros roles)
curl -X POST "http://localhost:8180/realms/gimnasio/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin" \
  -d "password=admin123" \
  -d "grant_type=password" \
  -d "client_id=gym-backend" \
  -d "client_secret=g7nM2pQ5rT8vX1yB4cF0hJ3kL6nP9sU"
```

La respuesta incluye `access_token`. Úsalo en las peticiones a los microservicios:

```bash
curl -H "Authorization: Bearer <ACCESS_TOKEN>" http://localhost:8081/api/miembros
```
