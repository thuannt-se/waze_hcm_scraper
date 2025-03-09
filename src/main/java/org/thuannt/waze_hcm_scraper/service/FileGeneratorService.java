package org.thuannt.waze_hcm_scraper.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thuannt.waze_hcm_scraper.domain.deeptte.DeepTTEDataSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileGeneratorService {
    private final FilesTranformer filesTranformer;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final int MAX_RECORD = 3600;
    public byte[] generateDeepTteTrainDataset(String route, long days , double trainRatio, double validionRatio) {
        var data = filesTranformer.transformDeepTte(route, days);
        var trainData = data.subList(0, (int) (data.size() * trainRatio));
        var testData = data.subList((int) (data.size() * trainRatio), (int) (data.size() * (trainRatio + validionRatio)));
        var validationData = data.subList((int) (data.size() * (trainRatio + validionRatio)), data.size());
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
            writeData(zipOutputStream, "train", trainData);
            writeData(zipOutputStream, "test", testData);
            writeData(zipOutputStream, "validate", validationData);

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error while generating deep tte dataset: {}", e.getMessage());
        }
        return null;
    }


    private void writeData(ZipOutputStream zipOutputStream, String fileName, List<DeepTTEDataSet> data) throws IOException {
        List<List<DeepTTEDataSet>> dataSubList = divideIntoSubList(data, MAX_RECORD);
        for(int i = 0; i < dataSubList.size(); i++) {
            writeDataToZip(zipOutputStream, fileName + "_" + i, dataSubList.get(i));
        }
    }

    public List<List<DeepTTEDataSet>> divideIntoSubList(List<DeepTTEDataSet> data, int maxSize) {
        List<List<DeepTTEDataSet>> result = new ArrayList<>();
        for(int i = 0; i < data.size(); i+=maxSize) {
            result.add(data.subList(i, Math.min(i + maxSize, data.size())));
        }
        return result;
    }
    private void writeDataToZip(ZipOutputStream zipOutputStream, String fileName, List<DeepTTEDataSet> data) throws IOException {
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOutputStream.putNextEntry(zipEntry);
        var result = filesTranformer.writeToByteArray(data);
        zipOutputStream.write(result);
        zipOutputStream.closeEntry();
    }
}
