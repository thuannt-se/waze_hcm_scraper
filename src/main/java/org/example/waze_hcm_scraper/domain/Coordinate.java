package org.example.waze_hcm_scraper.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Coordinate {
    private double lat;
    private double lon;
}
