package org.thuannt.waze_hcm_scraper.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thuannt.waze_hcm_scraper.domain.waze.Alternatives;
import org.thuannt.waze_hcm_scraper.domain.waze.DestinationInformation;
import org.thuannt.waze_hcm_scraper.domain.waze.Response;
import org.thuannt.waze_hcm_scraper.domain.waze.RoutingData;
import org.thuannt.waze_hcm_scraper.domain.waze.tabular.RoadSegment;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TabularDataConverter {

    private final RawToSegmentDataMapper mapper;

    public List<RoadSegment> convert(Alternatives alternatives, String timestamp) {
        var destinationId = alternatives.getRoutingData().stream().findAny().map(RoutingData::getResponse)
                .map(Response::getDestinationInformation).map(DestinationInformation::getSegment_id).orElse(null);
        return transformToRoadSegment(alternatives, destinationId, timestamp);
    }

    private List<RoadSegment> transformToRoadSegment(Alternatives alternatives, Integer destinationId, String timestamp) {
        var dayOfWeek = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(timestamp)), ZoneId.systemDefault()).getDayOfWeek().name();
        return alternatives.getRoutingData().stream()
                .map(RoutingData::getResponse)
                .map(Response::getResults)
                .flatMap(List::stream)
                .map(res -> mapper.mapToSegmentData(res, destinationId, timestamp, dayOfWeek)).collect(Collectors.toList());
    }
}
