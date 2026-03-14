# monilito-gimnasio

## Levantar localmente

Se provee un script para levantar Keycloak (en Docker, importando el realm) y ejecutar los microservicios localmente con Maven.

- Ejecutar el script:

```bash
./run-services.sh
```

- Keycloak se levanta en http://localhost:8180 (usuario/admin: admin) y los microservicios se ejecutan desde Maven. Los logs de cada servicio quedan en `./logs/`.

Nota: Docker se usa únicamente para Keycloak; los microservicios se ejecutan localmente con Maven.

### Levantar Keycloak manualmente

Si prefieres levantar Keycloak manualmente (sin el script), usa este comando desde la raíz del proyecto:

```bash
docker run -d --name keycloak-gimnasio -p 8180:8080 \
	-e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
	-e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
	-v keycloak_gym_data:/opt/keycloak/data \
	-v "$(pwd)/keycloak:/opt/keycloak/data/import" \
	quay.io/keycloak/keycloak:26.5.4 start-dev --import-realm
```

Accede a la consola en `http://localhost:8180/admin`.
 
