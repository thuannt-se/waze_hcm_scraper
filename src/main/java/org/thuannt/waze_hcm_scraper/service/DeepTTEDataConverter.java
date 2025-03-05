package org.thuannt.waze_hcm_scraper.service;

import org.springframework.stereotype.Component;
import org.thuannt.waze_hcm_scraper.domain.deeptte.DeepTTEDataSet;
import org.thuannt.waze_hcm_scraper.domain.waze.*;
import org.thuannt.waze_hcm_scraper.domain.waze.tabular.RoadSegment;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeepTTEDataConverter {

    public List<DeepTTEDataSet> convert(Route route, String timestamp) {
        var weekId = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(timestamp)), ZoneId.systemDefault()).getDayOfWeek().getValue() - 1;
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

    private List<DeepTTEDataSet> transformToRoadSegment(List<Result> results, int weekId) {
        return results.stream().map()

    }
}
