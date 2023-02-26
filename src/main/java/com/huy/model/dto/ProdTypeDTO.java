package com.huy.model.dto;


import com.huy.model.enums.EProdType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProdTypeDTO {

    @NotNull(message = "Role is required")
    private Long id;

    private EProdType prodType;

}
