package com.huy.model.dto;

import com.huy.model.BaseEntity;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRequestDTO implements Validator {
    private Long id;

    @NotEmpty(message = "Full name is empty")
    private String fullName;

    @NotEmpty(message = "Username is required")
    private String username;

    @NotEmpty(message = "Email is required")
    private String email;

    @NotEmpty(message = "Password is required")
    private String password;

    @NotEmpty(message = "Re-password is required")
    private String rePassword;


    @NotEmpty(message = "Address is required")
    private String address;


    @NotEmpty(message = "Phone is required")
    private String phone;

    private String roles;


    private MultipartFile file;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRequestDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserRequestDTO userDTO = (UserRequestDTO) target;

        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        String rePassword = userDTO.getRePassword();

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
            errors.rejectValue("password","password.format","Password must contain minimum of eight characters, at least one letter and one number");
        } else if (!password.equals(rePassword)) {
            errors.rejectValue("password","password.match","Password and re-password are not similar");
        }

    }

    public void validateEdit(Object target, Errors errors) {
        UserRequestDTO userDTO = (UserRequestDTO) target;
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
