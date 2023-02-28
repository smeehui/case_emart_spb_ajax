package com.huy.model.enums;

import lombok.Getter;

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
