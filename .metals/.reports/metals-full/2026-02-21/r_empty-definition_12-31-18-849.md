error id: file:///C:/Users/nicol/Documents/9%20Semestre/Microservicios/Taller%20de%20Gimnasio/Microservices-GYM/microservicio-clases/src/main/java/co/analisys/clases/ClasesApplication.java:_empty_/SpringApplication#run#
file:///C:/Users/nicol/Documents/9%20Semestre/Microservicios/Taller%20de%20Gimnasio/Microservices-GYM/microservicio-clases/src/main/java/co/analisys/clases/ClasesApplication.java
empty definition using pc, found symbol in pc: _empty_/SpringApplication#run#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 277
uri: file:///C:/Users/nicol/Documents/9%20Semestre/Microservicios/Taller%20de%20Gimnasio/Microservices-GYM/microservicio-clases/src/main/java/co/analisys/clases/ClasesApplication.java
text:
```scala
package co.analisys.clases;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClasesApplication {
    public static void main(String[] args) {
        SpringApplication.@@run(ClasesApplication.class, args);
    }

    // bean necesario para llamadas REST a otros microservicios
    @org.springframework.context.annotation.Bean
    public org.springframework.web.client.RestTemplate restTemplate() {
        return new org.springframework.web.client.RestTemplate();
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/SpringApplication#run#