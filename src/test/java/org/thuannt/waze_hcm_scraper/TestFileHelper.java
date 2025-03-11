package org.thuannt.waze_hcm_scraper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thuannt.waze_hcm_scraper.domain.deeptte.DeepTTEDataSet;
import org.thuannt.waze_hcm_scraper.service.FilesTranformer;
import org.thuannt.waze_hcm_scraper.utils.FileHelpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestFileHelper {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    void testWriteToByteArray() throws IOException {
        // Given
        List<DeepTTEDataSet> inputs = new ArrayList<>();
        DeepTTEDataSet data1 = new DeepTTEDataSet();
        data1.setDateID(1);
        data1.setDist(50.0);
        DeepTTEDataSet data2 = new DeepTTEDataSet();
        data1.setDateID(2);
        data1.setDist(100.0);
        inputs.add(data1);
        inputs.add(data2);

        // When
        byte[] result = FilesTranformer.writeToByteArray(inputs);

        // Then
        String resultString = new String(result);

        // Check for proper JSON formatting
        assertTrue(resultString.contains("{\"dist\":100.0,\"lats\":null,\"extraId\":0,\"weekID\":0,\"states\":null,\"timeID\":0,\"dateID\":2,\"time\":0.0,\"lngs\":null,\"time_gap\":null,\"dist_gap\":null}"));

        // Check that the JSON items are separated by new lines
        String[] lines = resultString.split("\\r?\\n");
        assertEquals(2, lines.length);  // Should match the number of input datasets
    }

}
