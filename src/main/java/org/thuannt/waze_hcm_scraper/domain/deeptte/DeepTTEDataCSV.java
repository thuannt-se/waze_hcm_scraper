package org.thuannt.waze_hcm_scraper.domain.deeptte;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import lombok.*;
import org.thuannt.waze_hcm_scraper.domain.waze.tabular.CsvData;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeepTTEDataCSV extends CsvData {
    @CsvBindByName(column = "time_gap")
    public String timeGap;

    @CsvBindByName(column = "dist")
    public String dist;

    @CsvBindByName(column = "lats")
    public String lats;

    @CsvBindByName(column = "week_id")
    public String weekID;
    @CsvBindByName(column = "time_id")
    public String timeID;
    @CsvBindByName(column = "date_id")
    public String dateID;
    @CsvBindByName(column = "time")
    public String time;
    @CsvBindByName(column = "lngs")
    public String lngs;

    @CsvBindByName(column = "dist_gap")
    public String distGap;
}

