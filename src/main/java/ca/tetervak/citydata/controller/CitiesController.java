package ca.tetervak.citydata.controller;

import ca.tetervak.citydata.data.City;
import ca.tetervak.citydata.data.CityRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/cities", produces = "application/json")
@Tag(name = "Cities", description = "Endpoints for managing cities")
public class CitiesController {

    private final CityRepository cityRepository;

    public CitiesController(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @GetMapping(produces = "application/json")
    @Operation(summary = "Retrieve all cities", description = "Returns a list of all cities")
    public List<City> getAllCities() {
        log.trace("getAllCities() is called");
        return cityRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a city by ID", description = "Returns a city with the specified ID")
    @Parameters(
            @Parameter(name = "id", description = "The ID of the city to retrieve", required = true, example = "C001")
    )
    public ResponseEntity<City> getCityById(@PathVariable String id) throws NoResourceFoundException {
        log.trace("getCityById() is called with id={}", id);
        return cityRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NoResourceFoundException(HttpMethod.GET, null, "/api/cities/" + id));
    }
}
