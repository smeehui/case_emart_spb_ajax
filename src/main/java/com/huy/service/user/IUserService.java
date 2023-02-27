package com.huy.service.user;

import com.huy.model.Role;
import com.huy.model.User;
import com.huy.model.dto.UserCreateRequestDTO;
import com.huy.model.dto.UserResponseDTO;
import com.huy.model.dto.UserUpdateRequestDTO;
import com.huy.service.IGeneralService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IUserService extends UserDetailsService, IGeneralService<User> {
    public Optional<User> findByUsername(String username);

    public Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    UserResponseDTO create(UserCreateRequestDTO userCreateRequestDTO);

    UserResponseDTO update(UserUpdateRequestDTO userUpdateRequestDTO, User user, MultipartFile file);

    List<User> findAllByDeletedIsFalse();

    List<User> findAllByDeletedIsFalseAndUsernameNot(String username);

    List<User> findAllByDeletedIsFalseAndRolesNotContainsIgnoreCaseAndUsernameNot(Role role, String username);

}
