# monilito-gimnasio

## Levantar localmente

Se provee un script para levantar Keycloak (en Docker, importando el realm) y ejecutar los microservicios localmente con Maven.

- Ejecutar el script:

```bash
./run-services.sh
```

- Keycloak se levanta en http://localhost:8180 (usuario/admin: admin) y los microservicios se ejecutan desde Maven. Los logs de cada servicio quedan en `./logs/`.

Nota: Docker se usa únicamente para Keycloak; los microservicios se ejecutan localmente con Maven.
 
