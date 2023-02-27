package com.huy.model.enums;

import lombok.Getter;

import javax.annotation.processing.Generated;

@Getter
public enum EProdType {
    BEST_SELLER(1L),
    PROMOTING(2L),
    MODERATE(3L),
    SLUMPED(4L);
    private Long value;

    EProdType(Long value) {
        this.value = value;
    }
}
