package org.example.crm_system.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFilter {
  private Integer year = null;
  private Integer month = null;
  private String type = null;
  private String currency = null;
  private boolean waitingForLogin;


}
