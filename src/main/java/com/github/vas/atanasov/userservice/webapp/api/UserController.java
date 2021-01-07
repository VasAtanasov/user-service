package com.github.vas.atanasov.userservice.webapp.api;

import com.github.vas.atanasov.userservice.service.exceptions.UserExistsException;
import com.github.vas.atanasov.userservice.service.exceptions.UserNotFoundException;
import com.github.vas.atanasov.userservice.service.models.UserCreateServiceModel;
import com.github.vas.atanasov.userservice.service.models.UserServiceModel;
import com.github.vas.atanasov.userservice.service.services.UserService;
import com.github.vas.atanasov.userservice.utils.MappingUtils;
import com.github.vas.atanasov.userservice.webapp.enums.RestMessage;
import com.github.vas.atanasov.userservice.webapp.models.request.UserCreateRequest;
import com.github.vas.atanasov.userservice.webapp.models.wrappers.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

import static com.github.vas.atanasov.userservice.webapp.enums.RestMessage.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(UserController.URL_USER_BASE)
public class UserController implements BaseController {
  public static final String URL_USER_BASE = "/v1/users";

  private final UserService userService;

  @ResponseStatus(value = HttpStatus.CREATED)
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> createUser(
      @Valid @RequestBody UserCreateRequest request, BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      log.error("error! binding result has errors: {}", bindingResult);
      ResponseWrapper wrapper =
          failureResponse(USER_CREATION_INVALID_INPUT, mapErrors(bindingResult.getFieldErrors()));
      return ResponseEntity.badRequest().body(wrapper);
    }

    try {
      UserServiceModel user =
          userService.createUser(MappingUtils.map(request, UserCreateServiceModel.class));
      log.info("Created user with username={} and id={}", user.getUsername(), user.getUid());
      URI location =
          ServletUriComponentsBuilder.fromCurrentContextPath()
              .path(URL_USER_BASE + "/" + user.getUid())
              .buildAndExpand()
              .toUri();
      ResponseWrapper wrapper = successResponse(USER_CREATED, user);
      return ResponseEntity.created(location).body(wrapper);
    } catch (UserExistsException userExistsException) {
      log.error("User with username={} already exists", request.getUsername());
      ResponseWrapper wrapper = failureResponse(USER_ALREADY_EXISTS);
      return ResponseEntity.status(HttpStatus.CONFLICT).body(wrapper);
    }
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getUsersPage(
      @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = SORT, direction = Sort.Direction.DESC)
          Pageable pageable) {
    Page<UserServiceModel> page = userService.getUsersPage(pageable);
    return ResponseEntity.ok(page);
  }

  @DeleteMapping(value = "/{uid}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public ResponseEntity<?> deleteUser(@PathVariable UUID uid) {
    try {
      userService.deleteUserByUid(uid);
      return ResponseEntity.noContent().build();
    } catch (UserNotFoundException userNotFoundException) {
      ResponseWrapper wrapper = failureResponse(RestMessage.USER_NOT_FOUND);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(wrapper);
    }
  }
}
