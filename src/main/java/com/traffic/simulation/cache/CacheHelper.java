package com.traffic.simulation.cache;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class CacheHelper {

  private boolean loadFromDirectory;
}
