package com.example.pumps.producer;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring component that reads the comma-delimited {@code city.names} property
 * and exposes the parsed city list to other beans.
 *
 * <p>Example property value:
 * {@code city.names=Mumbai,Delhi,Bangalore,Hyderabad,Chennai}
 */
@Component
public class CityProvider {

    /**
     * Raw comma-delimited city names injected from {@code application.yml}.
     */
    @Value("${city.names}")
    private String cityNamesRaw;

    /** Parsed, immutable list of city names. */
    private List<String> cityList;

    /**
     * Parses the raw property string into a trimmed list of city names.
     * Called automatically by Spring after dependency injection.
     */
    @PostConstruct
    public void parseCityNames() {
        cityList = Arrays.stream(cityNamesRaw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns the list of available city names.
     *
     * @return unmodifiable list of city names
     */
    public List<String> getCityList() {
        return cityList;
    }
}
