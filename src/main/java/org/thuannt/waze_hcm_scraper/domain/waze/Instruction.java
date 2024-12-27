package org.thuannt.waze_hcm_scraper.domain.waze;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Instruction {
    private String opcode;
    private int arg;
}
