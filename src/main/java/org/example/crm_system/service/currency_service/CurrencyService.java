package org.example.crm_system.service.currency_service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyService {
  RestTemplate restTemplate = new RestTemplate();
  Map<String, String> params = new HashMap<String, String>();
  String url = "https://cbu.uz/ru/arkhiv-kursov-valyut/json/";

  @Transactional
  public Map<String, String> getCurrency() {
    try {
      Map[] response = restTemplate.getForObject(url, Map[].class);
      if (response != null) {
        for (Map map : response) {
          if (map.get("Ccy").equals("USD")) {
            params.put("USD", map.get("Rate").toString());
          }
          if (map.get("Ccy").equals("RUB")) {
            params.put("RUB", map.get("Rate").toString());
          }
        }
      }
      return params;
    } catch (Exception e) {
      e.printStackTrace();
    }
    throw new RuntimeException("Failed to fetch currency data");
  }
}
