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
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class FileHelpers {

    @Value("${file.upload.path}")
    private String filePath;

    public void writeToFile(InputStream inputStream, String name) throws IOException {
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

    public void writeRoadSegmentsToCsv(List<RoadSegment> roadSegments, String fileName) throws IOException {
        var currentDayOfWeek = LocalDateTime.now().getDayOfWeek().name();
        File outputFolder = new File(filePath + "/" + currentDayOfWeek + "/" + fileName);
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
            Field[] fields = RoadSegment.class.getDeclaredFields();
            String[] headers = Arrays.stream(fields)
                    .map(Field::getName)
                    .toArray(String[]::new);
            csvWriter.writeNext(headers);

            // Write data
            beanToCsv.write(roadSegments);
            log.info("CSV file created successfully: " + csvFileName);
        } catch (Exception e) {
            log.error("Error writing CSV file: " + e.getMessage(), e);
            throw new IOException("Failed to write CSV file", e);
        }
    }
}
