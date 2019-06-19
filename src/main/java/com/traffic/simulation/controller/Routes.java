package com.traffic.simulation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@Slf4j
public class Routes {

  @Value("${app.path_traffic}")
  private String RESOURCE_PATH_TRAFFIC;

  @Value("${app.traffic}")
  private String TRAFFIC_SERVICE_URL;

  @Value("${app.traffic_key}")
  private String TRAFFIC_SERVICE_KEY;

  @Value("${app.path_raah}")
  private String RESOURCE_PATH_RAAH;

  @Value("${app.raah}")
  private String RAAH_SERVICE_URL;

  @Value("${app.raah_key}")
  private String RAAH_SERVICE_KEY;

  @Autowired
  RestTemplate restTemplate;

  @RequestMapping("/getTrafficData")
  public void getTrafficData() {
    log.info("Successfully called first rest api");
  }

  @GetMapping(value = "traffic/{z}/{x}/{y}",
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<Resource> getTraffic(@PathVariable
                                                 String z, @PathVariable String x,
                                             @PathVariable String y)
      throws IOException {
    log.info("Successfully called rest api {}, {}, {}", z, x, y);

    ResponseEntity<Resource> response = process(z, x, y, TRAFFIC_SERVICE_URL, TRAFFIC_SERVICE_KEY,
        RESOURCE_PATH_TRAFFIC);

    return response;
  }


  @GetMapping(value = "raah/{z}/{x}/{y}",
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<Resource> getRaah(@PathVariable
                                              String z, @PathVariable String x,
                                          @PathVariable String y)
      throws IOException {
    log.info("Successfully called rest api {}, {}, {}", z, x, y);

    ResponseEntity<Resource> response = process(z, x, y, RAAH_SERVICE_URL, RAAH_SERVICE_KEY,
        RESOURCE_PATH_RAAH);
    return response;
  }

  private ResponseEntity<Resource> process(String z, String x, String y, String SERVICE_URL,
                                           String KEY, String RESOURCE_PATH)
      throws IOException {

    ResponseEntity<Resource> response = null;
    File file = new File(RESOURCE_PATH + "\\" + y);

    if (file.exists()) {
      //send loaded file
      InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
      response = getFileResponse(file, resource);
    } else {
      //send file from actual web-service

      String url = SERVICE_URL + "/" + z + "/" + x + "/" + y;
      HttpHeaders headers = new HttpHeaders();
      headers.set("Accept", MediaType.APPLICATION_OCTET_STREAM_VALUE);
      HttpEntity<?> entity = new HttpEntity<>(headers);

      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
          .queryParam("key", KEY);

      response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
          entity, Resource.class);

      response = getFileResponse(response.getBody(), y);
    }


    return response;
  }

  private ResponseEntity<Resource> getFileResponse(File file, InputStreamResource resource) {
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
        .header("Cache-Control", "no-cache, no-store, must-revalidate")
        .header("Pragma", "no-cache")
        .header("Expires", "0")
        .contentLength(file.length())
        .contentType(MediaType.parseMediaType("application/octet-stream"))
        .body(resource);
  }

  private ResponseEntity<Resource> getFileResponse(Resource resource, String fileName)
      throws IOException {

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
        .header("Cache-Control", "no-cache, no-store, must-revalidate")
        .header("Pragma", "no-cache")
        .header("Expires", "0")
        .contentLength(resource.contentLength())
        .contentType(MediaType.parseMediaType("application/octet-stream"))
        .body(resource);
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
