package org.thuannt.waze_hcm_scraper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thuannt.waze_hcm_scraper.domain.deeptte.DeepTTEDataSet;
import org.thuannt.waze_hcm_scraper.domain.waze.Route;
import org.thuannt.waze_hcm_scraper.domain.waze.tabular.RoadSegment;
import org.thuannt.waze_hcm_scraper.utils.FileHelpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilesTranformer {
    private final TabularDataConverter tabularDataConverter;
    private final FileHelpers fileHelpers;
    private final DeepTTEDataConverter deepTTEDataConverter;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public byte[] transformDeepTte(String route, double days, double trainRatio) {
        var jsonFiles = fileHelpers.getJsonFiles(0, route);
        return null;
    }

    private List<RoadSegment> getPeriodRoadSegment(long daysFrNow, String route) {
        try {
            var jsonFiles = fileHelpers.getJsonFiles(daysFrNow, route);
            return transformRoutes(jsonFiles);
        } catch (Exception e) {
            log.error("Error processing JSON files: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public void processJsonFilesToCsv(String route) {
        var jsonFiles = fileHelpers.getJsonFiles(0, route);
        var allRoadSegments = transformRoutes(jsonFiles);
        fileHelpers.processJsonFilesToCsv(allRoadSegments, route);
    }

    private List<DeepTTEDataSet> transformToDeepTte(List<Path> jsonFiles) {
        if (jsonFiles.isEmpty()) return Collections.emptyList();
        return jsonFiles.stream()
                .map(this::toDeepTteDataset)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<RoadSegment> transformRoutes(List<Path> jsonFiles) {
        if (jsonFiles.isEmpty()) return Collections.emptyList();
        // Process each file and collect all RoadSegments
        return jsonFiles.stream()
                .map(this::toRoadSegments)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<DeepTTEDataSet> toDeepTteDataset(Path jsonFile) {
        try {
            log.info("Processing file: {}", jsonFile);
            String jsonContent = Files.readString(jsonFile);

            // Parse JSON to Route
            Route route = objectMapper.readValue(jsonContent, Route.class);

            var timestamp = getPartFromFileName(jsonFile.getFileName().toString(), 2);

            // Convert Route to RoadSegments
            return deepTTEDataConverter.convert(route, timestamp);

        } catch (IOException e) {
            log.error("Error processing file {}: {}", jsonFile, e.getMessage());
            return null;
        }
    }

    private List<RoadSegment> toRoadSegments(Path jsonFile) {
        try {
            log.info("Processing file: {}", jsonFile);
            String jsonContent = Files.readString(jsonFile);

            // Parse JSON to Route
            Route route = objectMapper.readValue(jsonContent, Route.class);

            var timestamp = getPartFromFileName(jsonFile.getFileName().toString(), 2);

            // Convert Route to RoadSegments
            return convertAlternativesToRoadSegments(route, timestamp);

        } catch (IOException e) {
            log.error("Error processing file {}: {}", jsonFile, e.getMessage());
            return null;
        }
    }

    private List<RoadSegment> convertAlternativesToRoadSegments(Route route, String timestamp) {
        if (route == null || (route.getAlternatives() == null && route.getResponse() == null)) {
            return Collections.emptyList();
        }

        return tabularDataConverter.convert(route, timestamp);
    }

    private static String getPartFromFileName(String filename, int group) {
        try {
            Pattern pattern = Pattern.compile("(.*)_(\\d+)\\.json$");
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
