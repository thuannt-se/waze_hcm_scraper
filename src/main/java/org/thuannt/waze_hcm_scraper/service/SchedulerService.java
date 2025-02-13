package org.thuannt.waze_hcm_scraper.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thuannt.waze_hcm_scraper.config.WazeConfiguration;
import org.thuannt.waze_hcm_scraper.utils.FileHelpers;

import java.io.IOException;

@Service
@EnableScheduling
@EnableAsync
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final WazeRoutingService wazeRoutingService;
    private final WazeConfiguration wazeConfiguration;
    private final FileHelpers fileHelpers;

    @Async("asyncTaskExecutor")
    @Scheduled(cron = "0 */5 5-23 * * *")
    public void wazeScheduler() {
        log.info("Starting Waze Scheduler...");
        wazeConfiguration.getTrip().forEach(tripCoordinate -> {
            wazeRoutingService.getRoutingData("HCMC", tripCoordinate.getOrigin(), tripCoordinate.getDestination())
                    .thenAccept(inputStream -> {
                        try {
                            fileHelpers.writeToFile(inputStream, tripCoordinate.getName());
                        } catch (IOException e) {
                            log.error("Error while writing JSON file for routing data: {}", e.getMessage());
                        }
                    });
        });
    }

    @Async("asyncTaskExecutor")
    @Scheduled(cron = "0 0 23 * * ?") // Runs at 11:00 PM every day
    public void runScheduledTask() {
        log.info("Starting Waze Scheduler: writing to CSV files");
        wazeConfiguration.getTrip().forEach(tripCoordinate -> {
            try {
                fileHelpers.processJsonFilesToCsv(tripCoordinate.getName());
            } catch (Exception e) {
                log.error("Error while writing CSV file from json data: {}", e.getMessage());
            }
        });
    }

}
