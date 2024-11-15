package org.thuannt.waze_hcm_scraper.domain;

import lombok.Data;

@Data
public class TripCoordinate {
    private String name;
    private Coordinate origin;
    private Coordinate destination;;
}
