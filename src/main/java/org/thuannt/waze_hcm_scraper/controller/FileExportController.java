package org.thuannt.waze_hcm_scraper.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileExportController {

    @GetMapping("/export/deeptte/records")
    public ResponseEntity<byte[]> exportDeepTteRecords(@RequestParam String route,
                                                       @RequestParam double trainRatio,
                                                       @RequestParam long days) {
        //TODO: integrate with the service
        return null;
    }
}
