package org.example.waze_hcm_scraper;

import lombok.extern.slf4j.Slf4j;
import org.example.waze_hcm_scraper.config.WazeConfiguration;
import org.example.waze_hcm_scraper.service.WazeRoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutionException;

@AutoConfiguration
@EnableConfigurationProperties
@SpringBootApplication
@EnableAsync
@Slf4j
public class WazeHcmScraperApplication {

	@Autowired
	private WazeRoutingService wazeRoutingService;

	@Autowired
	private WazeConfiguration wazeConfiguration;

	public static void main(String[] args) {
		SpringApplication.run(WazeHcmScraperApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void doStartRequest() throws ExecutionException, InterruptedException {
		wazeRoutingService.getRoutingData("HCMC", wazeConfiguration.getTrip().get(0).getOrigin(),
				wazeConfiguration.getTrip().get(0).getDestination()).get();

	}

	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(50);
		executor.setThreadNamePrefix("JobAsynThread-");
		executor.setRejectedExecutionHandler((r, executor1) -> log.warn("Task rejected, thread pool is full and queue is also full"));
		executor.initialize();
		return executor;
	}

}
