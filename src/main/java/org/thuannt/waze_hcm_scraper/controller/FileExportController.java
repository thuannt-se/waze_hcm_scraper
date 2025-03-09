package org.thuannt.waze_hcm_scraper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thuannt.waze_hcm_scraper.service.FileGeneratorService;

@RequiredArgsConstructor
@RestController
public class FileExportController {

    private final FileGeneratorService fileGeneratorService;
    @GetMapping("/export/deeptte/records")
    public ResponseEntity<byte[]> exportDeepTteRecords(@RequestParam String route,
                                                       @RequestParam double trainRatio,
                                                       @RequestParam double testRatio,
                                                       @RequestParam long days) {
        byte[] compressedData = fileGeneratorService.generateDeepTteTrainDataset(route, days, trainRatio, testRatio );

        if (compressedData == null || compressedData.length == 0) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // or handle appropriately
        }

        // Prepare the headers for the response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "data.zip"); // Set the file name
        headers.setContentLength(compressedData.length);

        // Return the ResponseEntity with the byte array
        return new ResponseEntity<>(compressedData, headers, HttpStatus.OK);
    }
}
