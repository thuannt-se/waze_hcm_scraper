package org.example.waze_hcm_scraper.out;


import lombok.AllArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.example.waze_hcm_scraper.config.HttpClientConfig;
import org.example.waze_hcm_scraper.config.WazeConfiguration;
import org.example.waze_hcm_scraper.domain.Coordinate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.stream.Collectors;


@Component
@AllArgsConstructor
public class WazeClient {
    private final HttpClient httpClient;
    private final WazeConfiguration wazeConfiguration;

    public HttpResponse getRoutingData(String coordinateServer, Map<String, String> options) throws URISyntaxException, IOException {
        HttpGet httpGet = new HttpGet(wazeConfiguration.getHttpUri() + wazeConfiguration.getCoordServers().get(coordinateServer));
        URI uri = new URIBuilder(httpGet.getURI())
                .addParameters(options.entrySet().stream()
                        .map(e -> new BasicNameValuePair(e.getKey(), e.getValue()))
                        .collect(Collectors.toList()))
                .build();
        httpGet.setURI(uri);
        return httpClient.execute(httpGet);
    }

}
