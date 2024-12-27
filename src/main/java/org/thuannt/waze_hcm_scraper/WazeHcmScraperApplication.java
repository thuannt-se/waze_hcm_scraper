package org.thuannt.waze_hcm_scraper;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import lombok.extern.slf4j.Slf4j;
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
import org.thuannt.waze_hcm_scraper.config.WazeConfiguration;
import org.thuannt.waze_hcm_scraper.service.WazeRoutingService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
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

    /*@EventListener(ApplicationReadyEvent.class)
    public void doStartRequest() throws ExecutionException, InterruptedException {
        wazeRoutingService.getRoutingData("HCMC", wazeConfiguration.getTrip().get(0).getOrigin(),
                wazeConfiguration.getTrip().get(0).getDestination()).thenAccept(inputStream -> {
            try {
                writeToFile(inputStream, "test");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private void writeToFile(InputStream inputStream, String name) throws IOException {
        JsonFactory factory = new JsonFactory();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        JsonParser parser = factory.createParser(inputStream);
        var currentDayOfWeek = timestamp.toLocalDateTime().getDayOfWeek().name();
        // Create a custom file name based on the current timestamp

        File outputFolder = new File("resources/output/raw_waze_data/FRIDAY" + "/" + currentDayOfWeek + "/" + name);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        String fileName = name + "_" + timestamp.toInstant().toEpochMilli() + ".json";

        File outputFile = new File(outputFolder, fileName);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        JsonGenerator generator = factory.createGenerator(outputStream);


        // Start writing the output JSON
        copyJsonStructure(parser, generator);

        // Close the parser, generator, and streams
        parser.close();
        generator.close();
        inputStream.close();
        outputStream.close();

        log.info("JSON data processed and saved to " + fileName + " successfully.");
    }

    private void copyJsonStructure(JsonParser parser, JsonGenerator generator) throws IOException {
        while (parser.nextToken() != null) {  // This loop continues until we run out of tokens
            JsonToken token = parser.getCurrentToken();

            switch (token) {
                case START_OBJECT:
                    generator.copyCurrentStructure(parser);
                    break;
                case END_OBJECT:
                    generator.copyCurrentStructure(parser);
                    break;
                case START_ARRAY:
                    generator.writeStartArray();
                    break;
                case END_ARRAY:
                    generator.writeEndArray();
                    break;
                case FIELD_NAME:
                    generator.writeFieldName(parser.getCurrentName());
                    break;
                case VALUE_STRING:
                    generator.writeString(parser.getText());
                    break;
                case VALUE_NUMBER_INT:
                    generator.writeNumber(parser.getLongValue());
                    break;
                case VALUE_NUMBER_FLOAT:
                    generator.writeNumber(parser.getDoubleValue());
                    break;

                case VALUE_TRUE:
                    generator.writeBoolean(true);
                    break;  // Only breaks the switch, returns to while loop

                case VALUE_FALSE:
                    generator.writeBoolean(false);
                    break;  //

                case VALUE_NULL:
                    generator.writeNull();
                    break;  // Only breaks the switch, returns to while loop
            }
        }
    }*/

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
