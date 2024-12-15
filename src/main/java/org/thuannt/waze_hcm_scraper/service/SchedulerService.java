package org.thuannt.waze_hcm_scraper.service;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.thuannt.waze_hcm_scraper.config.WazeConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Timestamp;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final WazeRoutingService wazeRoutingService;
    private final WazeConfiguration wazeConfiguration;

    @Value("${file.upload.path}")
    private String filePath;

    @Scheduled(cron = "0 */5 5-23 * * *")
    public void wazeScheduler() {
        log.info("Starting Waze Scheduler...");
        wazeConfiguration.getTrip().forEach(tripCoordinate -> {
            wazeRoutingService.getRoutingData("HCMC", tripCoordinate.getOrigin(), tripCoordinate.getDestination())
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
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        JsonParser parser = factory.createParser(inputStream);
        var currentDayOfWeek = timestamp.toLocalDateTime().getDayOfWeek().name();
        // Create a custom file name based on the current timestamp

        File outputFolder = new File(filePath + "/" + currentDayOfWeek + "/" + name);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        String fileName = name + "_" + timestamp.toInstant().toEpochMilli() + ".json";

        File outputFile = new File(outputFolder, fileName);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        JsonGenerator generator = factory.createGenerator(outputStream);

        // Start writing the output JSON object
        generator.writeStartObject();

        // Start writing the output JSON
        copyJsonStructure(parser, generator);

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
        inputStream.close();
        outputStream.close();

        log.info("JSON data processed and saved to " + fileName + " successfully.");
    }

    private void copyJsonStructure(JsonParser parser, JsonGenerator generator) throws IOException {
        int depth = 0;
        while (parser.nextToken() != null) {
            JsonToken token = parser.getCurrentToken();

            switch (token) {
                case START_OBJECT:
                case START_ARRAY:
                    depth++;
                    log.debug("Entering nested level: " + depth);
                    if (depth > 100) { // Example validation
                        throw new IOException("JSON structure too deeply nested");
                    }
                    generator.copyCurrentStructure(parser);
                    break;

                case END_OBJECT:
                case END_ARRAY:
                    depth--;
                    log.debug("Exiting nested level: " + depth);
                    if (depth < 0) {
                        throw new IOException("Invalid JSON structure: unmatched closing bracket/brace");
                    }
                    break;

                default:
                    generator.copyCurrentEvent(parser);
                    break;
            }
        }

        // Final validation
        if (depth != 0) {
            throw new IOException("Invalid JSON structure: unclosed objects or arrays");
        }
    }
}
