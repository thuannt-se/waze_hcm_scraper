package org.thuannt.waze_hcm_scraper.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thuannt.waze_hcm_scraper.domain.waze.Alternatives;
import org.thuannt.waze_hcm_scraper.domain.waze.tabular.RoadSegment;
import org.thuannt.waze_hcm_scraper.service.RawToSegmentDataMapper;
import org.thuannt.waze_hcm_scraper.service.TabularDataConverter;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FileHelpers {

    @Value("${file.upload.path}")
    private String filePath;

    @Autowired
    private TabularDataConverter tabularDataConverter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void writeToFile(InputStream inputStream, String name) throws IOException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        File outputFolder = new File(filePath  + "/" + name);
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

    public void processJsonFilesToCsv() {
        try {
            // Get all JSON files from the folder
            List<Path> jsonFiles = Files.walk(Paths.get(filePath))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .toList();

            // Process each file and collect all RoadSegments
            List<RoadSegment> allRoadSegments = jsonFiles.stream()
                    .map(this::processJsonFile)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            // Write to CSV
            if (!allRoadSegments.isEmpty()) {
                writeRoadSegmentsToCsv(allRoadSegments, "combined_road_segments");
                log.info("Successfully processed {} files and wrote {} road segments to CSV",
                        jsonFiles.size(), allRoadSegments.size());
            } else {
                log.warn("No road segments found in the processed files");
            }

        } catch (IOException e) {
            log.error("Error processing JSON files: " + e.getMessage(), e);
        }
    }

    private List<RoadSegment> processJsonFile(Path jsonFile) {
        try {
            log.info("Processing file: {}", jsonFile);
            String jsonContent = Files.readString(jsonFile);

            // Parse JSON to Alternatives
            Alternatives alternatives = objectMapper.readValue(jsonContent, Alternatives.class);

            var timestamp = this.getPartFromFileName(jsonFile.getFileName().toString(), 1);

            // Convert Alternatives to RoadSegments
            return convertAlternativesToRoadSegments(alternatives, timestamp);

        } catch (IOException e) {
            log.error("Error processing file {}: {}", jsonFile, e.getMessage());
            return null;
        }
    }

    private List<RoadSegment> convertAlternativesToRoadSegments(Alternatives alternatives, String timestamp) {
        if (alternatives == null || alternatives.getRoutingData() == null) {
            return Collections.emptyList();
        }

        return tabularDataConverter.convert(alternatives, timestamp);
    }

    // Optional: Method to process specific time range
    public void processTimeRange(LocalDateTime start, LocalDateTime end) {
        try {
            List<Path> jsonFiles = Files.walk(Paths.get(filePath))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .filter(path -> isFileInTimeRange(path, start, end))
                    .collect(Collectors.toList());

            processFiles(jsonFiles);
        } catch (IOException e) {
            log.error("Error processing files in time range: " + e.getMessage(), e);
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

    private void processFiles(List<Path> files) {
        var route = files.stream().findAny().map(Path::getFileName).map(Path::toString).orElse("");
        List<RoadSegment> allRoadSegments = files.stream()
                .map(this::processJsonFile)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        if (!allRoadSegments.isEmpty()) {
            try {
                writeRoadSegmentsToCsv(allRoadSegments, this.getPartFromFileName(route, 0));
                log.info("Successfully processed {} files and wrote {} road segments to CSV",
                        files.size(), allRoadSegments.size());
            } catch (IOException e) {
                log.error("Error writing to CSV: " + e.getMessage(), e);
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

    private String getPartFromFileName(String filename, int group) {
        try {
            Pattern pattern = Pattern.compile(".*_(\\d+)\\.json$");
            Matcher matcher = pattern.matcher(filename);

            if (matcher.find()) {
                return matcher.group(group);
            }
        } catch (Exception e) {
            log.error("Error parsing timestamp from filename: {}", filename, e);
        }
        return null;
    }
}
