package com.huy.model.enums;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
public enum EProdCategory {
    CELLPHONE("CELLPHONE"),
    LAPTOP("LAPTOP"),
    TABLET("TABLET"),
    PC("PC"),;

    EProdCategory(String value) {
    }
}
