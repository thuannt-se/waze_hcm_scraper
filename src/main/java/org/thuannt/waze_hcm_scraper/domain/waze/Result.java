package org.thuannt.waze_hcm_scraper.domain.waze;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {
    private Path path;
    private int street;
    private int distance;
    private int length;
    private int crossTime;
    private int crossTimeWithoutRealTime;
    private int crossTimeFreeFlow;
    private boolean knownDirection;
    private int penalty;
    private int roadType;
    private boolean useHovLane;
    private boolean isToll;
    private int attributes;
    private String lane;
    private String avoidStatus;
    private boolean isInvalid;
    private boolean isBlocked;
    private int speedLimit;
    private Instruction instruction;
}
