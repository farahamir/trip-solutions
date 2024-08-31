package tdr.solutions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TripSolutionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TripSolutionsApplication.class, args);
    }

}
