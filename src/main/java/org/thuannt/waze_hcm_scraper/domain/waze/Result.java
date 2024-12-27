package org.thuannt.waze_hcm_scraper.domain.waze;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private boolean isToll;
    private boolean useHovLane;
    private int attributes;
    private String lane;
    private String avoidStatus;
    private boolean isInvalid;
    private boolean isBlocked;
    private int speedLimit;
    private Instruction instruction;
}
