package com.github.vas.atanasov.userservice.service.models;

import lombok.Data;

@Data
public class UserCreateServiceModel {
  private String username;
  private String firstName;
  private String lastName;
}
