package ca.tetervak.citydata.controller;

import ca.tetervak.citydata.data.City;
import ca.tetervak.citydata.data.CityRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
    @PreAuthorize("hasAuthority('SCOPE_read') and hasAnyRole('USER', 'ADMIN')")
    public List<City> getAllCities() {
        log.trace("getAllCities() is called");
        return cityRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a city by ID", description = "Returns a city with the specified ID")
    @Parameters(
            @Parameter(name = "id", description = "The ID of the city to retrieve", required = true, example = "C001")
    )
    @PreAuthorize("hasAuthority('SCOPE_read') and hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<City> getCityById(@PathVariable String id) throws NoResourceFoundException {
        log.trace("getCityById() is called with id={}", id);
        return cityRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NoResourceFoundException(HttpMethod.GET, null, "/api/cities/" + id));
    }

    @PostMapping(produces = "application/json")
    @PreAuthorize("hasAuthority('SCOPE_write') and hasRole('ADMIN')")
    @Operation(summary = "Create a new city", description = "Creates a new city entry")
    public City createCity(@RequestBody @Valid City city) {
        log.trace("createCity() is called with city={}", city);
        return cityRepository.save(city);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_write') and hasRole('ADMIN')")
    @Operation(summary = "Update an existing city", description = "Updates an existing city entry")
    public ResponseEntity<City> updateCity(@PathVariable String id, @Valid @RequestBody  City city)
     throws  NoResourceFoundException {
        log.trace("updateCity() is called with id={} and city={}", id, city);
        return cityRepository.findById(id)
                .map(existingCity -> {
                    existingCity.setName(city.getName());
                    existingCity.setPopulation(city.getPopulation());
                    existingCity.setCapital(city.isCapital());
                    existingCity.setArea(city.getArea());
                    existingCity.setCountry(city.getCountry());
                    City updatedCity = cityRepository.save(existingCity);
                    return ResponseEntity.ok(updatedCity);
                })
                .orElseThrow(() -> new NoResourceFoundException(HttpMethod.GET, null, "/api/cities/" + id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_delete') and hasRole('ADMIN')")
    @Operation(summary = "Delete a city by ID", description = "Deletes a city with the specified ID")
    public  ResponseEntity<Void> deleteCity(@PathVariable String id) throws  NoResourceFoundException {
        log.trace("deleteCity() is called with id={}", id);
        if (cityRepository.existsById(id)) {
            cityRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
          throw  new NoResourceFoundException(HttpMethod.GET, null, "/api/cities/" + id);
        }
    }
}
