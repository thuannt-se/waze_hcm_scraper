package org.example.waze_hcm_scraper.config;

import lombok.Data;
import org.example.waze_hcm_scraper.domain.Coordinate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "trip")
public class TripData {
    private Map<String, Coordinate> northSouth;
    private Map<String, Coordinate> eastWest;
}
