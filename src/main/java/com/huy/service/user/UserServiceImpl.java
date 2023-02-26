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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService{
    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserAvatarRepositpory userAvatarRepositpory;


    @Autowired
    IUploadService uploadService;


    @Autowired
    UploadUtils uploadUtils;


    @Autowired
    ModelMapper modelMapper;


    @Override
    public List<User> findAll() {
        
        return userRepository.findAll();
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
            if (file == null) {
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

        userAvatarRepositpory.save(userAvatar);

        uploadAndSaveUserAvatar(userRequestDTO, userAvatar);

        Set<Role> roles  =getUserRole(userRequestDTO);
        User user = modelMapper.map(userRequestDTO, User.class);

        user.setId(null);
        user.setRoles(roles);

        user.setUserAvatar(userAvatar);

        save(user);

        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public void update(UserRequestDTO userRequestDTO, User user) {

        user.setEmail(userRequestDTO.getEmail());
        user.setAddress(userRequestDTO.getAddress());
        user.setPhone(userRequestDTO.getPhone());

        UserAvatar userAvatar = user.getUserAvatar();
        MultipartFile file = userRequestDTO.getFile();

        if (file == null) {
            userRepository.save(user);
        } else uploadAndSaveUserAvatar(userRequestDTO, userAvatar);

    }

    private Set<Role> getUserRole(UserRequestDTO userDTO) {
        String roleDTOs = userDTO.getRoles();
        if (roleDTOs == null) {
            throw new DataInputException("User roles is null");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Set<RoleDTO> roleDTOArr;
        try {
            roleDTOArr = objectMapper.readValue(roleDTOs,  new TypeReference<Set<RoleDTO>>(){});
        } catch (JsonProcessingException e) {
            throw new DataInputException("Role is invalid");
        }

        return roleDTOArr.stream().map(roleDTO -> modelMapper.map(roleDTO, Role.class)).collect(Collectors.toSet());
    }

}
