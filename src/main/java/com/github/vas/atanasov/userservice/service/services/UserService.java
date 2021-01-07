package com.github.vas.atanasov.userservice.service.services;

import com.github.vas.atanasov.userservice.service.exceptions.UserExistsException;
import com.github.vas.atanasov.userservice.service.models.UserCreateServiceModel;
import com.github.vas.atanasov.userservice.service.models.UserServiceModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
  UserServiceModel createUser(UserCreateServiceModel model)
      throws UserExistsException, IllegalArgumentException;

  Page<UserServiceModel> getUsersPage(Pageable pageable);

  void deleteUserByUid(UUID uid);
}
