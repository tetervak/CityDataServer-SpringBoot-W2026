package ca.tetervak.citydata.data;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;

@Slf4j
@Component
public class DatabaseInitializer {

    @Value("classpath:data/city-data.json")
    private Resource resourceFile;

    private final CityRepository cityRepository;
    private final ObjectMapper objectMapper;


    public DatabaseInitializer(
            CityRepository cityRepository,
            ObjectMapper objectMapper
    ) {
        this.cityRepository = cityRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        if (cityRepository.count() == 0) {
            try {
                InputStream inputStream = resourceFile.getInputStream();
                CityData cityData = objectMapper.readValue(inputStream, CityData.class);
                cityRepository.saveAll(cityData.getCities());
            } catch (java.io.IOException e) {
                throw new RuntimeException("Failed to initialize database from JSON file", e);
            }
            log.info("Database initialized with {} cities", cityRepository.count());
        }
    }

}
