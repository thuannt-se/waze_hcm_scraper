package org.thuannt.waze_hcm_scraper.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thuannt.waze_hcm_scraper.config.WazeConfiguration;
import org.thuannt.waze_hcm_scraper.domain.deeptte.DeepTTEDataSet;
import org.thuannt.waze_hcm_scraper.utils.FileHelpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileGeneratorService {
    private final FilesTranformer filesTranformer;
    private final FileHelpers fileHelpers;
    private final WazeConfiguration wazeConfiguration;
    private static final int MAX_RECORD = 3600;

    public byte[] generateDeepTteTrainDatasetOnly(String route, long days , double trainRatio, double validationRatio) throws IOException {
        var data = filesTranformer.transformDeepTte(route, days);
        var trainData = data.subList(0, (int) (data.size() * trainRatio));
        var testData = data.subList((int) (data.size() * trainRatio), (int) (data.size() * (trainRatio + validationRatio)));
        var validationData = data.subList((int) (data.size() * (trainRatio + validationRatio)), data.size());
        log.info("Data size: {}, train size: {}, test size: {}, validation size: {}", data.size(), trainData.size(), testData.size(), validationData.size());
        
        return FilesTranformer.writeToByteArray(trainData);
    }
    public byte[] generateDeepTteTrainDataset(String route, long days , double trainRatio, double validationRatio, String type) {
        List<DeepTTEDataSet> data = getDeepTTEDataSets(route, days);
        var trainData = data.subList(0, (int) (data.size() * trainRatio));
        var testData = data.subList((int) (data.size() * trainRatio), (int) (data.size() * (trainRatio + validationRatio)));
        var validationData = data.subList((int) (data.size() * (trainRatio + validationRatio)), data.size());
        log.info("Data size: {}, train size: {}, test size: {}, validation size: {}", data.size(), trainData.size(), testData.size(), validationData.size());

        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
            writeData(zipOutputStream, "train", trainData, type);
            writeData(zipOutputStream, "test", testData, type);
            writeData(zipOutputStream, "validate", validationData,  type);
            zipOutputStream.closeEntry();
            zipOutputStream.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error while generating deep tte dataset: {}", e.getMessage());
        }
        return null;
    }

    private List<DeepTTEDataSet> getDeepTTEDataSets(String route, long days) {
        List<DeepTTEDataSet> data;
        if(Objects.equals(route, "all")) {
            data = new ArrayList<>();
            wazeConfiguration.getTrip().forEach(tripCoordinate -> {
                data.addAll(filesTranformer.transformDeepTte(tripCoordinate.getName(), days));
            });
        } else {
            data = filesTranformer.transformDeepTte(route, days);
        }
        return data;
    }


    private void writeData(ZipOutputStream zipOutputStream, String fileName, List<DeepTTEDataSet> data, String type) throws IOException {
        log.info("Writing data to zip file: {}, size data={}", fileName, data.size());
        List<List<DeepTTEDataSet>> dataSubList = divideIntoSubList(data, MAX_RECORD);
        for(int i = 0; i < dataSubList.size(); i++) {
            writeDataToZip(zipOutputStream, fileName + "_" + i, dataSubList.get(i), type);
        }
    }

    public List<List<DeepTTEDataSet>> divideIntoSubList(List<DeepTTEDataSet> data, int maxSize) {
        List<List<DeepTTEDataSet>> result = new ArrayList<>();
        for(int i = 0; i < data.size(); i+=maxSize) {
            result.add(data.subList(i, Math.min(i + maxSize, data.size())));
        }
        return result;
    }
    private void writeDataToZip(ZipOutputStream zipOutputStream, String fileName, List<DeepTTEDataSet> data, String type) throws IOException {
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipEntry.setSize(data.size());
        zipOutputStream.putNextEntry(zipEntry);
        byte[] result;
        if (type.equals("csv")) {
            result = fileHelpers.processFilesToCsv(data, fileName, true).getBytes();
        } else {
            result = FilesTranformer.writeToByteArray(data);
        }
        zipOutputStream.write(result);
    }
}
