package ca.tetervak.citydata.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class City {
    @Id @NotBlank
    private String cityId;

    @NotBlank
    private String name;

    @Min(0)
    private int population;

    @JsonProperty("capital")
    private boolean isCapital;

    @Min(0)
    private double area;

    @NotBlank
    private String country;
}
