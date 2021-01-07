package com.github.vas.atanasov.userservice.core.domain;

import lombok.Getter;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(
    name = "user",
    indexes = {@Index(name = "idx_user_uid", columnList = "uid")})
public class User implements HasID<Long> {

  public static final int MAX_USERNAME_LENGTH = 50;
  public static final int MAX_NAME_LENGTH = 30;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  private Long id;

  @Column(name = "uid", nullable = false, unique = true, columnDefinition = "BINARY(16)")
  private UUID uid;

  @Column(name = "username", nullable = false, unique = true, length = MAX_USERNAME_LENGTH)
  private String username;

  @Column(name = "first_name", nullable = false, length = MAX_NAME_LENGTH)
  private String firstName;

  @Column(name = "last_name", length = MAX_NAME_LENGTH)
  private String lastName;

  @Column(name = "created_date_time", updatable = false, nullable = false)
  private LocalDateTime createdDateTime;

  protected User() {}

  public static User empty() {
    User user = new User();
    user.uid = UUID.randomUUID();
    return user;
  }

  public static User of(String username, String firstName, String lastName) {
    if (!StringUtils.hasLength(username) && !StringUtils.hasLength(firstName)) {
      throw new IllegalArgumentException("Username and First Name cannot both be null!");
    }
    User user = empty();
    user.username = username;
    user.firstName = firstName;
    user.lastName = StringUtils.hasLength(lastName) ? lastName : null;
    user.createdDateTime = LocalDateTime.now();
    return user;
  }

  @PreUpdate
  @PrePersist
  public void updateTimeStamps() {
    if (createdDateTime == null) {
      createdDateTime = LocalDateTime.now();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User)) return false;
    User user = (User) o;
    return getUid() != null ? getUid().equals(user.getUid()) : user.getUid() == null;
  }

  @Override
  public int hashCode() {
    return getUid() != null ? getUid().hashCode() : 0;
  }
}
