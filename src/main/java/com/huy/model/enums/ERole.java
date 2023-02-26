package com.huy.model.enums;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
public enum ERole {
    ROLE_ADMIN(1L),
    ROLE_MODERATOR(2L),
    ROLE_USER(3L),
    ;

    private Long value;

    ERole(Long val) {
        this.value = val;
    }

}
