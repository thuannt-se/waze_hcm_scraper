package org.thuannt.waze_hcm_scraper.domain.waze;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DestinationInformation {
    private int segment_id;
    private int street_id;
    private int city_id;
    private int country_id;
    private String city_name;
    private int exit_node_id;
}
