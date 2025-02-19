package ru.onegines.carpark.CarPark.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.onegines.carpark.CarPark.models.Brand;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.repositories.BrandRepository;

import java.util.List;
import java.util.Optional;


/**
 * @author onegines
 * @date 30.10.2024
 */
@Service
@Transactional(readOnly = true)
public class BrandService {
    private final BrandRepository brandRepository;

    @Autowired
    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    public Brand findById(long id) {
        Optional<Brand> brand = brandRepository.findById(id);
        return brand.orElse(null);
    }

    @Transactional
    public void save(Brand brand) {
        brandRepository.save(brand);
    }

    @Transactional
    public void update(Long id, Brand updatedBrand){
        updatedBrand.setId(id);
        brandRepository.save(updatedBrand);
    }

    @Transactional
    public void delete(long id){
        brandRepository.deleteById(id);
    }
}
