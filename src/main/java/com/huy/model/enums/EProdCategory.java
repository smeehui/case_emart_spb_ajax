package com.huy.model.enums;


import lombok.Getter;

@Getter
public enum EProdCategory {
    CELLPHONE("CELLPHONE"),
    LAPTOP("LAPTOP"),
    TABLET("TABLET"),
    PC("PC"),;

    EProdCategory(String value) {
    }
}
