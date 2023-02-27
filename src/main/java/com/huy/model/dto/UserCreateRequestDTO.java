package com.huy.model.dto;

import com.huy.exception.DataInputException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCreateRequestDTO implements Validator {

    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String password;
    private String rePassword;
    private String address;
    private String phone;
    private String roles;

    private MultipartFile file;


    @Override
    public boolean supports(Class<?> clazz) {
        return UserCreateRequestDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserCreateRequestDTO userDTO = (UserCreateRequestDTO) target;

        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        String rePassword = userDTO.getRePassword();

        int min = 5;
        int max = 10;
        if (username == null || username.length() < min || username.length() > max) {
            errors.rejectValue("username", "username.length", "Username's characters must be between 5 and 10");
        } else {
            if (!username.matches("[a-z]+")) {
                errors.rejectValue("username", "username.format", "Username must be text only");
            }
        }

        if (password == null || password.trim().equals("")) {
            errors.rejectValue("password","password.empty","Password is required");
        }
        else if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            errors.rejectValue("password","password.format","Password must contain minimum of eight characters, at least one letter and one number");
        }
        else if (!password.equals(rePassword)) {
            errors.rejectValue("password","password.match","Password and re-password are not similar");
        }

    }

    public void validateEdit(Object target, Errors errors) {
        UserCreateRequestDTO userDTO = (UserCreateRequestDTO) target;

        String fullName = userDTO.getFullName();
        String email = userDTO.getEmail();
        String address = userDTO.getAddress();
        String phone = userDTO.getPhone();
        MultipartFile file = userDTO.getFile();

        long min = 4l;
        long max = 100l;
        if (fullName == null || fullName.length() < min || fullName.length() > max) {
            errors.rejectValue("fullName", "fullName.length", "Full name's characters must be between 4 and 100");
        } else {
            if (!fullName.matches("([a-zA-Z]+ ?)+")) {
                errors.rejectValue("fullName", "fullName.format", "Full name must be text only");
            }
        }
        if (email == null || email.length() < min || email.length() > max) {
            errors.rejectValue("email", "email.length", "Email's characters must be between 4 and 100");
        } else {
            if (!email.matches("[a-z]+@[a-z]+\\.[a-z]{2,}")) {
                errors.rejectValue("email", "email.format", "Email must be text only");
            }
        }
        if (phone == null || phone.length() < min || phone.length() > max) {
            errors.rejectValue("phone", "phone.length", "Phone's characters must be between 4 and 100");
        }

        if (address == null || address.length() < min || address.length() > max) {
            errors.rejectValue("address", "address.length", "Address's characters must be between 4 and 100");
        }

        if (file != null && !file.isEmpty()) {
            long fileSize = file.getSize();

            if (fileSize > 512000) {
                throw new DataInputException("Max file size is 500KB");
            }
        }
    }
}
