package org.example.waze_hcm_scraper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties
@SpringBootApplication
public class WazeHcmScraperApplication {

	public static void main(String[] args) {
		SpringApplication.run(WazeHcmScraperApplication.class, args);
	}

}
