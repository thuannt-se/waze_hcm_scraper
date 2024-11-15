package org.thuannt.waze_hcm_scraper.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.thuannt.waze_hcm_scraper.domain.Coordinate;
import org.thuannt.waze_hcm_scraper.out.WazeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
@Slf4j
public class WazeRoutingService {
    //simple handle for retrieving data and returning it
    private final WazeClient wazeClient;
    private final String COOR_PATTERN = "x:%s y:%s";
    private static final Logger logger = LoggerFactory.getLogger(WazeRoutingService.class);

    private static final Map<String, String> HEADER_MAP = Map.of(
            "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3",
            "referer", "https://www.waze.com/"
    );


    @Async
    public CompletableFuture<InputStream> getRoutingData(String coordinateServer, Coordinate from, Coordinate to) {
        try {
            var options = Map.of(
                    "from", String.format(COOR_PATTERN, from.getLon(), from.getLat()),
                    "to", String.format(COOR_PATTERN, to.getLon(), to.getLat()),
                    "at", "1",
                    "returnJSON", "true",
                    "returnGeometries", "true",
                    "returnInstructions", "true",
                    "timeout", "60000",
                    "nPaths", "3",
                    "vehicleType", "MOTORCYCLE"
            );
            var response = wazeClient.getRoutingData(coordinateServer, options, HEADER_MAP);
            return CompletableFuture.completedFuture(response.getEntity().getContent());
        } catch (Exception e) {
            logger.error("Failed to get routing data from Waze", e);
            return CompletableFuture.completedFuture(null);
        }
    }
}
