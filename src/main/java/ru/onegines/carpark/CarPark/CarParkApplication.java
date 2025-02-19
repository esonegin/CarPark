package ru.onegines.carpark.CarPark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.onegines.carpark.CarPark.models.Brand;

@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
public class CarParkApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarParkApplication.class, args);
	}
}
