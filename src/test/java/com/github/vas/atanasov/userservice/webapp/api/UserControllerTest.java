package com.github.vas.atanasov.userservice.webapp.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.github.vas.atanasov.userservice.core.domain.User;
import com.github.vas.atanasov.userservice.service.exceptions.UserExistsException;
import com.github.vas.atanasov.userservice.service.exceptions.UserNotFoundException;
import com.github.vas.atanasov.userservice.service.models.UserCreateServiceModel;
import com.github.vas.atanasov.userservice.service.models.UserServiceModel;
import com.github.vas.atanasov.userservice.service.services.UserService;
import com.github.vas.atanasov.userservice.utils.MappingUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.github.vas.atanasov.userservice.webapp.enums.RestMessage.*;
import static com.github.vas.atanasov.userservice.webapp.enums.RestStatus.FAILURE;
import static com.github.vas.atanasov.userservice.webapp.enums.RestStatus.SUCCESS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private UserService userService;

  static Faker faker = new Faker();
  static List<User> fakeUsers = new ArrayList<>();
  static User fakeUser =
      User.of(
          faker.name().username() + faker.lorem().characters(5),
          faker.name().firstName(),
          faker.name().lastName());

  @BeforeAll
  static void init() {
    for (int i = 0; i < 20; i++) {
      User user =
          User.of(
              faker.name().username() + faker.lorem().characters(5),
              faker.name().firstName(),
              faker.name().lastName());
      fakeUsers.add(user);
    }
  }

  @Test
  void createUser_withValidData_createsUserAndReturnsCorrectStatusAndMessage() throws Exception {
    UserCreateServiceModel request = MappingUtils.map(fakeUser, UserCreateServiceModel.class);

    when(userService.createUser(request)).thenReturn(UserServiceModel.from(fakeUser));

    mockMvc
        .perform(
            post(UserController.URL_USER_BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status", is(String.valueOf(SUCCESS))))
        .andExpect(jsonPath("$.message", is(String.valueOf(USER_CREATED))));

    verify(userService).createUser(request);
  }

  @Test
  void createUser_withExistingUsername_returnsCorrectStatusAndMessage() throws Exception {
    UserCreateServiceModel request = MappingUtils.map(fakeUser, UserCreateServiceModel.class);

    when(userService.createUser(request)).thenThrow(new UserExistsException());

    mockMvc
        .perform(
            post(UserController.URL_USER_BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status", is(String.valueOf(FAILURE))))
        .andExpect(jsonPath("$.message", is(String.valueOf(USER_ALREADY_EXISTS))));

    verify(userService).createUser(request);
  }

  @Test
  void createUser_withInvalidUsername_returnsCorrectStatusAndMessage() throws Exception {
    UserCreateServiceModel request = MappingUtils.map(fakeUser, UserCreateServiceModel.class);
    request.setUsername("shrt");

    mockMvc
        .perform(
            post(UserController.URL_USER_BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(String.valueOf(FAILURE))))
        .andExpect(jsonPath("$.message", is(String.valueOf(USER_CREATION_INVALID_INPUT))));
  }

  @Test
  void createUser_withVeryLongFirstName_returnsCorrectStatusAndMessage() throws Exception {
    UserCreateServiceModel request = MappingUtils.map(fakeUser, UserCreateServiceModel.class);
    request.setFirstName(faker.lorem().characters(50));

    mockMvc
        .perform(
            post(UserController.URL_USER_BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(String.valueOf(FAILURE))))
        .andExpect(jsonPath("$.message", is(String.valueOf(USER_CREATION_INVALID_INPUT))));
  }

  @Test
  void deleteUser_withExistingId_returnsCorrectStatus() throws Exception {
    mockMvc
        .perform(delete(UserController.URL_USER_BASE + "/" + UUID.randomUUID().toString()))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteUser_nonExistingUserId_returnsCorrectStatusAndMessage() throws Exception {
    UUID userId = UUID.randomUUID();
    doThrow(new UserNotFoundException()).when(userService).deleteUserByUid(userId);
    mockMvc
        .perform(delete(UserController.URL_USER_BASE + "/" + userId.toString()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(String.valueOf(FAILURE))))
        .andExpect(jsonPath("$.message", is(String.valueOf(USER_NOT_FOUND))));
  }

  @Test
  void getUsersPage_withData_shouldReturnPage() throws Exception {
    List<UserServiceModel> fakeModels = MappingUtils.mapAll(fakeUsers, UserServiceModel.class);
    PageRequest pageRequest = PageRequest.of(0, fakeModels.size());
    Page<UserServiceModel> page = new PageImpl<>(fakeModels, pageRequest, fakeModels.size());

    when(userService.getUsersPage(any())).thenReturn(page);

    mockMvc
        .perform(get(UserController.URL_USER_BASE).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(fakeModels.size())))
        .andExpect(jsonPath("$.totalElements", is((int) page.getTotalElements())));
  }
}
