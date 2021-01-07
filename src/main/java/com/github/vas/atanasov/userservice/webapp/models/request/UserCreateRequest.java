package com.github.vas.atanasov.userservice.webapp.models.request;

import com.github.vas.atanasov.userservice.core.domain.User;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserCreateRequest {

  @Size(min = 5, max = User.MAX_USERNAME_LENGTH)
  @NotBlank(message = "Username is required")
  private String username;

  @Size(max = User.MAX_NAME_LENGTH)
  @NotBlank(message = "First name is required")
  private String firstName;

  @Size(max = User.MAX_NAME_LENGTH)
  private String lastName;
}
