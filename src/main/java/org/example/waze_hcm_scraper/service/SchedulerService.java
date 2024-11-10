package org.example.waze_hcm_scraper.service;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.waze_hcm_scraper.config.WazeConfiguration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final WazeRoutingService wazeRoutingService;
    private final WazeConfiguration wazeConfiguration;

    private static final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    @Scheduled(cron = "0 */5 5-23 * * *")
    public void wazeScheduler() {
        wazeConfiguration.getTrip().forEach(tripCoordinate -> {
            wazeRoutingService.getRoutingData("row-SearchServer/mozi", tripCoordinate.getOrigin(), tripCoordinate.getDestination())
                    .thenAccept(inputStream -> {
                        try {
                            writeToFile(inputStream, tripCoordinate.getName());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        });
    }

    private void writeToFile(InputStream inputStream, String name) throws IOException {
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper();
        JsonParser parser = factory.createParser(inputStream);

        var now = timestamp.toInstant().toEpochMilli();
        // Create a custom file name based on the current timestamp
        String fileName = name + "_" + now + ".json";
        File outputFile = new File(fileName);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        JsonGenerator generator = factory.createGenerator(outputStream);

        // Start writing the output JSON object
        generator.writeStartObject();

        while (parser.nextToken() != null) {
            JsonToken token = parser.getCurrentToken();
            if (token == JsonToken.FIELD_NAME) {
                String fieldName = parser.currentName();
                parser.nextToken();
                String fieldValue = parser.getText();
                // Process the field value as needed
                generator.writeFieldName(fieldName);
                generator.writeString(fieldValue);
            }
        }

        // End writing the output JSON object
        generator.writeEndObject();

        // Close the parser, generator, and streams
        parser.close();
        generator.close();
        outputStream.close();

        log.info("JSON data processed and saved to " + fileName + " successfully.");
    }

}
