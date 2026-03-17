# 🏋️ Sistema de Gestión de Gimnasio — Evolución a Microservicios

Este proyecto implementa un **sistema backend para la gestión de un gimnasio**, desarrollado inicialmente como un **monolito** y posteriormente evolucionado hacia una **arquitectura de microservicios**, incorporando tecnologías modernas de seguridad, mensajería y procesamiento de eventos.

La evolución del sistema se realizó en varias etapas para mejorar:

* Escalabilidad
* Mantenibilidad
* Desacoplamiento entre servicios
* Seguridad
* Procesamiento de eventos en tiempo real

---

# 📌 Evolución de la Arquitectura

## 1️⃣ Arquitectura Inicial — Monolito

Inicialmente, el sistema fue desarrollado como una **aplicación monolítica** donde todas las funcionalidades estaban dentro de una sola aplicación.

Esto incluía:

* Gestión de miembros
* Gestión de entrenadores
* Gestión de equipos
* Programación de clases

### Problemas del enfoque monolítico

Aunque funcional, el monolito presentaba algunas limitaciones:

* Alto **acoplamiento entre módulos**
* Difícil **escalabilidad**
* Despliegue completo para cambios pequeños
* Mayor complejidad para agregar nuevas funcionalidades

Por estas razones, se decidió evolucionar el sistema hacia una **arquitectura de microservicios**.

---

# 2️⃣ Migración a Arquitectura de Microservicios

El sistema fue dividido en **múltiples microservicios independientes**, cada uno responsable de un dominio específico.

### Microservicios implementados

| Microservicio | Puerto | Responsabilidad                  |
| ------------- | ------ | -------------------------------- |
| Miembros      | 8081   | Gestión de miembros del gimnasio |
| Clases        | 8082   | Programación y gestión de clases |
| Entrenadores  | 8083   | Administración de entrenadores   |
| Equipos       | 8084   | Inventario de equipos            |

Cada microservicio:

* Tiene su **propia base de datos**
* Implementa su **lógica de negocio independiente**
* Se despliega de forma independiente

---

# 3️⃣ Comunicación entre Microservicios — APIs RESTful

Para permitir la comunicación entre servicios se implementaron **APIs RESTful** utilizando **Spring Boot**.

Los servicios se comunican mediante **HTTP**.

Ejemplo:

* El microservicio de **clases** valida la existencia de:

  * entrenadores
  * equipos
  * miembros

consultando los otros microservicios.

### Ejemplo de flujo

```
Microservicio Clases
       │
       ▼
GET /api/entrenadores/{id}/existe
       │
       ▼
Microservicio Entrenadores
```

Esto permite mantener **integridad referencial distribuida** entre servicios.

---

# 4️⃣ Seguridad con Keycloak

Posteriormente se agregó un sistema de **seguridad centralizado** utilizando **Keycloak**.

Keycloak actúa como **servidor de autenticación e identidad** del sistema.

### Funcionalidades implementadas

* Autenticación de usuarios
* Autorización basada en roles
* Generación de **tokens JWT**
* Protección de endpoints

### Roles definidos

| Rol          | Descripción                    |
| ------------ | ------------------------------ |
| ROLE_ADMIN   | Administración del sistema     |
| ROLE_TRAINER | Gestión de clases              |
| ROLE_MEMBER  | Acceso a información de clases |

### Flujo de autenticación

```
Usuario
   │
Login
   │
   ▼
Keycloak
   │
Genera JWT
   │
   ▼
Microservicios validan el token
```

Esto permite **centralizar la seguridad del sistema**.

---

# 5️⃣ Comunicación Asincrónica con RabbitMQ

Posteriormente se incorporó mensajería asincrónica utilizando **RabbitMQ**.

RabbitMQ permite que los microservicios se comuniquen **mediante colas de mensajes**, evitando acoplamiento directo.

### Casos implementados

RabbitMQ se utiliza para:

* Procesamiento de **pagos**
* Manejo de **inscripciones a clases**
* Envío de **notificaciones cuando cambia el horario de una clase**

### Ejemplo de flujo

```
Servicio
  │
  ▼
RabbitMQ Queue
  │
  ▼
Consumidor procesa el mensaje
```

Esto permite ejecutar procesos en **segundo plano** sin bloquear las APIs.

---

# 6️⃣ Microservicio de Pagos

Se implementó un **microservicio adicional** para manejar el procesamiento de pagos.

### Características

* Procesamiento asincrónico de pagos
* Integración con RabbitMQ
* Manejo de errores mediante **Dead Letter Queue**

### Colas implementadas

| Cola        | Descripción                     |
| ----------- | ------------------------------- |
| pagos-queue | Cola principal de procesamiento |
| pagos-dlq   | Cola para mensajes fallidos     |

### Flujo

```
Cliente → API Pagos → pagos-queue → Procesador de pagos
                                      │
                                      ▼
                                  pagos-dlq (error)
```

Esto permite **manejar errores sin perder mensajes**.

---

# 7️⃣ Streaming de Eventos con Kafka

Finalmente se integró **Apache Kafka** para manejar **eventos en tiempo real**.

Kafka permite implementar una **arquitectura basada en eventos (Event-Driven Architecture)**.

### Casos implementados

1️⃣ Monitoreo de **ocupación de clases**
2️⃣ Procesamiento de **datos de entrenamiento de los miembros**

### Ejemplo de flujo

```
Servicio produce evento
        │
        ▼
Kafka Topic
        │
        ▼
Consumer procesa evento
```

### Ventajas

* Procesamiento de datos en **tiempo real**
* Alta **escalabilidad**
* Desacoplamiento entre servicios
* Persistencia de eventos mediante logs

### Diagrama de Arquitectura

![Deployment microservices](/keycloak/Deployment%20microservices.jpg)

### Swagger/OpenAPI

![Swagger](/keycloak/Swagger.png)

![Swagger-Miembros](/keycloak/end-point.png)


---

# 🧰 Tecnologías Utilizadas

| Tecnología        | Uso                          |
| ----------------- | ---------------------------- |
| Java 17           | Lenguaje principal           |
| Spring Boot       | Framework de microservicios  |
| Spring Security   | Seguridad                    |
| Keycloak          | Autenticación y autorización |
| RabbitMQ          | Mensajería asincrónica       |
| Kafka             | Streaming de eventos         |
| H2 Database       | Base de datos en memoria     |
| Swagger / OpenAPI | Documentación de APIs        |
| Docker            | Ejecución de Keycloak        |

---

# 🚀 Levantar el Proyecto Localmente

Se provee un script para levantar **Keycloak** y ejecutar los microservicios.

### Ejecutar el script

```bash
./run-services.sh
```

Esto realizará:

* Inicio de **Keycloak en Docker**
* Ejecución de los **microservicios con Maven**

### Accesos

| Servicio     | URL                                            |
| ------------ | ---------------------------------------------- |
| Keycloak     | [http://localhost:8180](http://localhost:8180) |
| Miembros     | [http://localhost:8081](http://localhost:8081) |
| Clases       | [http://localhost:8082](http://localhost:8082) |
| Entrenadores | [http://localhost:8083](http://localhost:8083) |
| Equipos      | [http://localhost:8084](http://localhost:8084) |
| Pagos        | [http://localhost:8085](http://localhost:8085) |

Logs disponibles en:

```
/logs
```

---

# ⚙️ Levantar Keycloak Manualmente

Si prefieres levantar Keycloak manualmente:

```bash
docker run -d --name keycloak-gimnasio -p 8180:8080 \
 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
 -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
 -v keycloak_gym_data:/opt/keycloak/data \
 -v "$(pwd)/keycloak:/opt/keycloak/data/import" \
 quay.io/keycloak/keycloak:26.5.4 start-dev --import-realm
```

Panel de administración:

```
http://localhost:8180/admin
```

Credenciales:

```
usuario: admin
contraseña: admin
```

---

# 👥 Autores

* Andrés Cabezas
* Davide Flamini
* Nicolas Cuellar
* Miguel Martinez
* Daron Mercado
