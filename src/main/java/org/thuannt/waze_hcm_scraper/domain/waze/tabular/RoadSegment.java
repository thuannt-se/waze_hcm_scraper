package org.thuannt.waze_hcm_scraper.domain.waze.tabular;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoadSegment extends CsvData {
    private int segmentId;
    private int nodeId;
    private double x;
    private double y;
    private int street;
    private int length;
    private int crossTime;
    private int crossTimeWithoutRealTime;
    private int crossTimeFreeFlow;
    private int roadType;
    private int averageSpeed;
    private int destinationId;
    private String dayOfWeek;
    private String timestamp;
}
