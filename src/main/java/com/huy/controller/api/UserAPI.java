package com.huy.controller.api;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huy.exception.DataInputException;
import com.huy.exception.UnauthorizedProcess;
import com.huy.model.Role;
import com.huy.model.User;
import com.huy.model.dto.RoleDTO;
import com.huy.model.dto.UserCreateRequestDTO;
import com.huy.model.dto.UserResponseDTO;
import com.huy.model.dto.UserUpdateRequestDTO;
import com.huy.model.enums.ERole;
import com.huy.repository.RoleRepository;
import com.huy.service.role.IRoleService;
import com.huy.service.user.IUserService;
import com.huy.utils.AppUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserAPI {

    @Autowired
    private IUserService userService;

    @Autowired
    IRoleService roleService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AppUtils appUtil;
    @Autowired
    private RoleRepository roleRepository;


    @GetMapping
    public ResponseEntity<?> getAllUser() {

        String username = appUtil.getUsernamePrincipal();

        User currentUser = userService.findByUsername(username).get();

        List<User> users = null;

        Role role = currentUser.getRoles().iterator().next();
        ERole roleName = role.getName();
        switch (roleName) {
            case ROLE_ADMIN -> {
                users = userService.findAllByDeletedIsFalseAndUsernameNot(username);
            }
            case ROLE_MODERATOR -> {
                Role adminRole = roleService.findById(1l).get();
                users = userService.findAllByDeletedIsFalseAndRolesNotContainsIgnoreCaseAndUsernameNot(adminRole, username);
            }
        }
        if (users == null) throw new UnauthorizedProcess("You are not authorized");
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
    public ResponseEntity<?> editUserBy(@PathVariable Long userID, MultipartFile file, UserUpdateRequestDTO userUpdateRequestDTO, BindingResult bindingResult) {

//        MultipartFile file = userCreateRequestDTO.getFile();

        if (file != null && !file.isEmpty()) {
            long fileSize = file.getSize();

            if (fileSize > 512000) {
                throw new DataInputException("Max file size is 500KB");
            }
        }

        if (bindingResult.hasErrors()) {
            return appUtil.mapErrorToResponse(bindingResult);
        }

        String username = appUtil.getUsernamePrincipal();

        Optional<User> modifyingUserUsernameOpt = userService.findByUsername(userUpdateRequestDTO.getUsername());
        if (modifyingUserUsernameOpt.isEmpty()) {
            throw new DataInputException("User not found");
        }

        Optional<User> userByIdOptional = userService.findById(userID);

        User currentUser = userService.findByUsername(username).get();


        if (userByIdOptional.isEmpty()) {
            throw new DataInputException("User not found");
        }
        User oldUser = userByIdOptional.get();


        Set<Role> newUserRoles = getUserRole(userUpdateRequestDTO);
        Role modifyingRole = newUserRoles.iterator().next();
        boolean isAuthoritiesValid = validateAuthorities(currentUser, modifyingRole, oldUser);

        if (!isAuthoritiesValid) {
            throw new UnauthorizedProcess("You are not authorized to proceed");
        }


        User user = modifyingUserUsernameOpt.get();
        new UserUpdateRequestDTO().validateEdit(userUpdateRequestDTO, bindingResult);



        Optional<User> userEmailOpt = userService.findByEmail(userUpdateRequestDTO.getEmail());
        Optional<User> userPhoneOpt = userService.findByPhone(userUpdateRequestDTO.getPhone());

        if (userEmailOpt.isPresent()) {
            if (!userEmailOpt.get().getUsername().equals(userUpdateRequestDTO.getUsername())) {
                throw new DataInputException("Email is existed");
            }
        }
        if (userPhoneOpt.isPresent()) {
            if (!userPhoneOpt.get().getUsername().equals(userUpdateRequestDTO.getUsername())) {
                throw new DataInputException("Phone is existed");
            }
        }

        user.setRoles(newUserRoles);
        UserResponseDTO userResponseDTO = userService.update(userUpdateRequestDTO, user, file);

        return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
    }

    private boolean validateAuthorities(User currentUser, Role modifyingRole, User modifyingUser) {

        Role currRole = currentUser.getRoles().iterator().next();
        Role oldRole = modifyingUser.getRoles().iterator().next();


        ERole curRoleName = currRole.getName();
        ERole modifyingRoleName = modifyingRole.getName();
        ERole oldUserRoleName = oldRole.getName();
        if (curRoleName.getValue() == 1) {
            if (currentUser.getUsername().equals(modifyingUser.getUsername())) return false;
            if (modifyingRoleName.getValue() > 1) return true;
        }
        if (curRoleName.getValue() > oldUserRoleName.getValue()) {
            return false;
        }
        if (oldUserRoleName.getValue() > modifyingRoleName.getValue()) {
            return false;
        }
        if (currentUser.getUsername().equals(modifyingUser.getUsername())) {
            return modifyingRoleName.getValue() >= oldUserRoleName.getValue();
        }
        return curRoleName.getValue() <= modifyingRoleName.getValue();
//        switch (curRoleName) {
//            case ROLE_ADMIN -> {
//                if (currentUser.getUsername().equals(modifyingUser.getUsername())) {
//                    return true;
//                }
//                else return !modifyingRoleName.equals(ERole.ROLE_ADMIN);
//            }
//            case ROLE_MODERATOR -> {
//                if (currentUser.getUsername().equals(modifyingUser.getUsername())) {
//                    return true;
//                }
//                else return !modifyingRoleName.equals(ERole.ROLE_ADMIN) && !modifyingRoleName.equals(ERole.ROLE_MODERATOR);
//            }
//            default -> {
//                return false;
//            }
//        }
    }

    @PostMapping
    public ResponseEntity<?> doCreate(@Validated UserCreateRequestDTO userCreateRequestDTO, BindingResult bindingResult) {

        new UserCreateRequestDTO().validate(userCreateRequestDTO, bindingResult);
        if (bindingResult.hasErrors()) {
//            if (!fieldError.getField().equals("file")) return appUtil.mapErrorToResponse(bindingResult);
            return appUtil.mapErrorToResponse(bindingResult);
        }

        if (userService.findByUsername(userCreateRequestDTO.getUsername()).isPresent()) {
            throw new DataInputException("User name is existed!");
        }

        if (userService.findByEmail(userCreateRequestDTO.getEmail()).isPresent()) {
            throw new DataInputException("Email is existed!");
        }

        if (userService.findByPhone(userCreateRequestDTO.getPhone()).isPresent()) {
            throw new DataInputException("Phone is existed!");
        }

        UserResponseDTO userResponseDTO = userService.create(userCreateRequestDTO);

        return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/delete/{userID}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userID) {

        Optional<User> userOptional = userService.findById(userID);

        if (userOptional.isEmpty()) {
            throw new DataInputException("User not found");
        }
        User user = userOptional.get();
        userService.delete(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    private Set<Role> getUserRole(UserUpdateRequestDTO userDTO) {
        String roleDTOs = userDTO.getRoles();
        if (roleDTOs == null) {
            throw new DataInputException("User roles is null");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Set<RoleDTO> roleDTOSet;
        try {
            roleDTOSet = objectMapper.readValue(roleDTOs, new TypeReference<Set<RoleDTO>>() {
            });
        } catch (JsonProcessingException e) {
            throw new DataInputException("Role is invalid");
        }
        Set<Role> completeRoles = new HashSet<>();
        for (RoleDTO role : roleDTOSet) {
            Optional<Role> roleOptional = roleRepository.findById(role.getId());
            if (roleOptional.isEmpty()) {
                throw new DataInputException("Role is invalid");
            }
            completeRoles.add(roleOptional.get());
        }
        return completeRoles;
    }

}
