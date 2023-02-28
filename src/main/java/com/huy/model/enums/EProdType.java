package com.huy.model.enums;

import lombok.Getter;

@Getter
public enum EProdType {
    BEST_SELLER(1L),
    PROMOTING(2L),
    REGULAR(3L),
    SLUMPED(4L);
    private Long value;

    EProdType(Long value) {
        this.value = value;
    }
}
