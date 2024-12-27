package org.thuannt.waze_hcm_scraper.domain.waze;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Path {
    private int segmentId;
    private int nodeId;
    private double x;
    private double y;
    private boolean direction;
}
