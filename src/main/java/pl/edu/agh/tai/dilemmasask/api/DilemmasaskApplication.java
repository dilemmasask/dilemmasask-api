package pl.edu.agh.tai.dilemmasask.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class DilemmasaskApplication {

    public static void main(String[] args) {
       // SpringApplication.run(DilemmasaskApplication.class, args)''
        new SpringApplicationBuilder(DilemmasaskApplication.class)
                .properties("spring.config.name:application-local")
                .build()
                .run(args);
    }
}
