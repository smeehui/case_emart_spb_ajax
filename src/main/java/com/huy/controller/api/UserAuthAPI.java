package com.huy.controller.api;

import com.huy.exception.DataInputException;
import com.huy.exception.ResourceNotFoundException;
import com.huy.model.User;
import com.huy.model.dto.UserDTO;
import com.huy.model.dto.UserRequestDTO;
import com.huy.model.dto.UserResponseDTO;
import com.huy.model.jwt.JwtResponse;
import com.huy.repository.RoleRepository;
import com.huy.service.jwt.JwtService;
import com.huy.service.user.IUserService;
import com.huy.utils.AppUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class UserAuthAPI {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IUserService userService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AppUtils appUtil;
    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (username.trim().equals("") || password.trim().equals("")) {
            throw new DataInputException("User name or password is empty");
        }
        UserDetails userDetails;
        try {
            userDetails = userService.loadUserByUsername(username);
        } catch (Exception e) {
            throw new DataInputException("Username is not found");
        }
        User user = (User) userDetails;
        if (user.isDeleted()) throw new DataInputException("User is deactivated");
        String jwt = jwtService.generateToken(userDetails);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(authentication);
        } catch (AuthenticationException e) {
            throw new ResourceNotFoundException("User name or password is not correct!");
        }
        JwtResponse response = new JwtResponse (jwt,userDTO.getId(),username,authenticate.getAuthorities());

        ResponseCookie springCookie = ResponseCookie.from("jwtToken", jwt)
                .httpOnly(false)
                .secure(false)
                .path("/")
                .maxAge(60 * 1000)
                .domain("localhost")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                .body(response);
    }

    @PostMapping(path = "/register",consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> doCreate(UserRequestDTO userRequestDTO, BindingResult bindingResult) {

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
