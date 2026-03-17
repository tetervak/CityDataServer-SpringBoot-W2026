package ca.tetervak.citydata.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class City {
    @Id
    private String cityId;
    private String name;
    private int population;
    private boolean isCapital;
    private double area;
    private String country;
}
