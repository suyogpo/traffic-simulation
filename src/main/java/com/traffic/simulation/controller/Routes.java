package com.traffic.simulation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class Routes {

  @RequestMapping("/getTrafficData")
  public void getTrafficData() {
    log.info("Successfully called first rest api");
  }
}
