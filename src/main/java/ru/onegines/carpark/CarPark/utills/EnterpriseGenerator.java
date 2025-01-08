package ru.onegines.carpark.CarPark.utills;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.shell.standard.ShellComponent;
import ru.onegines.carpark.CarPark.models.Driver;
import ru.onegines.carpark.CarPark.services.EnterpriseService;

import java.util.Random;

/**
 * @author onegines
 * @date 18.11.2024
 */
@ShellComponent
public class EnterpriseGenerator {

    private static final Random RANDOM = new Random();
    private static final String[] CAR_BRANDS = {"Suzuki", "Toyota", "Жигули"};
    private static final String[] DRIVER_NAMES = {"Ivan", "Maria", "Alexey", "Sofia", "Dmitry"};
    private static final String[] ENTERPRISE_CITY = {"Москва", "Осло", "Антверпен"};
    private static EnterpriseService enterpriseService;


    private ObjectMapper objectMapper; // Для преобразования в JSON


    public EnterpriseGenerator(EnterpriseService enterpriseService, ObjectMapper objectMapper) {
        this.enterpriseService = enterpriseService;
        this.objectMapper = objectMapper;
    }

  /*  @ShellMethod(key = "generate-enterprises", value = "Генерация предприятий с машинами и водителями")
    public String generateEnterprises(int enterpriseCount, int carCountPerEnterprise) {
        // Генерация списка предприятий
        List<Enterprise> enterprises = enterpriseService.generateEnterprises(enterpriseCount, carCountPerEnterprise);

        // Преобразование в EnterpriseDTO
        List<EnterpriseDTO> enterpriseDTOs = enterprises.stream()
                .map(EnterpriseMapper::toDTO)
                .collect(Collectors.toList());

        try {
            // Преобразование списка DTO в JSON
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(enterpriseDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка преобразования в JSON";
        }
    }*/

 /*   @ShellMethod(key = "show-generated-cars", value = "Show cars generated by generate-enterprises")
    public Page<Car> showGeneratedCars(@ShellOption(defaultValue = "0") int page,
                                       @ShellOption(defaultValue = "20") int size) {
        // Создание объекта Pageable
        Pageable pageable = PageRequest.of(page, size);

        Page<Car> cars = enterpriseService.getGeneratedCars(pageable);
        cars.forEach(car -> {
            System.out.println("Car ID: " + car.getCarId() +
                    ", Number: " + car.getNumber() +
                    ", Active Driver ID: " + (car.getActiveDriver() != null ? car.getActiveDriver().getId() : "None"));
        });
        return cars;
    }*/

    private Driver generateDriver() {
        String name = DRIVER_NAMES[RANDOM.nextInt(DRIVER_NAMES.length)];
        int salary = RANDOM.nextInt(200000);
        String enterprise = ENTERPRISE_CITY[RANDOM.nextInt(ENTERPRISE_CITY.length)];
        return new Driver(name, salary, enterprise);
    }


}
