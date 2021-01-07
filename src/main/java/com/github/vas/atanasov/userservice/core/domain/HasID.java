package com.github.vas.atanasov.userservice.core.domain;

import java.util.UUID;

public interface HasID<T> {
  T getId();

  UUID getUid();
}
