Microservicio Pagos
===================

Microservicio de ejemplo para procesar pagos con RabbitMQ y DLQ.

Uso rápido:

- Iniciar RabbitMQ local (por ejemplo con `docker run -d --hostname rmq --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management`)
- Construir e iniciar el microservicio:

```bash
cd microservicio-pagos
mvn spring-boot:run
```

- Enviar un pago (ejemplo curl):

```bash
curl -X POST http://localhost:8085/pagos -H "Content-Type: application/json" -d '{"miembroId":"m1","amount":100.0}'
```

Mensajería:

- Cola principal: `pagos-queue` (durable, TTL 30s)
- Dead Letter Queue: `pagos-dlq`
