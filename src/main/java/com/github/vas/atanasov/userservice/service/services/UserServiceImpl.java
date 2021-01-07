package com.github.vas.atanasov.userservice.service.services;

import com.github.vas.atanasov.userservice.core.domain.User;
import com.github.vas.atanasov.userservice.core.repositories.UserRepository;
import com.github.vas.atanasov.userservice.service.exceptions.UserExistsException;
import com.github.vas.atanasov.userservice.service.exceptions.UserNotFoundException;
import com.github.vas.atanasov.userservice.service.models.UserCreateServiceModel;
import com.github.vas.atanasov.userservice.service.models.UserServiceModel;
import com.github.vas.atanasov.userservice.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  @Override
  @Transactional
  public UserServiceModel createUser(UserCreateServiceModel model)
      throws UserExistsException, IllegalArgumentException {
    Assert.notNull(model, "UserCreateServiceModel cannot be null");
    Assert.notNull(model.getUsername(), "UserCreateServiceModel.username cannot be null");
    Assert.notNull(model.getFirstName(), "UserCreateServiceModel.firstName cannot be null");
    if (userRepository.existsByUsername(model.getUsername())) throw new UserExistsException();
    User user = User.of(model.getUsername(), model.getFirstName(), model.getLastName());
    log.info("Creating new user: username={}", model.getUsername());
    user = userRepository.save(user);
    return MappingUtils.map(user, UserServiceModel.class);
  }

  @Override
  public Page<UserServiceModel> getUsersPage(Pageable pageable) {
    Assert.notNull(pageable, "Pageable cannot be null");
    return userRepository
        .findAll(pageable)
        .map(user -> MappingUtils.map(user, UserServiceModel.class));
  }

  @Override
  @Transactional
  public void deleteUserByUid(UUID uid) {
    Assert.notNull(uid, "UUID cannot be null");
    User user = userRepository.findByUid(uid).orElseThrow(UserNotFoundException::new);
    userRepository.delete(user);
  }
}
