package com.huy.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huy.exception.DataInputException;
import com.huy.model.Role;
import com.huy.model.User;
import com.huy.model.UserAvatar;
import com.huy.model.dto.RoleDTO;
import com.huy.model.dto.UserRequestDTO;
import com.huy.model.dto.UserResponseDTO;
import com.huy.repository.RoleRepository;
import com.huy.repository.UserAvatarRepositpory;
import com.huy.repository.UserRepository;
import com.huy.service.upload.IUploadService;
import com.huy.utils.UploadUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserAvatarRepositpory userAvatarRepositpory;
    @Autowired
    IUploadService uploadService;
    @Autowired
    UploadUtils uploadUtils;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;


    @Override
    public List<User> findAll() {

        return userRepository.findAll();
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }


    @Override
    public List<User> findAllByDeletedIsFalse() {
        return userRepository.findAllByDeletedIsFalse();
    }

    @Override
    public List<User> findAllByDeletedIsFalseAndUsernameNot(String username) {
        return userRepository.findAllByDeletedIsFalseAndUsernameNot(username);
    }

    @Override
    public List<User> findAllByDeletedIsFalseAndRolesNotContainsIgnoreCaseAndUsernameNot(String role, String username) {
        return userRepository.findAllByDeletedIsFalseAndRolesNotContainsIgnoreCaseAndUsernameNot(role, username);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }


    private void uploadAndSaveUserAvatar(UserRequestDTO userRequestDTO, UserAvatar userAvatar) {
        try {
            MultipartFile file = userRequestDTO.getFile();
            if (file == null || file.isEmpty()) {
                userAvatar.setFileName(UploadUtils.DEFAULT_USER_AVATAR_IMAGE);
                userAvatar.setFileUrl(UploadUtils.DEFAULT_USER_AVATAR_URL);
                userAvatar.setFileFolder(UploadUtils.IMAGE_UPLOAD_FOLDER_USER);
                userAvatar.setCloudId(UploadUtils.DEFAULT_USER_AVATAR_CLOUD_ID);
                userAvatarRepositpory.save(userAvatar);
                return;
            }
            Map uploadResult = uploadService.uploadImage(file, uploadUtils.buildImageUploadParamsUser(userAvatar));
            String fileUrl = (String) uploadResult.get("secure_url");
            String fileFormat = (String) uploadResult.get("format");

            userAvatar.setFileName(userAvatar.getId() + "." + fileFormat);
            userAvatar.setFileUrl(fileUrl);
            userAvatar.setFileFolder(UploadUtils.IMAGE_UPLOAD_FOLDER_USER);
            String cloudId = userAvatar.getFileFolder() + "/" + userAvatar.getId();
            userAvatar.setCloudId(cloudId);
            userAvatarRepositpory.save(userAvatar);
        } catch (IOException e) {
            e.printStackTrace();
            throw new DataInputException("Upload hình ảnh thất bại");
        }
    }

    @Override
    public void deleteById(Long id) {


    }

    @Override
    public void delete(User user) {

        user.setDeleted(true);
        save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException(username + "is not found");
        }
        return userOptional.get();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Override
    public UserResponseDTO create(UserRequestDTO userRequestDTO) {
        UserAvatar userAvatar = new UserAvatar();

        Set<Role> roles = getUserRole(userRequestDTO);

        userAvatarRepositpory.save(userAvatar);

        uploadAndSaveUserAvatar(userRequestDTO, userAvatar);

        User user = modelMapper.map(userRequestDTO, User.class);

        user.setId(null);
        user.setRoles(roles);
        user.setUserAvatar(userAvatar);
        save(user);

        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO update(UserRequestDTO userRequestDTO, User user) {

        user.setEmail(userRequestDTO.getEmail());
        user.setAddress(userRequestDTO.getAddress());
        user.setPhone(userRequestDTO.getPhone());
        user.setFullName(userRequestDTO.getFullName());

        UserAvatar userAvatar = user.getUserAvatar();
        MultipartFile file = userRequestDTO.getFile();

        if (file == null) {
            userRepository.save(user);
        } else {
            destroyUserImageOnCloud(user, userAvatar);
            uploadAndSaveUserAvatar(userRequestDTO, userAvatar);
        }


        UserResponseDTO userResponseDTO = modelMapper.map(user, UserResponseDTO.class);

        userResponseDTO.setRoles(user.getRoles().stream().map(role -> modelMapper.map(role, RoleDTO.class)).collect(Collectors.toSet()));

        return userResponseDTO;

    }

    private void destroyUserImageOnCloud(User user, UserAvatar userAvatar) {
        if (userAvatar.getFileName().equals(uploadUtils.DEFAULT_USER_AVATAR_IMAGE)) {
            return;
        }
        String publicId = String.format("%s/%s", userAvatar.getFileFolder(), userAvatar.getId());
        Map params = uploadUtils.buildUserImageDestroyParams(user, publicId);
        try {
            uploadService.destroyImage(publicId, params);
        } catch (IOException e) {
            e.printStackTrace();
            throw new DataInputException("Destroy image failed");
        }
    }

    private Set<Role> getUserRole(UserRequestDTO userDTO) {
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
