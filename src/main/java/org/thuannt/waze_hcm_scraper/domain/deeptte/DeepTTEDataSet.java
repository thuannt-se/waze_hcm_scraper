package org.thuannt.waze_hcm_scraper.domain.deeptte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeepTTEDataSet {
    public List<Double> timeGap;
    public Double dist;
    public List<Double> lats;
    public int driverID;
    public int weekID;
    public List<Double> states;
    public int timeID;
    public int dateID;
    public double time;
    public List<Double> lngs;
    public List<Double> distGap;
}

