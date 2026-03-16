# Guía de Exposición: Parte 3 - Integración de Kafka en Microservicios

Este documento te servirá como guía de estudio y material de apoyo para tu sustentación del Taller. Detalla paso a paso cómo se resolvieron los 4 puntos de la consigna sobre Kafka y cómo puedes probar el proyecto sin tener que instalar dependencias complejas manualmente.

---

## 1. Configuración de Kafka en el Proyecto

**La duda principal:** *¿Necesito instalar RabbitMQ, Keycloak o Kafka en mi computadora?*
**Respuesta:** **No.** Todo funciona mediante contenedores de **Docker**. Siempre y cuando tengas Docker Desktop (o el servicio de Docker) ejecutándose en tu PC, el entorno se configurará solo.

**¿Qué hicimos para que funcione?**
Se modificó el script de inicialización del proyecto llamado `run-services.sh`. En este script agregamos las instrucciones para descargar y ejecutar la imagen oficial de **Apache Kafka** (`apache/kafka:3.7.0`) exponiendo el puerto `9092`, exactamente de la misma forma en que tus compañeros ya habían configurado Keycloak y RabbitMQ.

* **Archivo modificado:** `run-services.sh`
* **Dependencias añadidas:** Se editó el `pom.xml` de `microservicio-clases` y `microservicio-miembros` para inyectar `spring-kafka` y `kafka-streams`. 

---

## 2. Monitoreo en Tiempo Real de Ocupación (Publish/Subscribe)

El objetivo de este requerimiento era que, cada vez que la capacidad de una clase varíe, se notifique a todo el sistema al instante para actualizar un "Dashboard" o panel visual.

**Archivos involucrados (Ubicados en `microservicio-clases/.../kafka/`):**
1. **`OcupacionClase.java`**: Es el modelo o DTO (Data Transfer Object) que define la forma de la notificación (qué clase es, la ocupación actual y cuándo se envió).
2. **`OcupacionClaseProducer.java`**: Actúa como el **Productor**. Utiliza la clase base de Spring `KafkaTemplate` para "publicar" o inyectar el mensaje convertido en JSON en el topic de Kafka llamado `ocupacion-clases`.
3. **`OcupacionClaseConsumer.java`**: Actúa como el **Consumidor**. Utiliza la anotación `@KafkaListener(topics = "ocupacion-clases")` que automáticamente queda a la escucha. Apenas llega una notificación del productor, este consumidor "reacciona" e imprime en consola (lo que simula el redibujado de un Dashboard web).
4. **`ClaseController.java`**: Agregamos un endpoint `POST /api/clases/{id}/ocupacion` que recibe vía URL la ocupación y llama al Productor.

---

## 3. Procesamiento de Streams para Datos de Entrenamiento

En lugar de procesar los mensajes uno por uno al instante (como el punto anterior), usamos **Kafka Streams** para analizar grupos de datos en un período de tiempo. La regla exige calcular resúmenes cada 7 días.

**Archivos involucrados (Ubicados en `microservicio-miembros/.../kafka/`):**
1. **`DatosEntrenamiento.java` / `ResumenEntrenamiento.java`**: Modelos de entrada y salida respectivamente. Guardan la cantidad de minutos y calorías quemadas.
2. **`KafkaStreamsConfig.java`**: Aquí está el núcleo de la analítica en tiempo real. 
   - Toma los flujos de datos entrantes desde el tópico `datos-entrenamiento`.
   - Utiliza la función `windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofDays(7)))`, agrupando matemáticamente eventos en bloques de una semana.
   - Aplica `.aggregate(...)` para ir acumulando (sumando) minuto a minuto y caloría a caloría de ese usuario durante dicha semana.
   - Termina enviando el compilado consolidado final a un nuevo topic llamado `resumen-entrenamiento`.

---

## 4. Recuperación ante Fallos (Manual Offline Tracking)

En sistemas críticos, si el microservicio se apaga de repente o experimenta un fallo eléctrico, al encender *no debe obviar* los mensajes que pasaron ni debe *repetir* mensajes que ya había contabilizado y procesado.

**Archivos involucrados:**
1. **`RecuperacionService.java`**: 
   - Anulamos el comportamiento estándar defectuoso que comete errores (llamado Auto-Commit) definiendo manualmente `props.put("enable.auto.commit", "false")`. Esto frena que avise "ya recibí" sin haber calculado primero.
   - Guardamos el "Offset" (pensemos en esto como un marca-páginas en un libro que dice en qué evento exacto íbamos leyendo).
   - En el método `iniciarProcesamiento()`, instruimos explícitamente a Kafka indicando de dónde empezar tras el fallo con: `consumer.seek(ultimoOffsetProcesado)`. Funciona de forma resiliente.

---
---

## 🏃🏽‍♂️ Paso a Paso: Cómo Probarlo Exitosamente

Para que evidencies cómo todo esto engrana sin errores durante tu presentación, debes seguir estos pasos:

### Paso 1: Preparar el Terreno
1. Abre Docker Desktop (Solo ábrelo y minimízalo, debe arrojar luz verde de "Engine Running").
2. Cierra desde VS Code cualquier microservicio encendido.
3. Detén todos los procesos desde la terminal en esta misma carpeta escribiendo:
   - Git Bash: `./stop-services.sh`
   - Si no agarra en windows, prueba: `.\stop-services.bat`

### Paso 2: Levantar el Ecosistema Completo
Desde la consola (PowerShell, Bash o la de VS Code), lanza el archivo que se modificó:
```bash
./run-services.sh
```
* **¿Qué ocurrirá bajo el agua?** El script va a encender `Keycloak`, luego `RabbitMQ`, seguido de tu super-brocker de `Kafka` y por último compilará/desplegará los 5 microservicios.

### Paso 3: Interactuar y Validar mediante Logs (En vivo)

1. Para la parte de **Asimetría/Streaming**, ve a http://localhost:8080/swagger-ui/index.html (Cambia el puerto al puerto que tenga balanceado `microservicio-clases`).
2. Despliega la pestaña `POST /api/clases/{id}/ocupacion` 
3. Envía el ID: `CLASE-123`, y Ocupacion: `45` (Haz clic en Ejecutar).
4. Mientras haces esto entra a la carpeta de logs local (`logs/microservicio-clases.log`) de tu proyecto o mira la terminal principal, y deberás ver impreso lo siguiente de manera casi instantánea comprobando todo el pipeline:
   - `✅ Mensaje enviado a Kafka (Topic: ocupacion-clases): {"claseId":"CLASE-123","ocupacionActual":45,...}`
   - `🔥 Dashboard actualizado en tiempo real: {"claseId":"CLASE-123","ocupacionActual":45,...}`

Con estos datos y pasos guiados posees todos los insumos necesarios para mostrar tu pantalla en la clase, hablar sobre la arquitectura y evidenciar tu conocimiento sobre Apache Kafka.