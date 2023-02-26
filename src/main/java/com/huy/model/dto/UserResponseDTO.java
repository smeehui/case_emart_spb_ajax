package com.huy.model.dto;

import com.huy.model.UserAvatar;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponseDTO {
    private Long id;

    private String fullName;

    private String username;

    private String email;

    private String address;

    private String phone;

    private Set<RoleDTO> roles;

    private UserAvatarDTO userAvatar;
}
