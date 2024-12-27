package org.thuannt.waze_hcm_scraper.config;

import lombok.Data;

@Data
public class TripCoordinate {
    private String name;
    private Coordinate origin;
    private Coordinate destination;;
}
