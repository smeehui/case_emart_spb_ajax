package com.huy.controller.api;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huy.exception.DataInputException;
import com.huy.model.Role;
import com.huy.model.User;
import com.huy.model.dto.RoleDTO;
import com.huy.model.dto.UserRequestDTO;
import com.huy.model.dto.UserResponseDTO;
import com.huy.service.user.IUserService;
import com.huy.utils.AppUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserAPI {

    @Autowired
    private IUserService userService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AppUtils appUtil;


    @GetMapping
    public ResponseEntity<?> getAllUser() {

        List<User> users = userService.findAll();
        List<UserResponseDTO> userResponseDTOS = users.stream().map(user -> modelMapper.map(user, UserResponseDTO.class)).toList();

        return new ResponseEntity<>(userResponseDTOS, HttpStatus.OK);

    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {

        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            throw new DataInputException("User not found");
        }

        User user = userOptional.get();

        UserResponseDTO userResponseDTO = modelMapper.map(user, UserResponseDTO.class);

        return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
    }

    @PatchMapping("/edit/{userID}")
    public ResponseEntity<?> editUserBy(@PathVariable Long userID, UserRequestDTO userRequestDTO, BindingResult bindingResult) {

        String username = appUtil.getUsernamePrincipal();
        Optional<User> userUsernameOpt = userService.findByUsername(username);
        if (userUsernameOpt.isEmpty()) {
            throw new DataInputException("User not found");
        }

        Optional<User> userByIdOptional = userService.findById(userID);


        if (userByIdOptional.isEmpty()) {
            throw new DataInputException("User not found");
        }
        User user = userUsernameOpt.get();
        userRequestDTO.setUsername(username);
        new UserRequestDTO().validateEdit(userRequestDTO, bindingResult);


        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (!fieldError.getField().equals("file")) return appUtil.mapErrorToResponse(bindingResult);
        }

        Optional<User> userEmailOpt = userService.findByEmail(userRequestDTO.getEmail());

        if (userEmailOpt.isPresent()) {
        if (!userEmailOpt.get().getUsername().equals(username)) {
            throw new DataInputException("Email is existed");
            }
        }

        UserResponseDTO userResponseDTO = userService.update(userRequestDTO,user);

        return new ResponseEntity<>(userResponseDTO,HttpStatus.OK);
    }

    private Set<Role> getUserRole(UserRequestDTO userDTO) {
        String roleDTOs = userDTO.getRoles();
        if (roleDTOs == null) {
            throw new DataInputException("User roles is null");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Set<RoleDTO> roleDTOSet;
        try {
            roleDTOSet = objectMapper.readValue(roleDTOs, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new DataInputException("Role is invalid");
        }
        return roleDTOSet.stream().map(role -> modelMapper.map(role,Role.class)).collect(Collectors.toSet());
    }
    @PostMapping
    public ResponseEntity<?> doCreate(@Validated UserRequestDTO userRequestDTO, BindingResult bindingResult) {

        new UserRequestDTO().validate(userRequestDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            FieldError fieldError =  bindingResult.getFieldError();
            if (!fieldError.getField().equals("file")) return appUtil.mapErrorToResponse(bindingResult);
        }


        if (userService.findByUsername(userRequestDTO.getUsername()).isPresent()) {
            throw new DataInputException("User name is existed!");
        }

        if (userService.findByEmail(userRequestDTO.getEmail()).isPresent()) {
            throw new DataInputException("Email is existed!");
        }

        UserResponseDTO userResponseDTO = userService.create(userRequestDTO);

        return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
    }
}
