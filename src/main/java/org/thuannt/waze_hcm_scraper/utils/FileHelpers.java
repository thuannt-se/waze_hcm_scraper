package org.thuannt.waze_hcm_scraper.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thuannt.waze_hcm_scraper.domain.waze.tabular.RoadSegment;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class FileHelpers {

    @Value("${file.upload.path}")
    private String filePath;

    public void writeToFile(InputStream inputStream, String name) throws IOException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        File outputFolder = new File(filePath  + "/" + name);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        String fileName = name + "_" + timestamp.toInstant().toEpochMilli() + ".json";

        File outputFile = new File(outputFolder, fileName);
        FileOutputStream outputStream = new FileOutputStream(outputFile);

        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(inputStream);
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
    }

    public void processJsonFilesToCsv(List<RoadSegment> allRoadSegments, String route) {
        try {
            if (!allRoadSegments.isEmpty()) {
                writeRoadSegmentsToCsv(allRoadSegments, route);
                log.info("Successfully processed {} files and wrote road segments to CSV", allRoadSegments.size());
            } else {
                log.warn("No road segments found in the processed files");
            }
        } catch (IOException e) {
            log.error("Error processing JSON files: " + e.getMessage(), e);
        }
    }
    public List<Path> getJsonFiles(long daysFrNow, String route) {
        try {
            var today = LocalDate.now();
            var start = today.minusDays(daysFrNow);
            // Get all JSON files from the folder
            return Files.walk(Paths.get(filePath + "/" + route))
                    .filter(Files::isRegularFile)
                    .filter(path -> isFileInTimeRange(path, start.atStartOfDay(), today.atTime(23, 59)))
                    .filter(path -> path.toString().endsWith(".json"))
                    .toList();
        } catch (IOException e) {
            log.error("Error getting JSON files: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private boolean isFileInTimeRange(Path file, LocalDateTime start, LocalDateTime end) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
            LocalDateTime fileTime = LocalDateTime.ofInstant(
                    attrs.creationTime().toInstant(),
                    ZoneId.systemDefault()
            );
            return !fileTime.isBefore(start) && !fileTime.isAfter(end);
        } catch (IOException e) {
            log.error("Error reading file attributes: " + e.getMessage(), e);
            return false;
        }
    }

    public void writeRoadSegmentsToCsv(List<RoadSegment> roadSegments, String fileName) throws IOException {
        File outputFolder = new File(filePath + "/" + fileName);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        String csvFileName = fileName + ".csv";
        File csvFile = new File(outputFolder, csvFileName);

        try (Writer writer = new FileWriter(csvFile)) {
            StatefulBeanToCsv<RoadSegment> beanToCsv = new StatefulBeanToCsvBuilder<RoadSegment>(writer)
                    .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withOrderedResults(true)
                    .build();

            // Write headers manually using property names
            CSVWriter csvWriter = new CSVWriter(writer);

            // Write data
            beanToCsv.write(roadSegments);
            log.info("CSV file created successfully: " + csvFileName);
        } catch (Exception e) {
            log.error("Error writing CSV file: " + e.getMessage(), e);
            throw new IOException("Failed to write CSV file", e);
        }
    }


}
