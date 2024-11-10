package org.example.waze_hcm_scraper.out;


import lombok.AllArgsConstructor;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.example.waze_hcm_scraper.config.WazeConfiguration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
@AllArgsConstructor
public class WazeClient {
    private final HttpClient httpClient;
    private final WazeConfiguration wazeConfiguration;

    public HttpResponse getRoutingData(String coordinateServer, Map<String, String> options, Map<String, String> headerMap) throws URISyntaxException, IOException {
        HttpGet httpGet = new HttpGet(wazeConfiguration.getHttpUri() + wazeConfiguration.getRoutingServers().get(coordinateServer));
        URI uri = new URIBuilder(httpGet.getURI())
                .addParameters(options.entrySet().stream()
                        .map(e -> new BasicNameValuePair(e.getKey(), e.getValue()))
                        .collect(Collectors.toList()))
                .build();
        List<Header> headers = new ArrayList<>();
        for(Map.Entry<String, String> entry : headerMap.entrySet()) {
            headers.add(new BasicHeader(entry.getKey(), entry.getValue()));
        }

        httpGet.setHeaders(headers.toArray(Header[]::new));
        httpGet.setURI(uri);
        var response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == 200) {
            return response;
        } else {
            throw new IOException("Failed to get routing data from Waze");
        }
    }

}
