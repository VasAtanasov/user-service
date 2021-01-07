package com.github.vas.atanasov.userservice.service.services;

import com.github.vas.atanasov.userservice.core.domain.User;
import com.github.vas.atanasov.userservice.core.repositories.UserRepository;
import com.github.vas.atanasov.userservice.service.exceptions.UserExistsException;
import com.github.vas.atanasov.userservice.service.exceptions.UserNotFoundException;
import com.github.vas.atanasov.userservice.service.models.UserCreateServiceModel;
import com.github.vas.atanasov.userservice.service.models.UserServiceModel;
import com.github.vas.atanasov.userservice.utils.MappingUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @InjectMocks UserServiceImpl userService;

  @Mock UserRepository userRepository;

  @Test
  void createUser_withValidRequest_Uid_IsNotNull() {
    UserCreateServiceModel model = new UserCreateServiceModel();
    model.setUsername("username");
    model.setFirstName("firstName");

    when(userRepository.save(any(User.class))).thenReturn(MappingUtils.map(model, User.empty()));

    UserServiceModel createdUse = userService.createUser(model);

    assertThat(createdUse.getUid()).isNotNull();

    verify(userRepository).existsByUsername(any());
    verify(userRepository).save(any());
  }

  @Test
  void createUser_withExistingUsername_shouldThrow() {
    UserCreateServiceModel model = new UserCreateServiceModel();
    model.setUsername("username");
    model.setFirstName("firstName");
    when(userRepository.existsByUsername(any())).thenReturn(Boolean.TRUE);
    assertThatThrownBy(() -> userService.createUser(model)).isInstanceOf(UserExistsException.class);

    verify(userRepository).existsByUsername(any());
  }

  @Test
  void deleteUserByUid_withNonExistingUUID_shouldThrow() {

    when(userRepository.findByUid(any())).thenReturn(Optional.empty());
    assertThatThrownBy(() -> userService.deleteUserByUid(UUID.randomUUID()))
        .isInstanceOf(UserNotFoundException.class);

    verify(userRepository).findByUid(any());
  }
}
