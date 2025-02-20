package org.thuannt.waze_hcm_scraper.domain.waze;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Route {
    @JsonProperty("response")
    private Response response; //For the case that there's only one route

    private List<RoutingData> alternatives;
}
