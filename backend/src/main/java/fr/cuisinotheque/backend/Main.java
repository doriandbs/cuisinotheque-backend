
package fr.cuisinotheque.backend;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("fr.cuisinotheque.data.repositories")
@EntityScan("fr.cuisinotheque.data.entities")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }



}
