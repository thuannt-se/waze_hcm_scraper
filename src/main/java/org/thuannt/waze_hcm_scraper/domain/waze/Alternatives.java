package org.thuannt.waze_hcm_scraper.domain.waze;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alternatives {
    private List<RoutingData> routingData;
}
