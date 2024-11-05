package org.example.waze_hcm_scraper.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.waze_hcm_scraper.domain.Coordinate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "waze")
public class WazeConfiguration {
    private String httpUri;
    private List<String> vehicleType;
    private Map<String, Coordinate> baseCoord;
    private List<Map<String, Coordinate>> tripCoord;
    private Map<String, String> coordServers;
    private Map<String, String> routingServers;

}
