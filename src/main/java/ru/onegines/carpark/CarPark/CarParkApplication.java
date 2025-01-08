package ru.onegines.carpark.CarPark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.onegines.carpark.CarPark.models.Brand;

@SpringBootApplication
@EnableJpaRepositories
public class CarParkApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarParkApplication.class, args);
	}
}
