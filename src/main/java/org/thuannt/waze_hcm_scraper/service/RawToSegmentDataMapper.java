package org.thuannt.waze_hcm_scraper.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.thuannt.waze_hcm_scraper.domain.waze.Result;
import org.thuannt.waze_hcm_scraper.domain.waze.tabular.RoadSegment;

@Mapper(componentModel = "spring")
public interface RawToSegmentDataMapper {

    @Mapping(source = "path.x", target = "x")
    @Mapping(source = "path.y", target = "y")
    @Mapping(source = "path.segmentId", target = "segmentId")
    @Mapping(source = "path.nodeId", target = "nodeId")
    RoadSegment mapToSegmentData(Result rawData);
}
