package org.thuannt.waze_hcm_scraper.domain.waze;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutingData {
    private Response response;
    private List<Coord> coords;
}
