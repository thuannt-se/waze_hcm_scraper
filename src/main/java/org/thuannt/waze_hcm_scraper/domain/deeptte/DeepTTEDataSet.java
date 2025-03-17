package org.thuannt.waze_hcm_scraper.domain.deeptte;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import lombok.*;
import org.thuannt.waze_hcm_scraper.domain.waze.tabular.CsvData;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeepTTEDataSet {
    @JsonProperty("time_gap")
    public List<Double> timeGap;

    public Double dist;

    public List<Double> lats;

    public int extraId; //Use this field for any feature you want to add
    public int weekID;
    public List<Double> states;
    public int timeID;
    public int dateID;
    public double time;
    public List<Double> lngs;

    @JsonProperty("dist_gap")
    public List<Double> distGap;
}

