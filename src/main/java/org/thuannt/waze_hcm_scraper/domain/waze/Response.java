package org.thuannt.waze_hcm_scraper.domain.waze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private List<Result> results;
    private List<String> streetNames;
    private double fromlocMetersFromGeom;
    private double fromFraction;
    private double toFraction;
    private boolean sameFromSegment;
    private boolean sameToSegment;
    private List<Object> wayPoints;
    private int tollMeters;
    private int preferedRouteId;
    private boolean isInvalid;
    private boolean isBlocked;
    private String serverUniqueId;
    private boolean displayRoute;
    private int astarVisited;
    private String astarResult;
    private boolean isRestricted;
    private String avoidStatus;
    private boolean passesThroughDangerArea;
    private int distanceFromSource;
    private int distanceFromTarget;
    private int minPassengers;
    private int hovIndex;
    private int preRefinedRouteIndex;
    private int blockedRouteUid;
    private String alternativeRouteUuid;
    private long serverTilesVersion;
    private int rankBeforeReordering;
    private String serverType;
    private List<String> routeType;
    private List<String> routeAttr;
    private int astarCost;
    private int uid;
    private int totalRouteTime;
    private int totalRouteTimeWithoutRealtime;
    private int totalRouteTimeFreeFlow;
    private int carbonEmissionsGrams;
    private List<String> creationMechanismSet;
    private List<Object> laneTypes;
    private List<Object> areas;
    private List<Object> areasToAvoid;
    private List<Object> requiredPermits;
    private List<Object> etaHistograms;
    private String shortRouteName;
    private double tollPrice;
    private boolean isInvalidForPrivateVehicle;
    private CostInfo costInfo;
    private DestinationInformation destinationInformation;
    private String routeName;
    private List<Integer> routeNameStreetIds;
    private int totalRouteTimeWithoutMl;
    @JsonProperty("open")
    private boolean isOpen;
}
