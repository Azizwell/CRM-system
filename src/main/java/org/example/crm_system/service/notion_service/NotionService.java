package org.example.crm_system.service.notion_service;

import org.example.crm_system.entity.Type;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class NotionService {
  private final String notionToken = "secret_XkiYn0VRBnl9p2VXkIxQmc8nHaGBfdobu7paMvSwdEF";
  private final String databaseId = "176a2047fa93801b9b1bed2777704665";
  private final RestTemplate restTemplate = new RestTemplate();

  public void saveTransactionToNotion(
          UUID id, Type type, Long categoryId, String clientId,
          String description, BigDecimal amount, BigDecimal amountInUZS, String currency) {

    String url = "https://api.notion.com/v1/pages";

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + notionToken);
    headers.add("Content-Type", "application/json");
//    headers.add("Notion-Version", "2022-06-28");

    Map<String, Object> requestBody = new HashMap<>();

    Map<String, Object> parent = new HashMap<>();
    parent.put("database_id", databaseId);
    requestBody.put("parent", parent);

    Map<String, Object> properties = new HashMap<>();

    properties.put("Id", Map.of(
            "title", List.of(Map.of("text", Map.of("content", id.toString())))
    ));

    properties.put("Type", Map.of(
            "select", Map.of("name", type.name())
    ));

    properties.put("Currency", Map.of(
            "rich_text", List.of(Map.of("text", Map.of("content", currency)))
    ));

    properties.put("Amount", Map.of("number", amount));
    properties.put("AmountInUZS", Map.of("number", amountInUZS));

    properties.put("CategoryId", Map.of(
            "rich_text", List.of(Map.of("text", Map.of("content", categoryId.toString())))
    ));

    properties.put("ClientId", Map.of(
            "rich_text", List.of(Map.of("text", Map.of("content", clientId)))
    ));

    properties.put("Description", Map.of(
            "rich_text", List.of(Map.of("text", Map.of("content", description)))
    ));

    requestBody.put("properties", properties);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

    try {
      ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

      System.out.println("Response from Notion: " + response.getBody());
    } catch (Exception e) {
      System.err.println("Error occurred while sending request to Notion: " + e.getMessage());
      e.printStackTrace();
    }
  }

}

