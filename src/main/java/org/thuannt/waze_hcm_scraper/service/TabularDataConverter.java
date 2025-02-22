package org.thuannt.waze_hcm_scraper.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thuannt.waze_hcm_scraper.domain.waze.Route;
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

    public List<RoadSegment> convert(Route route, String timestamp) {
        Integer destinationId = 0;
        if (route.getAlternatives() != null) {
             destinationId = route.getAlternatives().stream().findAny().map(RoutingData::getResponse)
                    .map(Response::getDestinationInformation).map(DestinationInformation::getSegment_id).orElse(null);
        }
        if(destinationId == null && route.getResponse() != null) {
            destinationId = route.getResponse().getDestinationInformation().getSegment_id();
        }
        return transformToRoadSegment(route, destinationId, timestamp);
    }

    private List<RoadSegment> transformToRoadSegment(Route route, Integer destinationId, String timestamp) {
        var dayOfWeek = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(timestamp)), ZoneId.systemDefault()).getDayOfWeek().name();
        if(route.getAlternatives() == null) {
            return route.getResponse().getResults().stream()
                    .map(res -> mapper.mapToSegmentData(res, destinationId, timestamp, dayOfWeek)).collect(Collectors.toList());
        }
        return route.getAlternatives().stream()
                .map(RoutingData::getResponse)
                .map(Response::getResults)
                .flatMap(List::stream)
                .map(res -> mapper.mapToSegmentData(res, destinationId, timestamp, dayOfWeek)).collect(Collectors.toList());
    }
}
