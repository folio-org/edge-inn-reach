package org.folio.edge.domain.service;

import java.util.Optional;
import java.util.UUID;

import org.folio.edge.domain.dto.systemuser.User;

public interface UserService {

  Optional<User> getUserById(UUID id);

  Optional<User> getUserByName(String name);

  User saveUser(User user);

}
