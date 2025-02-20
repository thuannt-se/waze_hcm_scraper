package org.thuannt.waze_hcm_scraper.domain.waze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutingData {
    @JsonProperty("response")
    private Response response;
    @JsonProperty("coords")
    private List<Coord> coords;
}
