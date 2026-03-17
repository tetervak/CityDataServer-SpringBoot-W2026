package ca.tetervak.citydata.controller;

import ca.tetervak.citydata.data.City;
import ca.tetervak.citydata.data.CityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/cities")
public class CitiesController {

    private final CityRepository cityRepository;

    public CitiesController(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @GetMapping
    public List<City> getAllCities() {
        log.trace("getAllCities() is called");
        return cityRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<City> getCityById(@PathVariable String id) {
        log.trace("getCityById() is called with id={}", id);
        return cityRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("City with id '" + id + "' not found"));
    }
}
