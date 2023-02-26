package com.huy.service.user;

import com.huy.model.User;
import com.huy.model.dto.UserRequestDTO;
import com.huy.model.dto.UserResponseDTO;
import com.huy.service.IGeneralService;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface IUserService extends UserDetailsService, IGeneralService<User> {
    public Optional<User> findByUsername(String username);

    public  Optional<User> findByEmail(String email);

    UserResponseDTO create(UserRequestDTO userRequestDTO);

    void update(UserRequestDTO userRequestDTO,User user);
}
