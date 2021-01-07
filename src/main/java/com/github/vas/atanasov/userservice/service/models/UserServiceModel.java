package com.github.vas.atanasov.userservice.service.models;

import com.github.vas.atanasov.userservice.core.domain.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserServiceModel {
  private UUID uid;
  private String username;
  private String firstName;
  private String lastName;
  private LocalDateTime createdDateTime;

  public static UserServiceModel from(User user) {
    UserServiceModel model = new UserServiceModel();
    model.setUid(user.getUid());
    model.setUsername(user.getUsername());
    model.setFirstName(user.getFirstName());
    model.setLastName(user.getLastName());
    model.setCreatedDateTime(user.getCreatedDateTime());
    return model;
  }
}
