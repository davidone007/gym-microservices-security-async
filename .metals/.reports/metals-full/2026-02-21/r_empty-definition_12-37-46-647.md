error id: file:///C:/Users/nicol/Documents/9%20Semestre/Microservicios/Taller%20de%20Gimnasio/Microservices-GYM/microservicio-clases/src/main/java/co/analisys/clases/ClasesApplication.java:org/springframework/web/client/RestTemplate#
file:///C:/Users/nicol/Documents/9%20Semestre/Microservicios/Taller%20de%20Gimnasio/Microservices-GYM/microservicio-clases/src/main/java/co/analisys/clases/ClasesApplication.java
empty definition using pc, found symbol in pc: org/springframework/web/client/RestTemplate#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 239
uri: file:///C:/Users/nicol/Documents/9%20Semestre/Microservicios/Taller%20de%20Gimnasio/Microservices-GYM/microservicio-clases/src/main/java/co/analisys/clases/ClasesApplication.java
text:
```scala
package co.analisys.clases;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.@@RestTemplate;

@SpringBootApplication
public class ClasesApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClasesApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: org/springframework/web/client/RestTemplate#