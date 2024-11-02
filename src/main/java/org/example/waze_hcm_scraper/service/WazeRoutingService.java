package org.example.waze_hcm_scraper.service;

import lombok.AllArgsConstructor;
import org.example.waze_hcm_scraper.config.WazeConfiguration;
import org.example.waze_hcm_scraper.out.WazeClient;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WazeRoutingService {
    //simple handle for retrieving data and returning it
    private final WazeConfiguration wazeConfiguration;;
    private final WazeClient wazeClient;

    
}
