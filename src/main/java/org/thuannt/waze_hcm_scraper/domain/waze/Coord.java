package org.thuannt.waze_hcm_scraper.domain.waze;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coord {
    private double x;
    private double y;
    private Object z;
}
