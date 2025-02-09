package org.thuannt.waze_hcm_scraper.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.thuannt.waze_hcm_scraper.domain.waze.Result;
import org.thuannt.waze_hcm_scraper.domain.waze.tabular.RoadSegment;

@Mapper(componentModel = "spring")
public interface RawToSegmentDataMapper {

    @Mapping(source = "rawData.path.x", target = "x")
    @Mapping(source = "rawData.path.y", target = "y")
    @Mapping(source = "rawData.path.segmentId", target = "segmentId")
    @Mapping(source = "rawData.path.nodeId", target = "nodeId")
    RoadSegment mapToSegmentData(Result rawData, Integer destinationId, String timestamp, String dayOfWeek);
}
