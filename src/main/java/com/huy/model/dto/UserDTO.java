package com.huy.model.dto;

import com.huy.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO extends BaseEntity implements Validator {
    private Long id;

    @NotEmpty(message = "Full name is empty")
    private String fullName;

    @NotEmpty(message = "Username is required")
    private String username;

    @NotEmpty(message = "Email is required")
    private String email;

    @NotEmpty(message = "Password is required")
    private String password;

    @NotEmpty(message = "Address is required")
    private String address;


    @NotEmpty(message = "Phone is required")
    private String phone;

    @Valid
    private Set<RoleDTO> roles;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO userDTO = (UserDTO) target;

        String username = userDTO.getUsername();
        String password = userDTO.getPassword();

        int min = 5;
        int max = 10;
        if (username.length() < min || username.length() > max) {
            errors.rejectValue("username","username.length","Username's characters must be between 5 and 10");
        }
        else {
            if (!username.matches("[a-z]+")) {
                errors.rejectValue("username","username.format","Username must be text only");
            }
        }

        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            errors.rejectValue("password","password.format","Password must contain minimum of eight characters, at least one letter and one number:");
        }

    }
}
