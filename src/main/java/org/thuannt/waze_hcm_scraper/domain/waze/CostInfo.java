package org.thuannt.waze_hcm_scraper.domain.waze;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostInfo {
    private int unbiasedAstarCost;
    private int tollAsSeconds;
    private boolean keepForReordering;
}
