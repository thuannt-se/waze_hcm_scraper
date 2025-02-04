package org.thuannt.waze_hcm_scraper.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thuannt.waze_hcm_scraper.domain.waze.Alternatives;
import org.thuannt.waze_hcm_scraper.domain.waze.Response;
import org.thuannt.waze_hcm_scraper.domain.waze.RoutingData;
import org.thuannt.waze_hcm_scraper.domain.waze.tabular.RoadSegment;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TabularDataConverter {

    private final RawToSegmentDataMapper mapper;

    public List<RoadSegment> convert(Alternatives alternatives) {
        return transformToRoadSegment(alternatives);
    }

    private List<RoadSegment> transformToRoadSegment(Alternatives alternatives) {
        return alternatives.getRoutingData().stream()
                .map(RoutingData::getResponse)
                .map(Response::getResults)
                .flatMap(List::stream)
                .map(mapper::mapToSegmentData).collect(Collectors.toList());
    }
}
