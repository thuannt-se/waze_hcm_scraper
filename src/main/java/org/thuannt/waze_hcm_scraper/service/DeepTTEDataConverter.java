package org.thuannt.waze_hcm_scraper.service;

import org.springframework.stereotype.Component;
import org.thuannt.waze_hcm_scraper.domain.deeptte.DeepTTEDataSet;
import org.thuannt.waze_hcm_scraper.domain.waze.*;
import org.thuannt.waze_hcm_scraper.domain.waze.tabular.RoadSegment;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DeepTTEDataConverter {

    public List<DeepTTEDataSet> convert(Route route, String timestamp) {
        if(route.getAlternatives() == null) {
            return Optional.ofNullable(route.getResponse()).map(response -> transformToRoadSegment(response, timestamp)).map(List::of).orElse(List.of());
        }
        return route.getAlternatives().stream()
                .map(RoutingData::getResponse)
                .map(response -> transformToRoadSegment(response, timestamp))
                .collect(Collectors.toList());
    }

    private DeepTTEDataSet transformToRoadSegment(Response response, String timestamp) {
        var date = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(timestamp)), ZoneId.systemDefault());
        var timeId = date.getMinute() + 60 * date.getHour();
        var weekId = date.getDayOfWeek().getValue() - 1;
        var dateId = date.getDayOfMonth() - 1;
        var lats = response.getResults().stream().map(Result::getPath).map(Path::getY).toList();
        var lngs = response.getResults().stream().map(Result::getPath).map(Path::getX).toList();
        var disGap = response.getResults().stream().map(Result::getLength).map(Double::valueOf).map(len -> len/1000).toList();
        var timeGaps = response.getResults().stream().map(Result::getDistance).map(Double::valueOf).toList();
        var dist = response.getResults().stream().map(Result::getLength).map(Double::valueOf).reduce(Double::sum).orElse(0.0);

        return DeepTTEDataSet.builder()
                .weekID(weekId)
                .dateID(dateId)
                .timeGap(timeGaps)
                .timeID(timeId)
                .dist(dist/1000)
                .time(response.getTotalRouteTime())
                .lats(lats)
                .lngs(lngs)
                .distGap(disGap)
                .build();
    }
}
