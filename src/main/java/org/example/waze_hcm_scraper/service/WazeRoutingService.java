package org.example.waze_hcm_scraper.service;

import lombok.AllArgsConstructor;
import org.example.waze_hcm_scraper.config.WazeConfiguration;
import org.example.waze_hcm_scraper.out.WazeClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class WazeRoutingService {
    //simple handle for retrieving data and returning it
    private final WazeConfiguration wazeConfiguration;;
    private final WazeClient wazeClient;
    private static final Map<String, String> HEADER_MAP = Map.of(
            "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3",
            "referer", "https://www.waze.com/"
    );

    public String getRoutingData(String coordinateServer, Map<String, String> options) {
        try {
            var response = wazeClient.getRoutingData(coordinateServer, options, HEADER_MAP);
            var body = response.getEntity().getContent();
            return wazeClient.getRoutingData(coordinateServer, options, HEADER_MAP);
        } catch (Exception e) {
            return "Failed to get routing data from Waze";
        }
    }
}
