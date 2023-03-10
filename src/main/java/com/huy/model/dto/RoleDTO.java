package com.huy.model.dto;

import com.huy.model.enums.ERole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoleDTO {
    @NotNull(message = "Role is required")
    private Long id;

    private ERole name;
}
